package demo;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.UUID;

public class Player {

    private String name;
    private UUID id;
    private Session session;
    int playerIndex;

    public Player(String name, int playerIndex, Session session) {
        this.name = name;
        this.id = UUID.randomUUID();
        this.playerIndex = playerIndex;
        this.session = session;
    }

    public String getName() {
        return this.name;
    }

    public UUID getId() {
        return this.id;
    }

    public void sendMessage(String message) {
        try {
            this.session.getRemote().sendString(message);
        } catch (IOException error) {
            System.out.println(error);
        }
    }
}
