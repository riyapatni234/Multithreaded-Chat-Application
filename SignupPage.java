import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SignupPage {

    public static void show(Stage stage) {
        Label title = new Label("Create Account");
        title.getStyleClass().add("title");

        Label subtitle = new Label("Sign up to join the app");
        subtitle.getStyleClass().add("subtitle");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose Username");
        usernameField.getStyleClass().add("input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create Password");
        passwordField.getStyleClass().add("input");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.getStyleClass().add("input");

        Label message = new Label();
        message.getStyleClass().add("error-label");

        Button signupBtn = new Button("Sign Up");
        signupBtn.getStyleClass().add("primary-btn");

        Button loginBtn = new Button("Already have an account? Sign In");
        loginBtn.getStyleClass().add("secondary-btn");

        signupBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                message.setText("Please fill all fields.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                message.setText("Passwords do not match.");
                return;
            }

            if (UserDAO.registerUser(username, password)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Signup successful! Please sign in.");
                alert.showAndWait();

                LoginPage.show(stage);
            } else {
                message.setText("Username may already exist.");
            }
        });

        loginBtn.setOnAction(e -> LoginPage.show(stage));

        VBox root = new VBox(15, title, subtitle, usernameField, passwordField, confirmPasswordField, signupBtn, loginBtn, message);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("auth-root");

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(SignupPage.class.getResource("style.css").toExternalForm());

        stage.setTitle("Chat App - Signup");
        stage.setScene(scene);
        stage.show();
    }
}
