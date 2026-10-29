
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    app_name: str
    debug: bool

    db_host: str
    db_port: int
    db_name: str
    db_user: str
    db_password: str

    jwt_secret_key: str
    jwt_algorithm: str
    access_token_expire_minutes: int

    filebase_endpoint: str
    filebase_access_key: str
    filebase_secret_access_key: str
    filebase_region: str
    filebase_version: str
    filebase_bucket: str

    qdrant_url: str
    qdrant_api_key: str

    openrouter_api_key: str
    embedding_model: str

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
    )


settings = Settings()
