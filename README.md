# Multithreaded Chat Application

A real-time chat application built with Java, JavaFX, and PostgreSQL. Connect multiple users and send messages simultaneously!

## 📋 Requirements

- **Java 25** or higher
- **JavaFX SDK 25**
- **PostgreSQL 15+**
- **Windows** (PowerShell)

## 🚀 Quick Start

### 1. Database Setup (One-time)

PostgreSQL should already be running. The database and tables are automatically created when you first run the application.

### 2. Run the Server

Open PowerShell and run:

```powershell
cd d:\Projects\Multithreaded-Chat-Application
java -cp ".;postgresql.jar" chatapp.server.ServerMain
```

The server will start on `localhost:5555`

### 3. Run the Client

Open a **new** PowerShell window and run:

```powershell
cd d:\Projects\Multithreaded-Chat-Application
java --module-path "C:\Program Files\Java\jdk-25.0.2\javafx-sdk-25.0.2\lib" --add-modules javafx.controls,javafx.fxml -cp . chatapp.client.ClientMain
```

A login window will appear.

## 💬 How to Use

1. **Register**: Create a new account with username and password
2. **Login**: Login with your credentials
3. **Chat**: 
   - Select a user from the list on the left to send private messages
   - Select "ALL" to send broadcast messages to everyone
   - Type your message and click "Send"
4. **Multiple Clients**: Repeat step 3 in new terminal windows to chat between multiple users

## 🎯 Example Flow

### Terminal 1 - Start Server
```powershell
cd d:\Projects\Multithreaded-Chat-Application
java -cp ".;postgresql.jar" chatapp.server.ServerMain
```
*(Server will run in background)*

### Terminal 2 - User 1
```powershell
cd d:\Projects\Multithreaded-Chat-Application
java --module-path "C:\Program Files\Java\jdk-25.0.2\javafx-sdk-25.0.2\lib" --add-modules javafx.controls,javafx.fxml -cp . chatapp.client.ClientMain
```
- Register: `alice` / `password123`
- Login

### Terminal 3 - User 2
```powershell
cd d:\Projects\Multithreaded-Chat-Application
java --module-path "C:\Program Files\Java\jdk-25.0.2\javafx-sdk-25.0.2\lib" --add-modules javafx.controls,javafx.fxml -cp . chatapp.client.ClientMain
```
- Register: `bob` / `password123`
- Login
- Send messages to alice!

## 📁 Project Structure

```
chatapp/
├── client/          # Client application code
├── server/          # Server application code
├── database/        # Database connection and DAO
├── model/           # Data models (Message, User)
└── ui/              # JavaFX UI components
```

## 🔧 Configuration

Edit `config.properties` to change:
- Server host/port
- Database connection details
- Credentials

## ✨ Features

- ✅ User registration and login
- ✅ Broadcast messages (send to all)
- ✅ Private messages (send to specific user)
- ✅ Real-time messaging
- ✅ Message history
- ✅ Online user list
- ✅ Modern dark theme UI
- ✅ Multiple concurrent users

## 🗄️ Database

- PostgreSQL database: `chatapp`
- Tables: `users` (authentication), `messages` (message history)
- Passwords stored in plain text

## 📝 Commands Summary

| What | Command |
|------|---------|
| Start Server | `java -cp ".;postgresql.jar" chatapp.server.ServerMain` |
| Start Client | `java --module-path "C:\Program Files\Java\jdk-25.0.2\javafx-sdk-25.0.2\lib" --add-modules javafx.controls,javafx.fxml -cp . chatapp.client.ClientMain` |

## 🐛 Troubleshooting

**"Username taken or database error"**
- Make sure PostgreSQL is running
- Server is running first before client
- Check `config.properties` database credentials

**"Connection failed"**
- Verify server is running on port 5555
- Check firewall settings

**"Module not found"**
- Ensure JavaFX path is correct: `C:\Program Files\Java\jdk-25.0.2\javafx-sdk-25.0.2\lib`

## 🎓 Learning Points

This project demonstrates:
- Multithreading with ExecutorService
- Client-server architecture
- JavaFX GUI development
- JDBC database connectivity
- Real-time message protocols
- Concurrent user management