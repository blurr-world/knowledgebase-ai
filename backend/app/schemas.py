from pydantic import BaseModel, EmailStr, Field
from datetime import datetime


class UserCreate(BaseModel):
    email: EmailStr
    password: str = Field(min_length=8, max_length=128)

class RegisterResponse(BaseModel):
    success: bool
    message: str


class UserLogin(BaseModel):
    email: EmailStr
    password: str

class DocumentResponse(BaseModel):
    id: int
    filename: str
    status: str
    created_at: datetime

class RagResponse(BaseModel):
    citations: list[Citation] = Field(default_factory=list)
    answer: str | None = None
    is_success: bool
    message: str | None = None

class LLMAnswer(BaseModel):
    answer: str
    citations: list[Citation]

class Citation(BaseModel):
    document_id: int
    chunk_index: int