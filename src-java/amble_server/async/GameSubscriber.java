package amble_server.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class GameSubscriber {
    String gameId;
    String playerId;

//    Double on

    private GameSubscriber(String gameId, String playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }

    private static HashMap<String, List<GameSubscriber>> gamesMap = new HashMap<String, List<GameSubscriber>>();

    void notify(String moveId) {

    }

    public static List<GameSubscriber> gameSubscribers(String gameId) {
        List<GameSubscriber> subscribers = gamesMap.get(gameId);
        if (null == subscribers) {
            gamesMap.put(gameId, new ArrayList<>());
            System.out.println((String.format("Created new subscriber list for game, \"%s\".", gameId)));
        }
        return gamesMap.get(gameId);
    }


    public static void addGameSubscriber (String gameId, String playerId) {
        gameSubscribers(gameId).add(new GameSubscriber(gameId, playerId));
    }
}