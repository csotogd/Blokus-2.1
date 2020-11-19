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
import java.util.Collection;
import java.util.Collections;
import  java.util.Random;

/**
 *
 * runs a genetic algorithm for weight calculation.
 * We have a population of many genetic players
 * Every individual is a genetic player with different weights
 * In every generation, all individuals are split into groups of 4 RANDOMLY
 * Those 4 players play against each other and produce a winner.
 * The winner is stored.
 * All the winners reproduce until a population of hte same size is produced.
 * There might be different reproducing strategies, the one we have right now is also random in itself.
 * The previous generation dies including the winners. It is replaced by the new
 * generation formed by the winners descendants
 *
 *We repeat this proccess for a number of time.
 *
 * Then we will make all those individual play each other until only one is left (that code is still to be written)
 *
 */
public class weightCalculator {
    private int populationSize=800; //must be multiple of 4
    private ArrayList<GeneticPlayer> winners;//could also be a population of weights... we´ll see
    private  ArrayList<GeneticPlayer> population;
    private int nbrOfPlayers=4;
    private int generations= 10;

    private  ArrayList<GeneticPlayer> createPopulation(){
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
        Random random= new Random();
        float[] weightsFather= father.getWeightsAsArray();
        float[] weightsMother= mother.getWeightsAsArray();
        GeneticPlayer kid = new GeneticPlayer(0); //this number shouldn´t mind
        float[] kidsWeights= new float[GeneticPlayer.NUMBER_OF_STRATAEGIES];
        for (int i=0; i<GeneticPlayer.NUMBER_OF_STRATAEGIES; i++){

            float max=Math.max(weightsFather[i], weightsMother[i]);
            float min=Math.min(weightsFather[i], weightsMother[i]);
            float kidWeight = min + random.nextFloat() * (max - min);
            kidsWeights[i]=kidWeight;
        }

        kid.setWeightsAsArray(kidsWeights);

        return kid;

    }




    private  void transitionNextGeneration(){
        Random r= new Random();

        //shuffle the populations so people at the beggining may face people in the end
        Collections.shuffle(this.population);
        //we will match up 4 players for a game and remove them from the population
        //the winner will be stored. The winners will later reproduce to fill up the population again.
        //**this winners in real life would be in the population they would reproduce and then die
        //for simplicity sake we are killing them and then reproducing them.
        while(!population.isEmpty()){
            matchUpAndPlay();
        }

        //fill up the population with winners
        while(population.size()!=populationSize){
            int dadIndex=r.nextInt(winners.size());
            int momIndex=r.nextInt(winners.size());
            GeneticPlayer father= winners.get(dadIndex);
            GeneticPlayer mother= winners.get(momIndex);
            GeneticPlayer kid=reproduceByInterval(father, mother);
            population.add(kid);
        }


    }


private void matchUpAndPlay(){
    Player[] players = new Player[4];
    Random random= new Random();
    //we match up random players from the population
    for(int i=1; i<=nbrOfPlayers; i++){
        int index = random.nextInt(population.size());//select one individual from the ones that are left
        GeneticPlayer player= population.get(i);
        player.setNumber(i);
        players[i-1]=player;

        //delete it from the population
        population.remove(player);
    }


    //let those 4 players play against each other
    int DIMENSION=20;
    SimulatedGame simulation= new SimulatedGame(DIMENSION, players);
    simulation.simulate();

    //store winner
    winners.add((GeneticPlayer) simulation.getWinner());


}

public void calculateWeights(){
        this.createPopulation();
        for (int i=0; i>this.generations; i++){
            transitionNextGeneration();
        }

        //now we have to transit to a state where only individual survives



}

    public static void main(String[] args) {

        



    }


}
