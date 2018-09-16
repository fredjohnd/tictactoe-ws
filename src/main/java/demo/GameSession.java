package demo;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

enum Players {PLAYER_ONE, PLAYER_TWO};

class GameSession {

    private UUID gameId = null;

    private Player player1;
    private Player player2;

    private Players playerTurn = Players.PLAYER_ONE;

    private Map moves;

    public UUID getGameId() {
        return this.gameId;
    }

    public GameSession(Player player1) {

        // Generate game Id
        this.gameId = UUID.randomUUID();

        // instantiate Plays map
        this.moves = new HashMap();
        this.resetPlays();

        // Add Player 1 to the game
        this.joinGame(player1);
    }

    public String toJSON() {
        String data = String.valueOf(new JSONObject()
                .put("gameId", this.gameId)
                .put("firstPlayer", this.player1.getName())
                .put("secondPlayer", this.player2.getName())
                .put("playerTurn", this.playerTurn)
                .put("moves", this.moves)

        );

        return data;
    }

    public void joinGame(Player player) {

        if (this.player1 == null) {
            this.player1 = player;
        } else {
            this.player2 = player;
        }
    }

    public void start() {
        this.playerTurn = Players.PLAYER_ONE;
        this.updateClients();
    }

    public void updateClients() {
        this.player1.sendMessage(this.toJSON());
        this.player2.sendMessage(this.toJSON());
    }

    public void makeMove(Player player, int moveIndex) {
        this.moves.put(moveIndex, player.playerIndex);
    }

    private void resetPlays() {
        this.moves.clear();
        this.moves.put(0, null);
        this.moves.put(1, null);
        this.moves.put(2, null);
        this.moves.put(3, null);
        this.moves.put(4, null);
        this.moves.put(5, null);
        this.moves.put(6, null);
        this.moves.put(7, null);
        this.moves.put(8, null);
    }
}

