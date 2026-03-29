package chatapp.ui;

import chatapp.client.ChatClient;
import java.io.IOException;
import java.nio.file.Paths;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginView {
    private final ChatClient client;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Label statusLabel;
    private final Scene scene;

    public LoginView(ChatClient client) {
        this.client = client;
        BorderPane root = new BorderPane();
        VBox form = new VBox(12);
        form.setPadding(new Insets(40));
        form.setAlignment(Pos.CENTER);

        Label title = new Label("Chat Login");
        title.getStyleClass().add("title");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        HBox actions = new HBox(10, loginButton, registerButton);
        actions.setAlignment(Pos.CENTER);

        statusLabel = new Label();
        statusLabel.getStyleClass().add("status");

        form.getChildren().addAll(title, usernameField, passwordField, actions, statusLabel);
        root.setCenter(form);

        scene = new Scene(root, 420, 320);
        scene.getStylesheets().add(Paths.get("chatapp/ui/styles.css").toUri().toString());

        loginButton.setOnAction(e -> submitLogin());
        registerButton.setOnAction(e -> submitRegister());
    }

    public Scene getScene() {
        return scene;
    }

    public String getUsernameInput() {
        return usernameField.getText();
    }

    public void setStatus(String text, boolean error) {
        Platform.runLater(() -> {
            statusLabel.setText(text);
            statusLabel.getStyleClass().removeIf(s -> s.equals("error"));
            if (error) {
                statusLabel.getStyleClass().add("error");
            }
        });
    }

    private void submitLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();
        if (user.isEmpty() || pass.isEmpty()) {
            setStatus("Enter credentials", true);
            return;
        }
        setStatus("Connecting...", false);
        new Thread(() -> {
            try {
                if (!client.isConnected()) {
                    client.connect();
                }
                client.login(user, pass);
            } catch (IOException ex) {
                setStatus("Connection failed", true);
            }
        }).start();
    }

    private void submitRegister() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();
        if (user.isEmpty() || pass.isEmpty()) {
            setStatus("Enter credentials", true);
            return;
        }
        setStatus("Registering...", false);
        new Thread(() -> {
            try {
                if (!client.isConnected()) {
                    client.connect();
                }
                client.register(user, pass);
            } catch (IOException ex) {
                setStatus("Connection failed", true);
            }
        }).start();
    }
}
