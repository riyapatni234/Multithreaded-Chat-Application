package chatapp.client;

import chatapp.model.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {
    public interface ClientEvents {
        void onRegister(boolean success, String reason);

        void onLogin(boolean success, String username, String reason);

        void onMessage(Message message);

        void onHistory(List<Message> messages);

        void onUserList(List<String> users);

        void onError(String error);

        void onDisconnect();
    }

    private final String host;
    private final int port;
    private final ExecutorService executor;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ClientListener listener;
    private ClientEvents events;
    private String username;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void setEvents(ClientEvents events) {
        this.events = events;
    }

    public void connect() throws IOException {
        if (socket != null && socket.isConnected()) {
            return;
        }
        socket = new Socket(host, port);
        socket.setKeepAlive(true);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        listener = new ClientListener(in, this);
        executor.submit(listener);
    }

    public void register(String user, String pass) {
        send("REGISTER|" + user + "|" + pass);
    }

    public void login(String user, String pass) {
        this.username = user;
        send("LOGIN|" + user + "|" + pass);
    }

    public void sendBroadcast(String content) {
        String encoded = Base64.getEncoder().encodeToString(content.getBytes());
        send("BROADCAST|" + encoded);
    }

    public void sendPrivate(String target, String content) {
        String encoded = Base64.getEncoder().encodeToString(content.getBytes());
        send("MESSAGE|" + target + "|" + encoded);
    }

    public void requestUsers() {
        send("USERS");
    }

    public void disconnect() {
        send("LOGOUT");
        close();
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    void handleServerLine(String line) {
        String[] parts = line.split("\\|", 5);
        String command = parts[0];
        if ("REGISTER_OK".equalsIgnoreCase(command)) {
            fireRegister(true, "");
        } else if ("REGISTER_FAIL".equalsIgnoreCase(command)) {
            fireRegister(false, parts.length > 1 ? parts[1] : "Registration failed");
        } else if ("LOGIN_OK".equalsIgnoreCase(command)) {
            fireLogin(true, parts.length > 1 ? parts[1] : username, "");
        } else if ("LOGIN_FAIL".equalsIgnoreCase(command)) {
            fireLogin(false, "", parts.length > 1 ? parts[1] : "Login failed");
        } else if ("MESSAGE".equalsIgnoreCase(command) && parts.length == 5) {
            Message message = parseMessage(parts[1], parts[2], parts[3], parts[4]);
            fireMessage(message);
        } else if ("HISTORY".equalsIgnoreCase(command) && parts.length == 5) {
            Message message = parseMessage(parts[1], parts[2], parts[3], parts[4]);
            fireHistory(List.of(message));
        } else if ("USERLIST".equalsIgnoreCase(command) && parts.length >= 2) {
            List<String> users = new ArrayList<>();
            if (!parts[1].isEmpty()) {
                String[] entries = parts[1].split(",");
                for (String entry : entries) {
                    if (!entry.isBlank()) {
                        users.add(entry.trim());
                    }
                }
            }
            fireUserList(users);
        } else if ("ERROR".equalsIgnoreCase(command)) {
            fireError(parts.length > 1 ? parts[1] : "Server error");
        }
    }

    private Message parseMessage(String sender, String receiver, String timestamp, String encodedContent) {
        String decoded = new String(Base64.getDecoder().decode(encodedContent));
        LocalDateTime time = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new Message(sender, receiver, decoded, time);
    }

    private void send(String payload) {
        if (out != null) {
            out.println(payload);
        }
    }

    private void close() {
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
        if (listener != null) {
            listener.stop();
        }
        if (events != null) {
            events.onDisconnect();
        }
    }

    private void fireRegister(boolean success, String reason) {
        if (events != null) {
            events.onRegister(success, reason);
        }
    }

    private void fireLogin(boolean success, String user, String reason) {
        if (events != null) {
            events.onLogin(success, user, reason);
        }
    }

    private void fireMessage(Message message) {
        if (events != null) {
            events.onMessage(message);
        }
    }

    private void fireHistory(List<Message> messages) {
        if (events != null && !messages.isEmpty()) {
            events.onHistory(messages);
        }
    }

    private void fireUserList(List<String> users) {
        if (events != null) {
            events.onUserList(users);
        }
    }

    private void fireError(String error) {
        if (events != null) {
            events.onError(error);
        }
    }
}
