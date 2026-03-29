package chatapp.server;

import chatapp.database.UserDAO;

public class ServerMain {
    public static void main(String[] args) {
        int port = 5555;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        ChatServer server = new ChatServer(port, new UserDAO());
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.start();
    }
}
