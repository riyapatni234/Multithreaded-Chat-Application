package chatapp.ui;

import chatapp.client.ChatClient;
import chatapp.model.Message;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChatView {
    private final ChatClient client;
    private final BorderPane root;
    private final ListView<String> userList;
    private final VBox messages;
    private final ScrollPane scrollPane;
    private final TextField input;
    private final Label header;
    private final Scene scene;
    private String username;

    public ChatView(ChatClient client) {
        this.client = client;
        root = new BorderPane();
        userList = new ListView<>();
        userList.getItems().add("ALL");
        userList.getSelectionModel().selectFirst();

        messages = new VBox(8);
        messages.setPadding(new Insets(12));
        scrollPane = new ScrollPane(messages);
        scrollPane.setFitToWidth(true);

        input = new TextField();
        input.setPromptText("Type a message...");
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());
        input.setOnAction(e -> sendMessage());

        HBox inputBar = new HBox(10, input, sendButton);
        inputBar.setPadding(new Insets(12));
        HBox.setHgrow(input, Priority.ALWAYS);

        header = new Label();
        Button logout = new Button("Logout");
        logout.setOnAction(e -> client.disconnect());
        HBox topBar = new HBox(10, header, logout);
        topBar.setPadding(new Insets(12));
        topBar.setAlignment(Pos.CENTER_LEFT);

        root.setLeft(userList);
        root.setCenter(scrollPane);
        root.setBottom(inputBar);
        root.setTop(topBar);
        BorderPane.setMargin(userList, new Insets(12));

        scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(Paths.get("chatapp/ui/styles.css").toUri().toString());
    }

    public Scene getScene() {
        return scene;
    }

    public void setUsername(String username) {
        this.username = username;
        Platform.runLater(() -> header.setText("Logged in as " + username));
    }

    public void addHistory(List<Message> history) {
        for (Message message : history) {
            addMessage(message);
        }
    }

    public void addMessage(Message message) {
        Platform.runLater(() -> {
            String timestamp = message.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm"));
            String target = message.getReceiver();
            String meta = message.getSender() + ("ALL".equalsIgnoreCase(target) ? "" : " → " + target) + " • "
                    + timestamp;
            Label metaLabel = new Label(meta);
            metaLabel.getStyleClass().add("meta");

            Label content = new Label(message.getContent());
            content.setWrapText(true);
            content.getStyleClass().add("message-content");
            // Set max width so bubbles don't stretch fully across the screen
            content.setMaxWidth(500);

            VBox bubble = new VBox(4, metaLabel, content);
            boolean isSelf = message.getSender().equals(username);
            bubble.getStyleClass().add(isSelf ? "bubble-self" : "bubble-other");

            // Align bubble: self to the right, others to the left
            HBox row = new HBox(bubble);
            row.setAlignment(isSelf ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            messages.getChildren().add(row);
            scrollPane.setVvalue(1.0);
        });
    }

    public void updateUsers(List<String> users) {
        Platform.runLater(() -> {
            String currentSelection = userList.getSelectionModel().getSelectedItem();
            userList.getItems().setAll("ALL");
            for (String user : users) {
                if (!user.equals(username)) {
                    userList.getItems().add(user);
                }
            }
            if (currentSelection != null && userList.getItems().contains(currentSelection)) {
                userList.getSelectionModel().select(currentSelection);
            } else {
                userList.getSelectionModel().selectFirst();
            }
        });
    }

    private void sendMessage() {
        String text = input.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        String target = Optional.ofNullable(userList.getSelectionModel().getSelectedItem()).orElse("ALL");
        if ("ALL".equalsIgnoreCase(target)) {
            client.sendBroadcast(text);
        } else {
            client.sendPrivate(target, text);
        }
        input.clear();
    }
}
