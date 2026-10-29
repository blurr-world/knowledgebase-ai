from openai import OpenAI

from app.config import settings

openai_client = OpenAI(
    api_key=settings.openrouter_api_key,
    base_url="https://openrouter.ai/api/v1"
)

def create_embeddings(text: str) -> list[float]:
    response = openai_client.embeddings.create(
        input=text,
        model=settings.embedding_model
    )

    return response.data[0].embedding
