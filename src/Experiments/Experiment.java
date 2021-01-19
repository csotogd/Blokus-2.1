package Experiments;

import DataBase.Data;
import GA_tools.SimulatedGame;
import Player.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import java.util.ArrayList;

public class Experiment {
    private int runs = 100; //The number of times this experiment will be performed
    private int nbrOfPlayers = 2;
    private int dimension = 10;
    private int timeLimit = 3000;//Time limit for MC related bots (in ms)
    private Player[] players;
    private boolean allPlayerCombinations = false;//Does runs number of runs with every possible player combination:
    //So for example, 100 runs of [player1, player2] and 100 runs of [player2, player1]
    private ArrayList<Result> results = new ArrayList<>();
    private LocalDateTime startTime;

    public static void main(String[] args) {
        Player p = new MiniMaxPlayer(1);//new GeneticPlayer(1);
        Player p3 = new GeneticPlayer(2);
        ((GeneticPlayer)p3).setDepth(1);
        p3.setName("GA depth 1");
        /*
        Player p3 = new GeneticPlayer(3);
        ((GeneticPlayer)p3).setDepth(1);
        p3.setName("GA depth 1");
        Player p4 = new GaMcTplayer(4);
        Player[] players = {p, p2, p3, p4};
        Data.setPlayerTypes(new String[]{"MiniMax-MaxN Player", "", "", ""});
         */
        Player[] players = {p, p3};
        Data.setPlayerTypes(new String[]{"MiniMax-MaxN Player", ""});
        Experiment exp = new Experiment(10, 2, 10, 3000, players, false);
        exp.run();
        exp.logExperiment("src/Experiments/resultLog.txt");
    }

    public Experiment(int runs, int nbrOfPlayers, int dimension, int timeLimit, Player[] players, boolean allPlayerCombinations){
        this.runs = runs;
        this.nbrOfPlayers = nbrOfPlayers;
        this.dimension = dimension;
        this.timeLimit = timeLimit;
        this.players = players;
        this.allPlayerCombinations = allPlayerCombinations;
        giveNames();
    }

    public Experiment(Player[] players){
        this.players = players;
        nbrOfPlayers = players.length;
        giveNames();
    }

    public void run(){
        startTime = LocalDateTime.now();
        results.clear();

        if (! allPlayerCombinations){
            doSimulations(players);
            return;
        }

        Player[] playerOrder = new Player[players.length];

        if (nbrOfPlayers == 2){
            playerOrder[0] = players[0];
            playerOrder[1] = players[1];
            doSimulations(playerOrder.clone());
            playerOrder[0] = players[1];
            playerOrder[1] = players[0];
            doSimulations(playerOrder.clone());
        } else if (nbrOfPlayers == 4){
            for (int i = 0; i < 4; i++){
                for (int j = 0; j < 4; j++){
                    if (j == i){
                        continue;
                    }
                    for (int k = 0; k < 4; k++){
                        if (k == i || k == j){
                            continue;
                        }
                        for (int l = 0; l < 4; l++){
                            if (l == i || l == j || l == k){
                                continue;
                            }
                            playerOrder[i] = players[0];
                            playerOrder[j] = players[1];
                            playerOrder[k] = players[2];
                            playerOrder[l] = players[3];
                            doSimulations(playerOrder.clone());
                        }
                    }
                }
            }
        }
    }

    private void doSimulations(Player[] players){
        Result result = new Result(players, this);

        for (int i = 0; i < runs; i++){
            //Clone the players so they don't keep info from previous games
            Player[] clonedPlayers = new Player[players.length];
            for (int j = 0; j < players.length; j++){
                clonedPlayers[j] = players[j].clone();//Nog ff kijken naar die clone method
            }

            SimulatedGame sim = SimulatedGame.makeSimGame(dimension, clonedPlayers, timeLimit);
            long start = System.currentTimeMillis();
            sim.simulate();
            long duration = System.currentTimeMillis() - start;

            result.addSimResults(sim, duration, i);
        }

        results.add(result);
        System.out.println(result.resultToString());
    }

    public boolean logExperiment(String filename){//boolean for whether it succeeded or failed
        try {
            FileWriter w = new FileWriter(filename, true);

            //Experiment info
            w.write(
                    "EXPERIMENT: " +
                            "\nStart time: " + startTime.toString() +
                            "\nEnd time: " + LocalDateTime.now().toString() +
                            "\nNumber of runs: " + runs +
                            "\nBoard dimension: " + dimension +
                            "\nTime limit for MC-related bots: " + timeLimit +
                            "\n\nRESULTS: \n"
            );

            //Results info
            for (Result r : results){
                w.write(r.resultToString());
            }

            //End of experiment
            w.write("--------------------------------------------------------------------------------------\n\n");

            w.close();
            System.out.println("Successfully wrote to file");
        } catch (IOException e){
            System.out.println("Whoops there's an exception");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void giveNames(){
        for (Player player : players){
            if (player.getName() == null) {
                player.setName(player.getClass().getName().substring(7));
            }
        }
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getNbrOfPlayers() {
        return nbrOfPlayers;
    }

    public void setNbrOfPlayers(int nbrOfPlayers) {
        this.nbrOfPlayers = nbrOfPlayers;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }
}