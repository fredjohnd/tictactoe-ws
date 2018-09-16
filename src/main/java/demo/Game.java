package demo;

import jdk.nashorn.internal.parser.JSONParser;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

@WebSocket
public class Game {

    // Store sessions if you want to, for example, broadcast a message to all users
//    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    private static final GameSession[] gameSessions = {};

    @OnWebSocketConnect
    public void connected(Session session) {

        // Get query params
        Map<String, List<String>> params = session.getUpgradeRequest().getParameterMap();
        // Initiate new session

        if (params.get("gameId") != null) {
            this.joinSession(session, params);
        } else {
            this.createSession(session, params);
        }

    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {

        //sessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessageReceived(Session session, String message) throws IOException {
        JSONObject data = new JSONObject(message);
        String gameId = String.valueOf(data.get("gameId"));

        if (gameId != null) {
            System.out.println(gameId);
        }

        System.out.println("Got: " + message);
    }

    public void createSession(Session session, Map<String, List<String>> params) {

        // Check if name supplied
        if (!params.containsKey("name")) {
            String data = String.valueOf(new JSONObject().put("error", "No name specified"));
            this.sendMsg(session, data);
            return;
        }

        String playerName = params.get("name").get(0);
        Player player1 = new Player(playerName, 0, session);

        GameSession gameSession = new GameSession(player1);
        String data = gameSession.toJSON();

        this.sendMsg(session, data);
    }

    public void joinSession(Session session, Map<String, List<String>> params) {
        String gameId = params.get("gameId").get(0);
        String playerName = params.get("name").get(0);

        System.out.println(String.format("%s trying to join session with Id: %s" , playerName, gameId));

        GameSession gameSession = this.getGameSession(gameId);

        if (gameSession == null) {
            System.out.println(String.format("Session not found with Id %s", gameId));
            String data = String.valueOf(new JSONObject().put("error", "Session not found"));
            this.sendMsg(session, data);
            return;
        }

        System.out.println("Found existing session" + gameSession);

        // Add player2
        Player player2 = new Player(playerName, 1, session);
        gameSession.joinGame(player2);
        gameSession.start();

    }

    public void sendMsg(Session session, String message) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException error) {
            System.out.println(error);
        }
    }

    public GameSession getGameSession(String id) {
        Optional<GameSession> currentSessionExists = Arrays.stream(this.gameSessions).filter(s -> s.getGameId().toString().equals(id)).findFirst();
        if (currentSessionExists.isPresent()) {
            return currentSessionExists.get();
        } else {
            return null;
        }
    }

}

