import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    // Save a message to the database
    public static boolean saveMessage(String senderUsername, String messageContent) {
        String query = "INSERT INTO messages (sender_username, message_content) VALUES (?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("ERROR: Could not establish database connection for saveMessage!");
            return false;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, senderUsername);
            stmt.setString(2, messageContent);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Message saved successfully!");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Failed to save message!");
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }

    // Get all chat messages (chat history)
    public static List<String> getChatHistory() {
        List<String> messages = new ArrayList<>();
        String query = "SELECT sender_username, message_content, timestamp FROM messages ORDER BY timestamp ASC";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("ERROR: Could not establish database connection for getChatHistory!");
            return messages;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("sender_username");
                String content = rs.getString("message_content");
                Timestamp timestamp = rs.getTimestamp("timestamp");

                String formattedMessage = String.format("[%s] %s: %s", 
                    timestamp.toString().substring(11, 16), 
                    username, 
                    content);
                messages.add(formattedMessage);
            }
            
            System.out.println("Successfully retrieved " + messages.size() + " messages from history");

        } catch (SQLException e) {
            System.out.println("Failed to retrieve chat history!");
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return messages;
    }

    // Get all registered users
    public static List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        String query = "SELECT username FROM users ORDER BY username ASC";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("ERROR: Could not establish database connection for getAllUsers!");
            return users;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(rs.getString("username"));
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve users!");
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return users;
    }

    // Get recent messages (last N messages)
    public static List<String> getRecentMessages(int limit) {
        List<String> messages = new ArrayList<>();
        String query = "SELECT sender_username, message_content, timestamp FROM messages ORDER BY timestamp DESC LIMIT ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("ERROR: Could not establish database connection for getRecentMessages!");
            return messages;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            List<String> tempMessages = new ArrayList<>();
            while (rs.next()) {
                String username = rs.getString("sender_username");
                String content = rs.getString("message_content");
                Timestamp timestamp = rs.getTimestamp("timestamp");

                String formattedMessage = String.format("[%s] %s: %s", 
                    timestamp.toString().substring(11, 16), 
                    username, 
                    content);
                tempMessages.add(formattedMessage);
            }

            // Reverse to show in chronological order
            for (int i = tempMessages.size() - 1; i >= 0; i--) {
                messages.add(tempMessages.get(i));
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve recent messages!");
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return messages;
    }
}
