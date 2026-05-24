# Chit-Chat 💬

Chit-Chat is a secure, real-time Android messaging application built with **Kotlin** and **Firebase**. It features **End-to-End Encryption (E2EE)** using a hybrid cryptographic approach to ensure that only the intended participants can read their conversations.

## ✨ Features

- **Secure Messaging:** All messages are encrypted locally before being sent to the server.
- **End-to-End Encryption:** Implements a hybrid RSA + AES encryption system.
- **Real-Time Updates:** Instant message delivery using Firebase Realtime Database.
- **User Authentication:** Secure signup and login powered by Firebase Auth.
- **User Discovery:** Easily find and start chats with other registered users.
- **Modern UI:** Built with Material Design components and efficient RecyclerView adapters.

## 🔐 Security Architecture

Chit-Chat prioritizes user privacy through a robust E2EE implementation:

1.  **Key Generation:** Upon registration, a 2048-bit RSA key pair is generated on the user's device.
2.  **Private Key:** Stored securely on the user's local device using `SharedPreferences`. It never leaves the device.
3.  **Public Key:** Uploaded to the Firebase Database, allowing other users to encrypt messages for them.
4.  **Hybrid Encryption:**
    - A unique **AES-128** session key is generated for every message.
    - The message is encrypted with the AES key.
    - The AES key is then encrypted with the recipient's **RSA Public Key**.
    - The encrypted AES key and the encrypted message are bundled and sent.
5.  **Decryption:** The recipient uses their local **RSA Private Key** to decrypt the AES key, which is then used to decrypt the message.

## 🚀 Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **Backend:** [Firebase](https://firebase.google.com/)
  - Realtime Database (Message & User Storage)
  - Authentication (Email/Password)
  - Firebase BOM
- **UI Architecture:** Material Design, XML Layouts, RecyclerView
- **Security:** Java Cryptography Architecture (JCA) - RSA, AES

## 🛠️ Setup & Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/iaadarsh/Chit-Chat.git
    ```
2.  **Firebase Setup:**
    - Create a new project in the [Firebase Console](https://console.firebase.google.com/).
    - Add an Android App with the package name `com.example.chit_chat`.
    - Download the `google-services.json` file and place it in the `app/` directory.
    - Enable **Email/Password Authentication** and **Realtime Database**.
3.  **Build the project:**
    - Open the project in **Android Studio**.
    - Sync Gradle files.
    - Run the app on an emulator or a physical device.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
*Developed  by **[Aadarsh]***

