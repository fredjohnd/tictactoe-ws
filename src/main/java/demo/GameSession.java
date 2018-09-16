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

    private Player playerTurn = null;

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
        JSONObject data = new JSONObject()
                .put("gameId", this.gameId)
                .put("firstPlayer", this.player1.getName())
                .put("playerTurn", this.playerTurn.getName())
                .put("moves", this.moves);

        if (this.player2 != null) {
            data.put("secondPlayer", this.player2.getName());
        }
        String dataJSON = String.valueOf(data);
        return dataJSON;
    }

    public void joinGame(Player player) {

        if (this.player1 == null) {
            this.player1 = player;
        } else {
            this.player2 = player;
        }
    }

    public void start() {
        this.playerTurn = this.player1;
        this.updateClients();
    }

    public void updateClients() {
        this.player1.sendMessage(this.toJSON());
        this.player2.sendMessage(this.toJSON());
    }

    public void makeMove(Player player, int moveIndex) {

        // if player is the current Player and can play chosen index
        if (this.playerTurn.equals(player) && this.moves.get(moveIndex).equals(null)) {
            this.moves.put(moveIndex, player.playerIndex);

            // Change player turn and update clients
            this.playerTurn = player.equals(this.player1) ? this.player2 : this.player1;
            this.updateClients();
        }
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

    public Player getPlayerById(String id) {
        if (this.player1.getId().equals(id)) {
            return this.player1;
        } else if (this.player2.getId().equals(id)) {
            return this.player2;
        } else {
            return null;
        }
    }
}

