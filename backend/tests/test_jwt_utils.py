import pytest

from app.token import create_access_token, decode_access_token


def test_created_access_token_can_be_decoded():
    token = create_access_token(user_id=123)
    payload = decode_access_token(token)

    assert payload["sub"] == "123"


def test_decode_access_token_rejects_invalid_token():
    with pytest.raises(Exception):
        decode_access_token("not.a.valid.jwt")
