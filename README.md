\# KnowledgeBase AI



An AI-powered Android application that lets users upload PDF documents and ask questions about their content using retrieval-augmented generation (RAG).



\## Features



\- User registration and JWT authentication

\- PDF upload and document processing

\- AI-powered question answering with citations

\- Secure, user-specific document access

\- Document management



\## Tech Stack



\- \*\*Android:\*\* Kotlin, XML

\- \*\*Backend:\*\* Python, FastAPI, SQLAlchemy

\- \*\*Database:\*\* MySQL (Aiven)

\- \*\*Vector Database:\*\* Qdrant Cloud

\- \*\*Object Storage:\*\* Filebase

\- \*\*AI:\*\* OpenRouter



\## Project Structure



\- `backend/` — FastAPI application and tests

\- `android/` — Android Studio project



\## Getting Started



1\. Clone the repository.

2\. Configure the backend environment variables using `backend/.env.example`.

3\. Install backend dependencies and run the API.

4\. Open `android/` in Android Studio and run the app.



See the README files in `backend/` and `android/` for setup instructions.



\## Deployment



The backend can be deployed to Render. The Android application connects to the deployed API.



