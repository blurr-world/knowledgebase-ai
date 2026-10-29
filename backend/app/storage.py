import boto3
from botocore.config import Config

from app.config import settings

s3_client = boto3.client(
    "s3",
    endpoint_url=settings.filebase_endpoint,
    aws_access_key_id=settings.filebase_access_key,
    aws_secret_access_key=settings.filebase_secret_access_key,
    region_name=settings.filebase_region,
    config=Config(
        signature_version=settings.filebase_version,
    ),
)

def upload_file(file_bytes: bytes, storage_key: str) -> None:
    s3_client.put_object(
        Bucket=settings.filebase_bucket,
        Key=storage_key,
        Body=file_bytes,
        ContentType="application/pdf"
    )

def delete_file(storage_key: str) -> None:
    s3_client.delete_object(
        Bucket=settings.filebase_bucket,
        Key=storage_key
    )

def download_file(storage_key: str) -> bytes:
    response = s3_client.get_object(
        Bucket=settings.filebase_bucket,
        Key=storage_key
    )

    return response["Body"].read()