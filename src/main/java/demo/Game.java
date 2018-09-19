package demo;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import org.json.JSONObject;

import java.io.*;
import java.util.*;


@WebSocket
public class Game {

    private List<GameSession> gameSessions = new ArrayList<GameSession>();

    @OnWebSocketConnect
    public void connected(Session session) {

        GameParams params = new GameParams(session);

        if (params.gameId != null) {
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
        String playerId = data.get("playerId").toString();

        if (gameId != null) {
            GameSession gameSession = this.getGameSession(gameId);

            if (gameSession == null) {
                System.out.println("Game session not found");
                return;
            }

            Player player = gameSession.getPlayerById(playerId);

            if (player == null) {
                System.out.println("Player doesn't exist in session");
                return;
            }

            if (!gameSession.isBothPlayersJoined()) {
                player.sendMessage(Actions.WAITING_PLAYER, new JSONObject());
                return;
            }

            if (this.isAction(Actions.PLAY, data.get("action"))) {
                int moveIndex = Integer.parseInt(data.get("move").toString());
                gameSession.makeMove(player, moveIndex);
            } else if (this.isAction(Actions.RESTART, data.get("action"))) {
                gameSession.restart();
            }
        }

        System.out.println("Got: " + message);
    }

    public void createSession(Session session, GameParams params) {

        // Check if name supplied
        if (params.name.isEmpty()) {
            String data = String.valueOf(new JSONObject().put("error", "No name specified"));
            this.sendSessionMessage(session, data);
            return;
        }

        String playerName = params.name;
        Player player1 = new Player(playerName, 0, session);

        GameSession gameSession = new GameSession(player1);
        this.gameSessions.add(gameSession);

        JSONObject data = gameSession.toJSONObject();
        player1.sendMessage(Actions.SESSION_CREATED, data);

    }

    public void joinSession(Session session, GameParams params) {
        String gameId = params.gameId;
        String playerName = params.name;

        System.out.println(String.format("%s trying to join session with Id: %s" , playerName, gameId));

        GameSession gameSession = this.getGameSession(gameId);

        if (gameSession == null) {
            System.out.println(String.format("Session not found with Id %s", gameId));
            String data = String.valueOf(new JSONObject().put("error", "Session not found"));
            this.sendSessionMessage(session, data);
            return;
        }

        System.out.println("Found existing session" + gameSession);

        // Add player2
        Player player2 = new Player(playerName, 1, session);
        gameSession.joinGame(player2);
        gameSession.start();

    }

    public void sendSessionMessage(Session session, String message) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException error) {
            System.out.println(error);
        }
    }

    public GameSession getGameSession(String id) {
        Optional<GameSession> currentSessionExists = this.gameSessions.stream().filter(s -> s.getGameId().toString().equals(id)).findFirst();
        if (currentSessionExists.isPresent()) {
            return currentSessionExists.get();
        } else {
            return null;
        }
    }

    public boolean isAction(Actions action, Object actionIndex) {
        return actionIndex.equals(action.ordinal());
    }

}

