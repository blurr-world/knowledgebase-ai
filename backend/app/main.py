from fastapi import FastAPI, HTTPException, Depends, status, File, UploadFile

from sqlalchemy.orm import Session

from app.models import User, Document
from app.config import settings
from app.dependencies import get_db
from app.security import hash_password, verify_password
from app.schemas import UserCreate, UserLogin, DocumentResponse, RagResponse, RegisterResponse
from app.token import create_access_token
from app.auth import get_current_user
from app.pdf_utils import validate_pdf, extract_text
from app.storage import upload_file, delete_file, download_file
from app.chunking import chunk_text
from app.vector_db import store_chunks, delete_chunks
from app.rag import generate_answer

import uuid

app = FastAPI(
    title=settings.app_name,
    debug=settings.debug
)

@app.get("/")
def home():
    return {"message": f"{settings.app_name} API is running"}


@app.get("/me")
def get_me(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    documents = (
        db.query(Document)
        .filter(Document.user_id == current_user.id)
        .order_by(Document.created_at.desc())
        .all()
    )
    
    return {
        "id": current_user.id,
        "email": current_user.email,
        "total_documents": len(documents)
    }


@app.post("/register", response_model=RegisterResponse, status_code=status.HTTP_201_CREATED)
def register(
    user_data: UserCreate,
    db: Session = Depends(get_db)
):
    existing_user = (
        db.query(User)
        .filter(User.email == user_data.email)
        .first()
    )

    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Email is already registered"
        )

    user = User(
        email = str(user_data.email),
        hashed_password = hash_password(user_data.password)
    )

    db.add(user)
    db.commit()
    db.refresh(user)

    return {
        "success": True,
        "message": "Account created successfully. Please log in.",
    }


@app.post("/login")
def login(
    user_data: UserLogin,
    db: Session = Depends(get_db)
):
    existing_user = (
        db.query(User)
        .filter(User.email == user_data.email)
        .first()
    )

    if not existing_user or not verify_password(
        user_data.password,
        existing_user.hashed_password
    ):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid email or password"
        )

    access_token = create_access_token(existing_user.id)

    return {
        "access_token": access_token,
        "token_type": "bearer",
        "user": {
            "id": existing_user.id,
            "email": existing_user.email
        }
    }


@app.get("/documents", response_model=list[DocumentResponse])
def list_documents(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    documents = (
        db.query(Document)
        .filter(Document.user_id == current_user.id)
        .order_by(Document.created_at.desc())
        .all()
    )

    return documents


@app.post(
    "/documents",
    response_model=DocumentResponse,
    status_code=status.HTTP_201_CREATED,
)
def upload_document(
    file: UploadFile = File(...),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    if not file.filename or not file.filename.lower().endswith(".pdf"):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Only PDF files are allowed",
        )

    pdf_bytes = file.file.read()

    if not validate_pdf(pdf_bytes):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid file or file exceeds the 10 MB limit",
        )

    storage_key = f"{current_user.id}/{uuid.uuid4()}.pdf"

    try:
        upload_file(pdf_bytes, storage_key)
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail="Failed to upload PDF to storage",
        )

    document = Document(
        user_id=current_user.id,
        filename=file.filename,
        storage_key=storage_key,
        status="processing",
    )

    db.add(document)
    db.commit()
    db.refresh(document)

    try:
        text = extract_text(pdf_bytes)
        chunks = chunk_text(text)

        if not chunks:
            raise ValueError("No extractable text found in PDF")

        store_chunks(
            chunks,
            current_user.id,
            document.id,
        )

        document.status = "ready"
        db.commit()
        db.refresh(document)

    except Exception as exc:
        db.rollback()

        failed_document = db.get(Document, document.id)
        if failed_document is not None:
            failed_document.status = "failed"
            db.commit()

        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="PDF uploaded, but processing failed",
        ) from exc

    return document



@app.delete("/documents/{document_id}", status_code=status.HTTP_200_OK)
def delete_document(
    document_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    document = (
        db.query(Document)
        .filter(
            Document.id == document_id,
            Document.user_id == current_user.id
        )
        .first()
    )


    if document is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Document not found"
        )

    try:
        delete_chunks(current_user.id, document_id)
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail="Failed to delete document chunks"
        )

    try:
        delete_file(document.storage_key)
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail="Failed to delete PDF from storage"
        )

    db.delete(document)
    db.commit()

    return {
        "deleted": True 
    }


@app.post("/documents/{document_id}/retry")
def retry_store_chunk(
    document_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):

    document = (
        db.query(Document)
        .filter(
            Document.id == document_id,
            Document.user_id == current_user.id
        )
        .first()
    )


    if document is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Document not found"
        )

    if document.status != "failed":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Only failed documents can be retried"
        )

    try:
        pdf_bytes = download_file(document.storage_key)
        
        text = extract_text(pdf_bytes)
        chunks = chunk_text(text)

        delete_chunks(current_user.id, document.id)

        store_chunks(
            chunks,
            current_user.id,
            document.id
        )
        failed_document = db.get(Document, document.id)
        failed_document.status = "ready"
        db.commit()
        db.refresh(failed_document)
    except Exception as exc:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="PDF uploaded, but processing failed",
        ) from exc

    return document


@app.post(
    "/ask",
    response_model=RagResponse,
    status_code=status.HTTP_200_OK,
)
def ask_llm(
    question: str,
    document_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    document = (
        db.query(Document)
        .filter(
            Document.id == document_id,
            Document.user_id == current_user.id,
        )
        .first()
    )

    if document is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Document not found",
        )

    return generate_answer(
        question,
        current_user.id,
        document_id,
    )

    
    
    
            