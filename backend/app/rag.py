
from openai import OpenAI

from app.config import settings
from app.vector_db import search_chunks
from app.schemas import RagResponse, LLMAnswer, Citation

openai_client = OpenAI(
    api_key=settings.openrouter_api_key,
    base_url="https://openrouter.ai/api/v1",
)


def generate_answer(
    question: str,
    user_id: int,
    document_id: int
) -> RagResponse:

    searched_chunks = search_chunks(question, user_id, document_id, limit=2)

    current_citations = set()

    for chunk in searched_chunks:
        if chunk.payload and chunk.payload.get("text"):
            current_citations.add((
                chunk.payload["document_id"],
                chunk.payload["chunk_index"]
            ))

    if not searched_chunks:
        return RagResponse(
            answer=None,
            is_success=False,
            message="There isn't enough information in your documents.",
        )

    context = "\n\n".join(
        f"[document_id={chunk.payload['document_id']}, "
        f"chunk_index={chunk.payload['chunk_index']}]\n"
        f"{chunk.payload['text']}"
        for chunk in searched_chunks
        if chunk.payload and chunk.payload.get("text")
    )

    if not context:
        return RagResponse(
            answer=None,
            is_success=False,
            message="No usable document text was found.",
        )

    response = openai_client.chat.completions.parse(
        model="nvidia/nemotron-3-ultra-550b-a55b:free",
        response_format=LLMAnswer,
        messages=[
            {
                "role": "system",
                "content": (
                    "Return only the required response format. Answer using only the "
                    "provided context. If the context does not directly answer the question, respond only:"
                    "The provided documents do not contain the answer. Cite only document IDs and"
                    "chunk indexes that directly support the answer. Do not include "
                    "irrelevant citations or explain the retrieval process."
                    "Cite only the minimum number of chunks needed to support the answer. "
                    "Do not cite multiple chunks containing the same evidence."
                ),
            },
            {
                "role": "user",
                "content": f"Context:\n{context}\n\nQuestion:\n{question}",
            },
        ],
    )

    llm_answer = response.choices[0].message.parsed

    valid_citations = []

    for citation in llm_answer.citations:
        citation_pair = (
            citation.document_id,
            citation.chunk_index
        )

        if citation_pair in current_citations:
            valid_citations.append(
                Citation(
                    document_id=citation.document_id,
                    chunk_index=citation.chunk_index
                )
            )


    return RagResponse(
        citations=valid_citations,
        answer=llm_answer.answer,
        is_success=True,
        message=None
    )
