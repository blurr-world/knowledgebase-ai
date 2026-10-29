# Android — KnowledgeBase AI

The Android client for **KnowledgeBase AI**, an AI-powered application that allows users to upload PDF documents and ask questions about their content using AI-powered question answering.

## Tech Stack

- **Language:** Kotlin
- **UI:** XML layouts
- **IDE:** Android Studio
- **Networking:** Retrofit
- **JSON Parsing:** Gson
- **Authentication:** JWT-based API authentication

## Features

- **User Registration and Login** — Create an account and authenticate securely.
- **PDF Upload** — Select and upload PDF documents.
- **AI-Powered Question Answering** — Ask questions about uploaded document content.
- **Document Management** — View uploaded documents and delete them when needed.
- **Authenticated API Requests** — Communicate with the backend using JWT authentication.

## Getting Started

### Prerequisites

- Android Studio
- A running KnowledgeBase AI backend or a deployed API endpoint
- An Android emulator or physical Android device

### Setup Instructions

1. **Open the project.**

   Open the `android/` directory in Android Studio.

2. **Sync Gradle.**

   Allow Android Studio to sync the project and download the required dependencies.

3. **Configure the API base URL.**

   Update the API base URL to point to your local backend or deployed Render service.

   Ensure the URL is reachable from the emulator or physical device.

4. **Run the application.**

   Select an emulator or connect an Android device, then build and run the app from Android Studio.

## Configuration Notes

- For Android Emulator access to a backend running on your development machine, use `10.0.2.2` instead of `localhost` when connecting to the host machine.
- For a physical Android device, use your computer's reachable local IP address if the backend is running locally, or use your deployed API URL.
- Ensure the backend is running and accessible before testing the application.
- Use HTTPS for deployed APIs and avoid exposing sensitive credentials in source code.

## Security

- Keep API keys, signing keys, and other sensitive credentials out of version control.
- Store and transmit JWT tokens securely.
- Ensure authenticated requests include the required authorization headers.
- Never commit local configuration files containing secrets.

## Related Documentation

For backend setup, environment variables, and API configuration, refer to the main project README and `backend/README.md`.
