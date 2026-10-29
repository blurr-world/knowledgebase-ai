import io
import pytest
from fastapi.testclient import TestClient

from app.main import app


@pytest.fixture
def client():
    """API client. Tests that need a database/service should override dependencies or mock them."""
    with TestClient(app) as test_client:
        yield test_client


@pytest.fixture
def valid_pdf_bytes():
    """Create a tiny valid PDF in memory using PyMuPDF."""
    import fitz

    doc = fitz.open()
    page = doc.new_page()
    page.insert_text((72, 72), "Knowledgebase test document. The answer is blue.")
    data = doc.tobytes()
    doc.close()
    return data
