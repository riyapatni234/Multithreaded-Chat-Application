import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class ChatDashboard {

    public static void show(Stage stage, String username) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("dashboard-root");

        // ===== SIDEBAR =====
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(25));
        sidebar.setPrefWidth(280);
        sidebar.getStyleClass().add("sidebar");

        Label appTitle = new Label("💬 Chat App");
        appTitle.getStyleClass().add("sidebar-title");

        Label userLabel = new Label("Welcome, " + username);
        userLabel.getStyleClass().add("sidebar-subtitle");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("sidebar-btn");

        logoutBtn.setOnAction(e -> LoginPage.show(stage));

        // ===== MAIN CHAT AREA (Define early for button handlers) =====
        VBox mainArea = new VBox(15);
        mainArea.setPadding(new Insets(25));

        Label heading = new Label("Global Chat Room");
        heading.getStyleClass().add("main-title");

        Label subHeading = new Label("Chat with all users in real-time");
        subHeading.getStyleClass().add("main-subtitle");

        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPromptText("Messages will appear here...");
        chatArea.getStyleClass().add("chat-area");
        chatArea.setPrefHeight(450);
        chatArea.setWrapText(true);

       
        
        // Global Chat starts clean
chatArea.clear();
chatArea.appendText("Welcome to Global Chat!\nStart a new conversation...\n");

        // ===== NOW SETUP SIDEBAR BUTTONS (with heading and chatArea available) =====
        
        // Global Chat button - refresh and show current chat
        Button globalChatBtn = new Button("Global Chat");
        globalChatBtn.getStyleClass().add("sidebar-btn");
        globalChatBtn.setOnAction(e -> {
    heading.setText("Global Chat Room");
    subHeading.setText("Chat with all users in real-time");
    chatArea.clear();
    chatArea.appendText("Welcome to Global Chat!\nStart a new conversation...\n");
});

        // Chat History button - show all chat history
        Button historyBtn = new Button("Chat History");
        historyBtn.getStyleClass().add("sidebar-btn");
       historyBtn.setOnAction(e -> {
    heading.setText("Chat History");
    subHeading.setText("View all messages");
    chatArea.clear();

    List<String> history = MessageDAO.getChatHistory();
    if (history.isEmpty()) {
        chatArea.appendText("No messages yet. Start chatting!\n");
    } else {
        for (String message : history) {
            chatArea.appendText(message + "\n");
        }
    }
});

        // ===== USER LISTING SECTION =====
        Label usersTitle = new Label("📱 Online Users");
        usersTitle.getStyleClass().add("sidebar-section-title");

        VBox usersList = new VBox(8);
        usersList.getStyleClass().add("users-list");
        usersList.setPadding(new Insets(10, 0, 0, 0));

        // Load users from database
        List<String> users = MessageDAO.getAllUsers();
        for (String user : users) {
            Label userItem = new Label("• " + user);
            userItem.getStyleClass().add("user-item");
            if (user.equals(username)) {
                userItem.getStyleClass().add("current-user");
            }
            usersList.getChildren().add(userItem);
        }

        ScrollPane usersScroll = new ScrollPane(usersList);
        usersScroll.setFitToWidth(true);
        usersScroll.getStyleClass().add("users-scroll");
        usersScroll.setPrefHeight(200);

        sidebar.getChildren().addAll(appTitle, userLabel, new Separator(), globalChatBtn, historyBtn, 
                                     new Separator(), usersTitle, usersScroll, new Separator(), logoutBtn);

        // ===== INPUT AND SEND MESSAGE AREA =====
        TextField messageField = new TextField();
        messageField.setPromptText("Type your message...");
        messageField.getStyleClass().add("input");

        Button sendBtn = new Button("Send");
        sendBtn.getStyleClass().add("primary-btn");

        sendBtn.setOnAction(e -> {
            String messageText = messageField.getText().trim();
            if (!messageText.isEmpty()) {
                // Save message to database
                boolean saved = MessageDAO.saveMessage(username, messageText);
                if (saved) {
                    chatArea.appendText(String.format("[%s] %s: %s\n", 
                        java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), 
                        username, 
                        messageText));
                    messageField.clear();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to send message");
                    alert.setContentText("Could not save message to database.");
                    alert.showAndWait();
                }
            }
        });

        // Allow Enter key to send message
        messageField.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                sendBtn.fire();
            }
        });

        HBox inputBox = new HBox(10, messageField, sendBtn);
        HBox.setHgrow(messageField, Priority.ALWAYS);

        mainArea.getChildren().addAll(heading, subHeading, chatArea, inputBox);

        root.setLeft(sidebar);
        root.setCenter(mainArea);

        Scene scene = new Scene(root, 1400, 800);
        scene.getStylesheets().add(ChatDashboard.class.getResource("style.css").toExternalForm());

        stage.setTitle("Chat App - Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
