package chatapp.server;

import chatapp.model.Message;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class MessageFormatter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String serialize(Message message) {
        String encoded = Base64.getEncoder().encodeToString(message.getContent().getBytes());
        return String.join("|", "MESSAGE", message.getSender(), message.getReceiver(),
                FORMATTER.format(message.getTimestamp()), encoded);
    }
}
