import pymupdf
import pytest

from app.pdf_utils import validate_pdf, extract_text, MAX_PDF_SIZE


def make_pdf(text="PDF test text"):
    doc = pymupdf.open()
    page = doc.new_page()
    page.insert_text((72, 72), text)
    data = doc.tobytes()
    doc.close()
    return data


def test_validate_pdf_accepts_valid_pdf():
    assert validate_pdf(make_pdf()) is not False


def test_validate_pdf_rejects_empty_bytes():
    assert validate_pdf(b"") is False


def test_validate_pdf_rejects_non_pdf_bytes():
    assert validate_pdf(b"this is not a PDF") is False


def test_validate_pdf_rejects_oversized_file():
    oversized = b"x" * (MAX_PDF_SIZE + 1)
    assert validate_pdf(oversized) is False


def test_extract_text_returns_page_text():
    text = extract_text(make_pdf("The answer is blue."))
    assert "The answer is blue." in text


def test_extract_text_from_empty_pdf_returns_empty_text():
    doc = pymupdf.open()
    doc.new_page()
    data = doc.tobytes()
    doc.close()

    assert extract_text(data).strip() == ""
