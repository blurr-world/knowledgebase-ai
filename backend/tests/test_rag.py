from unittest.mock import MagicMock

import pytest

import app.rag as rag


def test_rag_returns_not_found_message_when_no_chunks(monkeypatch):
    monkeypatch.setattr(rag, "search_chunks", lambda *args, **kwargs: [])

    result = rag.generate_answer("What is in the document?", user_id=1, document_id=1)

    assert result.is_success is False
    assert result.citations == []
    assert result.message
