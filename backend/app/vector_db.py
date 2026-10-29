from qdrant_client import QdrantClient
from qdrant_client.models import Distance, VectorParams, PointStruct, PointsSelector

from qdrant_client.models import (
    Filter,
    FieldCondition,
    MatchValue,
)

from app.config import settings
from app.embeddings import create_embeddings

import uuid

collection="knowledgebase_ai"

qdrant_client = QdrantClient(
    url=settings.qdrant_url,
    api_key=settings.qdrant_api_key
)

def is_collection_exists():
    is_exists = qdrant_client.collection_exists("knowledgebase_ai")

    if not is_exists:
        qdrant_client.create_collection(
            collection_name="knowledgebase_ai",
            vectors_config=VectorParams(size=1536, distance=Distance.COSINE)
        )

    return True

def store_chunks(
    chunks: list[str],
    user_id: int,
    document_id: int
):

    points = []

    for index, chunk in enumerate(chunks):

        vector = create_embeddings(chunk)

        point = PointStruct(
            id=str(uuid.uuid4()),
            vector=vector,
            payload={
                "user_id": user_id,
                "document_id": document_id,
                "chunk_index": index,
                "text": chunk
            }
        )

        points.append(point)

    if points:
        qdrant_client.upsert(
            collection_name=collection,
            points=points
        )

def search_chunks(
    question: str, 
    user_id: int,
    document_id: int, 
    limit: int = 5
):

    query_embedding = create_embeddings(question)

    response = qdrant_client.query_points(
        query=query_embedding,
        collection_name=collection,
        limit=limit,
        query_filter=Filter(
            must=[
                FieldCondition(
                    key="user_id",
                    match=MatchValue(value=user_id)
                ),
                FieldCondition(
                    key="document_id",
                    match=MatchValue(value=document_id),
                ),
            ]
        ),
        with_payload=True
    )

    return response.points

def delete_chunks(
    user_id: int,
    document_id: int
):

    response = qdrant_client.delete(
        collection_name=collection,
        points_selector=Filter(
                must=[
                    FieldCondition(key="user_id", match=MatchValue(value=user_id)),
                    FieldCondition(key="document_id", match=MatchValue(value=document_id))
                ]
            ),
        wait=True
    )

    return response

    