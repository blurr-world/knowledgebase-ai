from fastapi.testclient import TestClient

from app.main import app


def test_home_returns_success():
    with TestClient(app) as client:
        response = client.get("/")
    assert response.status_code == 200


def test_documents_requires_authentication():
    with TestClient(app) as client:
        response = client.get("/documents")
    assert response.status_code in (401, 403)


def test_me_requires_authentication():
    with TestClient(app) as client:
        response = client.get("/me")
    assert response.status_code in (401, 403)


def test_documents_rejects_malformed_bearer_token():
    with TestClient(app) as client:
        response = client.get(
            "/documents",
            headers={"Authorization": "Bearer definitely-not-a-jwt"},
        )
    assert response.status_code in (401, 403)
