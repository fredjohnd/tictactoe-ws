package demo;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

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

        JSONObject data = new JSONObject(message);
        data.put("playerId", this.id.toString());
        String dataWithId = String.valueOf(data);
        try {
            this.session.getRemote().sendString(dataWithId);
        } catch (IOException error) {
            System.out.println(error);
        }
    }
}
