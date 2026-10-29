def chunk_text(
    text: str,
    chunk_size: int = 800,
    overlap: int = 100
):
    if chunk_size <= 0:
        raise ValueError("chunk_size must be positive")

    if overlap < 0 or overlap >= chunk_size:
        raise ValueError("overlap must be >= 0 and < chunk_size")

    text = text.strip()

    if not text:
        return []

    chunks = []
    start = 0

    while start <= len(text):
        end = start + chunk_size
        chunk = text[start:end]

        if chunk:
            chunks.append(chunk)

        start += chunk_size - overlap 

    return chunks


    
    