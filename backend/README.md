# Backend — KnowledgeBase AI

The FastAPI backend for **KnowledgeBase AI**, responsible for user authentication, PDF document management, and AI-powered question answering using Retrieval-Augmented Generation (RAG).

## Tech Stack

- **Language:** Python
- **API Framework:** FastAPI
- **ORM:** SQLAlchemy
- **Database:** MySQL
- **Authentication:** JWT
- **Vector Database:** Qdrant Cloud
- **Object Storage:** Filebase
- **AI Integration:** OpenRouter
- **ASGI Server:** Uvicorn
- **Deployment:** Render

## Getting Started

### Prerequisites

Before setting up the backend, ensure you have:

- Python installed
- Access to a MySQL database
- Configured credentials for Qdrant Cloud, Filebase, and OpenRouter
- Git installed

### Setup Instructions

1. **Navigate to the backend directory.**

   ```bash
   cd backend
   ```

2. **Create a virtual environment.**

   ```bash
   python -m venv venv
   ```

3. **Activate the virtual environment.**

   On Windows:

   ```bash
   venv\Scripts\activate
   ```

   On macOS or Linux:

   ```bash
   source venv/bin/activate
   ```

4. **Install dependencies.**

   ```bash
   pip install -r requirements.txt
   ```

5. **Configure environment variables.**

   Create a `.env` file in the `backend/` directory using `.env.example` as a reference, if that example file is available.

   Add the required configuration values for the database, JWT authentication, Qdrant Cloud, Filebase, and OpenRouter.

6. **Start the development server.**

   ```bash
   uvicorn app.main:app --reload
   ```

   The API will typically be available at `http://127.0.0.1:8000`.

## API Documentation

Once the backend server is running, access the following endpoints:

| Resource | URL | Description |
|---|---|---|
| Swagger UI | `http://127.0.0.1:8000/docs` | Interactive API documentation |
| ReDoc | `http://127.0.0.1:8000/redoc` | Alternative API documentation |
| Health Check | `http://127.0.0.1:8000/` | Root endpoint |

The health check URL assumes the root endpoint is configured to respond to requests.

## Deployment

The backend can be deployed to [Render](https://render.com/).

Before deploying:

1. Configure all required environment variables in the Render dashboard.
2. Ensure the MySQL database and external services are accessible.
3. Configure the appropriate start command for the deployment environment.
4. Verify that the deployed API and its required endpoints are working correctly.

A typical Uvicorn start command is:

```bash
uvicorn app.main:app --host 0.0.0.0 --port $PORT
```

Use this command if it matches your Render service configuration.

## Security

- Never commit `.env` files, credentials, API keys, or private signing keys.
- Use environment variables to manage sensitive configuration.
- Protect API endpoints with JWT authentication where required.
- Enforce user-specific access controls for uploaded documents.
- Use HTTPS for production API traffic.

## Troubleshooting

- **Dependency errors:** Ensure your virtual environment is activated and dependencies are installed.
- **Database connection errors:** Verify your MySQL connection settings and database availability.
- **AI or vector search errors:** Check your OpenRouter and Qdrant Cloud credentials and configuration.
- **File upload errors:** Verify your Filebase configuration and storage permissions.
- **Android connection errors:** Ensure the API is reachable from your emulator or device and that the Android client uses the correct base URL.

## Related Documentation

For Android setup and application usage, refer to `android/README.md` and the main project README.
