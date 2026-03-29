package chatapp.client;

import chatapp.model.Message;
import chatapp.ui.ChatView;
import chatapp.ui.LoginView;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ClientMain extends Application {
    private ChatClient client;
    private LoginView loginView;
    private ChatView chatView;

    @Override
    public void start(Stage stage) {
        Properties config = loadConfig();
        String host = config.getProperty("server.host", "localhost");
        int port = Integer.parseInt(config.getProperty("server.port", "5555"));
        client = new ChatClient(host, port);
        loginView = new LoginView(client);
        chatView = new ChatView(client);
        client.setEvents(new ChatClient.ClientEvents() {
            @Override
            public void onRegister(boolean success, String reason) {
                loginView.setStatus(success ? "Registered. You can log in." : reason, !success);
            }

            @Override
            public void onLogin(boolean success, String username, String reason) {
                if (success) {
                    chatView.setUsername(username);
                    Platform.runLater(() -> stage.setScene(chatView.getScene()));
                } else {
                    loginView.setStatus(reason, true);
                }
            }

            @Override
            public void onMessage(Message message) {
                chatView.addMessage(message);
            }

            @Override
            public void onHistory(List<Message> messages) {
                chatView.addHistory(messages);
            }

            @Override
            public void onUserList(List<String> users) {
                chatView.updateUsers(users);
            }

            @Override
            public void onError(String error) {
                loginView.setStatus(error, true);
            }

            @Override
            public void onDisconnect() {
                Platform.runLater(() -> {
                    stage.setScene(loginView.getScene());
                    loginView.setStatus("Disconnected", true);
                });
            }
        });
        stage.setScene(loginView.getScene());
        stage.setTitle("Chat Client");
        stage.show();
        stage.setOnCloseRequest(e -> client.disconnect());
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Could not load config.properties: " + e.getMessage());
        }
        return props;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
