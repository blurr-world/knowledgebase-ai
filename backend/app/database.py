from sqlalchemy import create_engine, text
from sqlalchemy.orm import DeclarativeBase, sessionmaker


from app.config import settings

DATABASE_URL = (
    f"mysql+pymysql://{settings.db_user}:{settings.db_password}"
    f"@{settings.db_host}:{settings.db_port}/{settings.db_name}"
)


engine = create_engine(
    url=DATABASE_URL,
    pool_pre_ping=True
)

SessionLocal = sessionmaker(
    bind=engine,
    autoflush=False,
)

class Base(DeclarativeBase):
    pass

