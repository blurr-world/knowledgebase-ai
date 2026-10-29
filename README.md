# KnowledgeBase AI

KnowledgeBase AI is an AI-powered Android application that allows users to upload PDF documents and ask questions about their content using **Retrieval-Augmented Generation (RAG)**. It provides relevant answers with citations, helping users find information in their documents quickly and efficiently.

## 🚀 Live API

The KnowledgeBase AI backend is deployed on Render.

- **API Documentation (Swagger UI):** [Explore API Docs](https://knowledgebase-ai.onrender.com/docs)
- **API Base URL:** https://knowledgebase-ai.onrender.com
- **APK Download:** [Release](https://github.com/blurr-world/knowledgebase-ai/releases/tag/1.0.0)

Use the interactive Swagger UI to explore endpoints and test API requests.

## Features

- **User Authentication** — User registration and JWT-based authentication.
- **PDF Upload** — Upload and process PDF documents.
- **AI-Powered Question Answering** — Ask questions about uploaded documents using RAG.
- **Cited Answers** — Receive answers with citations to relevant document content.
- **Secure Document Access** — Ensure users can access only their own documents.
- **Document Management** — Manage uploaded documents.

## Tech Stack

| Component | Technologies |
|---|---|
| Android | Kotlin, XML |
| Backend | Python, FastAPI, SQLAlchemy |
| Database | MySQL (Aiven) |
| Vector Database | Qdrant Cloud |
| Object Storage | Filebase |
| AI Integration | OpenRouter |
| Authentication | JWT |
| Deployment | Render |

## Project Structure

```text
KnowledgeBase-AI/
├── backend/    # FastAPI application and tests
└── android/    # Android Studio project
```

## Getting Started

Follow these steps to set up the project locally.

### Prerequisites

- Python installed
- Android Studio installed
- Access to the required database, vector database, object storage, and AI services

### Setup Instructions

1. **Clone the repository.**

   ```bash
   git clone https://github.com/blurr-world/knowledgebase-ai.git
   cd knowledgebase-ai
   ```

2. **Configure the backend.**

   Use the example environment file to configure the required environment variables.

   ```bash
   cd backend
   cp .env.example .env
   ```

   Update `.env` with your credentials and service configuration. Do not commit secrets or API keys to version control.

3. **Install backend dependencies and start the API.**

   Follow the instructions in `backend/README.md` to install dependencies, configure the environment, and run the FastAPI server.

4. **Set up the Android application.**

   Open the `android/` directory in Android Studio, configure the API base URL to point to your backend, and run the application on an emulator or physical device.

For detailed setup instructions, refer to the README files in the `backend/` and `android/` directories.

## Deployment

The backend can be deployed to [Render](https://render.com/). The Android application communicates with the deployed backend API.

Before deployment, ensure that all required environment variables are configured and that the database, vector database, object storage, and AI service credentials are valid.

## Security

- Protect API endpoints using JWT authentication.
- Enforce user-specific access controls for documents.
- Store credentials and API keys in environment variables.
- Never commit `.env` files or other sensitive configuration to version control.

## License

No license has been specified yet. Add a `LICENSE` file if you intend to distribute the project under an open-source license.
