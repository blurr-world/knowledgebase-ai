from datetime import datetime, timedelta, timezone


import jwt

from app.config import settings



def create_access_token(user_id: int) -> str:
    expires_at = datetime.now(timezone.utc) + timedelta (
        minutes=int(60*24*30)
    )

    payload = {
        "sub": str(user_id),
        "exp": expires_at
    }

    return jwt.encode(
        payload=payload,
        key=settings.jwt_secret_key,
        algorithm=settings.jwt_algorithm
    )

def decode_access_token(token: str) -> dict:
    return jwt.decode(
        jwt=token,
        key=settings.jwt_secret_key,
        algorithms=[settings.jwt_algorithm]
    )
