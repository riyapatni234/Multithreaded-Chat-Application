package chatapp.database;

import chatapp.model.Message;
import chatapp.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Optional<User> authenticate(String username, String password) {
        String sql = "SELECT id, password FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password");
                    if (password.equals(stored)) {
                        int id = rs.getInt("id");
                        return Optional.of(new User(id, username, stored));
                    }
                }
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public void saveMessage(Message message) {
        String sql = "INSERT INTO messages(sender, receiver, message, timestamp) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, message.getSender());
            ps.setString(2, message.getReceiver());
            ps.setString(3, message.getContent());
            ps.setTimestamp(4, Timestamp.valueOf(message.getTimestamp()));
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    public List<Message> fetchRecentMessages(String username, int limit) {
        String sql = "SELECT sender, receiver, message, timestamp FROM messages WHERE receiver = ? OR receiver = ? OR sender = ? ORDER BY timestamp DESC LIMIT ?";
        List<Message> messages = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, "ALL");
            ps.setString(3, username);
            ps.setInt(4, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String receiver = rs.getString("receiver");
                    String content = rs.getString("message");
                    LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                    messages.add(new Message(sender, receiver, content, timestamp));
                }
            }
        } catch (SQLException ignored) {
        }
        Collections.reverse(messages);
        return messages;
    }

}
