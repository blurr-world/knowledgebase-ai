# Test suite

These tests are intended for the current `knowledgebase-ai` project structure.

## Install test dependencies

```powershell
python -m pip install pytest httpx
```

The PDF tests also require the project's existing `pymupdf` dependency. API tests use FastAPI's `TestClient`.

## Run tests

Run everything:

```powershell
python -m pytest -q
```

Run one file:

```powershell
python -m pytest tests/test_chunking.py -q
```

List collected tests:

```powershell
python -m pytest --collect-only -q
```

## Important compatibility notes

- `test_security.py` assumes `app.security` exports `hash_password` and `verify_password`.
- `test_jwt_utils.py` assumes `app.jwt_utils` exports `create_access_token` and `decode_access_token`, with `create_access_token(data=...)`.
- `test_rag.py` assumes `app.rag` exports `answer_question(question, user_id=...)` and imports `search_chunks` in that module.
- The exact names/signatures in the repository may differ. If a test reports an import/signature error, update that test to match the implementation rather than changing working application code just to satisfy a guessed test.
- The suite deliberately does not connect to live MySQL, Qdrant, Filebase, or OpenRouter. Integration tests for upload/delete/retry and user-isolation should mock these services and use a dedicated test database.
- Do not put `.env`, API keys, real user data, database dumps, or private PDFs in the test archive.
