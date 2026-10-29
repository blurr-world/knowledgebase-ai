from app.security import hash_password, verify_password


def test_hash_password_does_not_return_plaintext():
    password = "correct-horse-battery-staple"
    hashed = hash_password(password)

    assert hashed
    assert hashed != password


def test_verify_password_accepts_correct_password():
    password = "correct-horse-battery-staple"
    hashed = hash_password(password)

    assert verify_password(password, hashed) is True


def test_verify_password_rejects_wrong_password():
    hashed = hash_password("correct-password")

    assert verify_password("wrong-password", hashed) is False
