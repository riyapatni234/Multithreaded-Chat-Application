package chatapp.model;

import java.time.LocalDateTime;

public class Message {
    private final int id;
    private final String sender;
    private final String receiver;
    private final String content;
    private final LocalDateTime timestamp;

    public Message(int id, String sender, String receiver, String content, LocalDateTime timestamp) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Message(String sender, String receiver, String content, LocalDateTime timestamp) {
        this(0, sender, receiver, content, timestamp);
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
