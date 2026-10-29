import pymupdf


MAX_PDF_SIZE = 10 * 1024 * 1024

def validate_pdf(pdf_bytes: bytes) -> bool:
    if not pdf_bytes or len(pdf_bytes) > MAX_PDF_SIZE:
        return False

    try:
        with pymupdf.open(stream=pdf_bytes, filetype="pdf") as pdf:
            return pdf.page_count > 0
    except Exception:
        return False


def extract_text(pdf_bytes: bytes) -> str:
    with pymupdf.open(stream=pdf_bytes, filetype="pdf") as pdf:
        pages = []

        for page in pdf:
            pages.append(page.get_text())

    return "\n".join(pages).strip()


