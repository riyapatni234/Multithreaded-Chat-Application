package chatapp.server;

import chatapp.database.UserDAO;
import chatapp.model.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ChatServer server;
    private final UserDAO userDAO;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private boolean authenticated;

    public ClientHandler(Socket socket, ChatServer server, UserDAO userDAO) {
        this.socket = socket;
        this.server = server;
        this.userDAO = userDAO;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            handleConnection();
        } catch (IOException ignored) {
        } finally {
            close();
        }
    }

    private void handleConnection() throws IOException {
        while (!authenticated) {
            String line = in.readLine();
            if (line == null) {
                return;
            }
            String[] parts = line.split("\\|", 3);
            if (parts.length < 1) {
                continue;
            }
            String command = parts[0];
            if ("REGISTER".equalsIgnoreCase(command) && parts.length == 3) {
                handleRegister(parts[1], parts[2]);
            } else if ("LOGIN".equalsIgnoreCase(command) && parts.length == 3) {
                handleLogin(parts[1], parts[2]);
            } else {
                send("ERROR|Authenticate first");
            }
        }
        listen();
    }

    private void handleRegister(String user, String pass) {
        boolean success = userDAO.registerUser(user, pass);
        if (success) {
            send("REGISTER_OK");
        } else {
            send("REGISTER_FAIL|Username taken or database error");
        }
    }

    private void handleLogin(String user, String pass) {
        if (server.getOnlineUsers().contains(user)) {
            send("LOGIN_FAIL|User already online");
            return;
        }
        if (userDAO.authenticate(user, pass).isPresent()) {
            boolean added = server.registerClient(user, this);
            if (!added) {
                send("LOGIN_FAIL|User already online");
                return;
            }
            this.username = user;
            this.authenticated = true;
            send("LOGIN_OK|" + user);
            sendHistory();
            server.broadcastUserList();
            server.broadcast(ChatServer.systemMessage("ALL", user + " joined"));
        } else {
            send("LOGIN_FAIL|Invalid credentials");
        }
    }

    private void sendHistory() {
        List<Message> history = userDAO.fetchRecentMessages(username, 100);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        for (Message message : history) {
            String encoded = Base64.getEncoder().encodeToString(message.getContent().getBytes());
            String payload = String.join("|", "HISTORY", message.getSender(), message.getReceiver(),
                    formatter.format(message.getTimestamp()), encoded);
            send(payload);
        }
    }

    private void listen() throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            String[] parts = line.split("\\|", 3);
            String command = parts[0];
            if ("MESSAGE".equalsIgnoreCase(command) && parts.length == 3) {
                handlePrivate(parts[1], parts[2]);
            } else if ("BROADCAST".equalsIgnoreCase(command) && parts.length == 2) {
                handleBroadcast(parts[1]);
            } else if ("USERS".equalsIgnoreCase(command)) {
                server.broadcastUserList();
            } else if ("LOGOUT".equalsIgnoreCase(command)) {
                return;
            } else {
                send("ERROR|Unknown command");
            }
        }
    }

    private void handlePrivate(String target, String encodedContent) {
        String content = new String(Base64.getDecoder().decode(encodedContent));
        Message message = new Message(username, target, content, LocalDateTime.now());
        server.sendPrivate(target, message);
    }

    private void handleBroadcast(String encodedContent) {
        String content = new String(Base64.getDecoder().decode(encodedContent));
        Message message = new Message(username, "ALL", content, LocalDateTime.now());
        server.broadcast(message);
    }

    public void send(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void close() {
        if (username != null) {
            server.removeClient(username);
            server.broadcast(ChatServer.systemMessage("ALL", username + " left"));
        }
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
