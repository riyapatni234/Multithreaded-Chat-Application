package chatapp.server;

import chatapp.database.UserDAO;
import chatapp.model.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer {
    private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());
    private final int port;
    private final ExecutorService executor;
    private final ConcurrentHashMap<String, ClientHandler> clients;
    private final UserDAO userDAO;
    private final AtomicBoolean running;
    private ServerSocket serverSocket;

    public ChatServer(int port, UserDAO userDAO) {
        this.port = port;
        this.userDAO = userDAO;
        this.executor = Executors.newCachedThreadPool();
        this.clients = new ConcurrentHashMap<>();
        this.running = new AtomicBoolean(false);
    }

    public void start() {
        running.set(true);
        try (ServerSocket server = new ServerSocket(port)) {
            this.serverSocket = server;
            while (running.get()) {
                Socket socket = server.accept();
                socket.setKeepAlive(true);
                executor.submit(new ClientHandler(socket, this, userDAO));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server error", e);
        } finally {
            shutdown();
        }
    }

    public boolean registerClient(String username, ClientHandler handler) {
        return clients.putIfAbsent(username, handler) == null;
    }

    public void removeClient(String username) {
        clients.remove(username);
        broadcastUserList();
    }

    public void broadcast(Message message) {
        userDAO.saveMessage(message);
        String payload = MessageFormatter.serialize(message);
        clients.values().forEach(handler -> handler.send(payload));
    }

    public void sendPrivate(String target, Message message) {
        userDAO.saveMessage(message);
        ClientHandler receiver = clients.get(target);
        ClientHandler sender = clients.get(message.getSender());
        String payload = MessageFormatter.serialize(message);
        if (receiver != null) {
            receiver.send(payload);
        }
        if (sender != null && receiver != sender) {
            sender.send(payload);
        }
    }

    public void broadcastUserList() {
        List<String> users = new ArrayList<>(clients.keySet());
        Collections.sort(users);
        String payload = "USERLIST|" + String.join(",", users);
        clients.values().forEach(handler -> handler.send(payload));
    }

    public Set<String> getOnlineUsers() {
        return clients.keySet();
    }

    public void shutdown() {
        running.set(false);
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
        clients.values().forEach(ClientHandler::close);
        executor.shutdownNow();
    }

    public static Message systemMessage(String receiver, String content) {
        return new Message("SYSTEM", receiver, content, LocalDateTime.now());
    }
}
