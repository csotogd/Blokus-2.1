package GA_tools;

import DataBase.Piece;
import GameBoard.Board;
import GameBoard.BoardUI;
import Player.Player;
import Game.Game;
import Tools.Vector2d;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Player.*;

import java.util.ArrayList;
import  java.util.Random;
/*many games will be simulated at once*/
public class weightCalculator {
    private int populationSize=800; //must be multiple of 4


    private ArrayList<GeneticPlayer> createPopulation(){
        ArrayList<GeneticPlayer> population= new ArrayList<GeneticPlayer>();
        Random random= new Random();
        //will assign random weights between 0 and 1 to the strategies of every player
        for (int i=0; i<populationSize; i++){
            GeneticPlayer individual= new GeneticPlayer(i);
            individual.setWeightAddMostCorners(random.nextFloat());
            individual.setWeightBiggestPiece(random.nextFloat());
            individual.setWeightBlocksMostCorners(random.nextFloat());
            individual.setWeightClosestToMiddle(random.nextFloat());
            individual.setWeightFarFromStartingPoint(random.nextFloat());

        population.add(individual);
        }
    return population;
    }

    /**
     * one reproducing strategy
     * Reproducing strategy by interval:
     * for every strategy  construct an interval in with the parents weights:
     * [min(weightFather, weightMother), max(weightFather, weightMother),]
     * then the kid´s weight will be a random  point in that interval
     * @param father
     * @param mother
     * @return A new individual that will be introduce in the population
     */
    private GeneticPlayer reproduceByInterval(GeneticPlayer father, GeneticPlayer mother){

        float[] weightsFather= father.getWeightsAsArray();
        float[] weightsMother= mother.getWeightsAsArray();
        GeneticPlayer kid = new GeneticPlayer(0); //this number shouldn´t mind
        return null;

    }



    public static void main(String[] args) {
        /*
         * //WE need to
         *
         * 1. Reproducing winners by altering weights
         * 2. introduce mutations
         * 3. run the algorithm several times
         *
         *
         * */



        //to create a new game first we need to define new genetic players
        Player[] players = new Player[5];
        GeneticPlayer player1= new GeneticPlayer(1);
        float player1Weights[]= {0.132f, 0.4745f, 0.8537f, 0.67319f, 0.0489f};
        players[0]=player1;
        //we do that for every player....


        int DIMENSION=20;
        SimulatedGame simulation= new SimulatedGame(DIMENSION, players);
        simulation.simulate();
    //we crete and simulate a new game between those players
        Player winner=simulation.getWinner();

        //if we want to create a new game we repeat the proccess.
        //knowing these, we can write a ga algorithm.



    }


}
