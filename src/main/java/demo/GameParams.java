package demo;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.util.List;
import java.util.Map;

public class GameParams {

    public String gameId;
    public String playerId;
    public String name;

    public GameParams(Session session) {
        Map<String, List<String>> params = session.getUpgradeRequest().getParameterMap();
        this.gameId     = params.containsKey("gameId")    ? params.get("gameId").get(0)     : null;
        this.playerId   = params.containsKey("playerId")  ? params.get("playerId").get(0)   : null;
        this.name       = params.containsKey("name")      ? params.get("name").get(0)       : null;
    }
}
