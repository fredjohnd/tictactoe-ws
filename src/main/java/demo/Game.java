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

        System.out.println("Received: " + message);

        // Parse data to JSON
        JSONObject data = new JSONObject(message);

        String gameId   = data.get("gameId").toString();
        String playerId = data.get("playerId").toString();

        // only continue if there' a gameId
        if (gameId != null) {

            // Try to fetch existing session
            GameSession gameSession = this.getGameSession(gameId);
            if (gameSession == null) {
                System.out.println("Game session not found");
                return;
            }

            // Try to fetch existing Player
            Player player = gameSession.getPlayerById(playerId);
            if (player == null) {
                System.out.println("Player doesn't exist in session");
                return;
            }

            // Bail out if still waiting for a Player 2
            if (!gameSession.isBothPlayersJoined()) {
                player.sendMessage(Actions.WAITING_PLAYER, new JSONObject());
                return;
            }

            // If we are playing make a move
            if (this.isAction(Actions.PLAY, data.get("action"))) {
                int moveIndex = (int)data.get("move");
                gameSession.makeMove(player, moveIndex);

            // if we are restarting, then restart the game
            } else if (this.isAction(Actions.RESTART, data.get("action"))) {
                gameSession.restart();
            }
        }

    }

    /**
     * Creates a new session / starts new game as Player 1
     * @param session
     * @param params
     */
    // TODO: 19/09/2018 Create error method
    public void createSession(Session session, GameParams params) {

        // Check if name supplied
        if (params.name.isEmpty()) {
            String data = String.valueOf(new JSONObject().put("error", "No name specified"));
            this.sendSessionMessage(session, data);
            return;
        }

        // Instantiate player
        String playerName = params.name;
        Player player1 = new Player(playerName, 0, session);

        // Instantiate session
        GameSession gameSession = new GameSession(player1);
        this.gameSessions.add(gameSession);

        // Return gameSession to player
        JSONObject data = gameSession.toJSONObject();
        player1.sendMessage(Actions.SESSION_CREATED, data);

    }

    /**
     * Joins an existing session as a new player (Player 2)
     * @param session The session
     * @param params The GameParams (QueryParams/Request)
     */
    // TODO: 19/09/2018 Refactor error messages to method
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

    /**
     * Sends back a message to the session
     * @param session The session we are currently communicating with
     * @param message A message to send back
     */
    public void sendSessionMessage(Session session, String message) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException error) {
            System.out.println(error);
        }
    }

    /**
     * Get's a gameSession by ID
     * @param id The id to loo kfor
     * @return Existing GameSession or null if no session found
     */
    public GameSession getGameSession(String id) {
        Optional<GameSession> currentSessionExists = this.gameSessions.stream().filter(s -> s.getGameId().toString().equals(id)).findFirst();
        if (currentSessionExists.isPresent()) {
            return currentSessionExists.get();
        } else {
            return null;
        }
    }

    /**
     * Checks if an Action integer matches the ENUM
     * @param action The Action enum we're checking against
     * @param actionIndex The enum "index"
     * @return boolean If it matches
     */
    public boolean isAction(Actions action, Object actionIndex) {
        return actionIndex.equals(action.ordinal());
    }

}

