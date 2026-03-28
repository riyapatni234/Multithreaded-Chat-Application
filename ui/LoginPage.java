import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage {

    public static void show(Stage stage) {
        Label title = new Label("Welcome Back");
        title.getStyleClass().add("title");

        Label subtitle = new Label("Sign in to continue");
        subtitle.getStyleClass().add("subtitle");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input");

        Label message = new Label();
        message.getStyleClass().add("error-label");

        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("primary-btn");

        Button signupBtn = new Button("Create Account");
        signupBtn.getStyleClass().add("secondary-btn");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                message.setText("Please fill all fields.");
                return;
            }

            if (UserDAO.loginUser(username, password)) {
                ChatDashboard.show(stage, username);
            } else {
                message.setText("Invalid username or password.");
            }
        });

        signupBtn.setOnAction(e -> SignupPage.show(stage));

        VBox root = new VBox(15, title, subtitle, usernameField, passwordField, loginBtn, signupBtn, message);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("auth-root");

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(LoginPage.class.getResource("/ui/style.css").toExternalForm());

        stage.setTitle("Chat App - Login");
        stage.setScene(scene);
        stage.show();
    }
}
