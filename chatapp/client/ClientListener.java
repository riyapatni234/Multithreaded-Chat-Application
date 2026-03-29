package chatapp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientListener implements Runnable {
    private final BufferedReader in;
    private final ChatClient client;
    private final AtomicBoolean running;

    public ClientListener(BufferedReader in, ChatClient client) {
        this.in = in;
        this.client = client;
        this.running = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        try {
            String line;
            while (running.get() && (line = in.readLine()) != null) {
                client.handleServerLine(line);
            }
        } catch (IOException ignored) {
        } finally {
            if (running.get()) {
                client.disconnect();
            }
        }
    }

    public void stop() {
        running.set(false);
    }
}
