package Experiments;

import GA_tools.SimulatedGame;
import Player.*;

import java.util.ArrayList;

public class Result {
    private final Player[] players;
    private final Experiment experiment;
    private float[] wins;//Contains the number of wins of the players.
    private int[][] scores;//Contains the scores of all players: scores[run][playernumber-1]
    private long[] times;//Contains the times of all runs.

    public Result(Player[] players, Experiment experiment){
        this.players = players;
        this.experiment = experiment;
        wins = new float[players.length];
        scores = new int[experiment.getRuns()][players.length];
        times = new long[experiment.getRuns()];
    }

    public void addSimResults(SimulatedGame sim, long time, int run){
        //Collect results:
        //Winner
        ArrayList<Player> winners = sim.getWinners();
        for (Player winner : winners){
            wins[winner.getPlayerNumber() - 1] += 1./winners.size();
        }
        //Scores
        for (int i = 0; i < players.length; i++){
            scores[run][i] = sim.players[i].getPoints();
        }
        //Time? (begin? & duration?)
        times[run] = time;
    }

    public float[] getWins() {
        return wins;
    }

    /**
     *
     * @return the average scores of all players, where avg score of player i is avgScores[i-1]
     */
    public float[] getAverageScores(){
        float[] avgScores = new float[players.length];
        for (int i = 0; i < players.length; i++){
            float total = 0;
            for (int[] run : scores) {
                total += run[i];
            }
            avgScores[i] = total/scores.length;
        }
        return avgScores;
    }

    /**
     *
     * @return the average time of the simulations in minutes
     */
    public float getAverageTime(){
        long total = 0;
        for (long time : times){
            total += time;
        }
        long avgInMillis = total/times.length;
        return avgInMillis/60000.f;
    }

    public String resultToString(){
        float[] avgScores = getAverageScores();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < players.length; i++){
            result.append("Player ").append(i+1).append(": ");
            result.append("Name=").append(players[i].getName()).append(", ");
            result.append("Nr of wins=").append(wins[i]).append(", ");
            result.append("Avg score=").append(avgScores[i]);
            result.append("\n");
        }
        result.append("Avg time per game=").append(getAverageTime()).append(" minutes");
        result.append("\n\n");
        return result.toString();
    }
}
