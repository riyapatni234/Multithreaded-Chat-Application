import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Enter your name:");
            clientName = in.readLine();
            broadcast(clientName + " joined the chat!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message;

        try {
            while ((message = in.readLine()) != null) {

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }

                if (message.startsWith("@")) {
                    sendPrivateMessage(message);
                } else {
                    broadcast(clientName + ": " + message);
                }
            }
        } catch (IOException e) {
            System.out.println("Connection lost: " + clientName);
        } finally {
            removeClient();
        }
    }

   
    private void broadcast(String msg) {
        for (ClientHandler client : ChatServer.clients) {
            client.out.println(msg);
        }
    }

    private void sendPrivateMessage(String msg) {
        String[] parts = msg.split(" ", 2);
        String targetName = parts[0].substring(1);
        String message = (parts.length > 1) ? parts[1] : "";

        for (ClientHandler client : ChatServer.clients) {
            if (client.clientName.equals(targetName)) {
                client.out.println("(Private) " + clientName + ": " + message);
                return;
            }
        }
        System.out.println("User not found!");
    }

    private void removeClient() {
        try {
            ChatServer.clients.remove(this);
            broadcast(clientName + " left the chat!");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}