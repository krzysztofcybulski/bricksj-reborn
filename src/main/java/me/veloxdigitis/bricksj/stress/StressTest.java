package me.veloxdigitis.bricksj.stress;

import me.veloxdigitis.bricksj.battle.BrickPlayer;
import me.veloxdigitis.bricksj.logger.Logger;
import me.veloxdigitis.bricksj.map.Brick;
import me.veloxdigitis.bricksj.map.InvalidBrick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StressTest implements Runnable {

    private List<BrickPlayer> players;
    private int mapSize;
    private int iterations;
    private StressListener listener;

    public StressTest(List<BrickPlayer> players, int mapSize, int iterations, StressListener listener) {
        this.players = players;
        this.mapSize = mapSize;
        this.iterations = iterations;
        this.listener = listener;
    }

    public StressTest(List<BrickPlayer> players, StressLevel stressLevel, TimeLevel timeLevel, StressListener listener) {
        this(players, stressLevel.getMapSize(), timeLevel.getIterations(), listener);
    }


    @Override
    public void run() {
        List<PlayerStressResult> results = new ArrayList<>();
        players.forEach(p -> results.add(checkPlayer(p)));
        listener.end(results);
    }

    private PlayerStressResult checkPlayer(BrickPlayer player) {
        PlayerStressResult result = new PlayerStressResult(player);
        listener.player(player);
        player.setMap(mapSize, Collections.emptyList());
        try {
            player.startMove();
            for (int i = 0; i < iterations; i++) {
                listener.iteration(i);
                result.registerTime(player.move(Brick.getRandom(mapSize)).getTime());
            }
            player.endGame();
        } catch (InvalidBrick invalidBrick) {
            Logger.error("Invalid move " + player + "!");
        }
        return result;
    }

}