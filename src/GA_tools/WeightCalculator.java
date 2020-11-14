package GA_tools;

import Player.Player;
import Player.*;

import java.util.ArrayList;
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
 * All the winners reproduce until a population of the same size is produced.
 * There might be different reproducing strategies, the one we have right now is also random in itself.
 * The previous generation dies including the winners. It is replaced by the new
 * generation formed by the winners descendants
 *
 * We repeat this process for a number of time.
 *
 * Then we will make all those individual play each other until only one is left (that code is still to be written)
 *
 */
public class WeightCalculator {
    private int populationSize = 800; //must be multiple of 4
    private final int generations = 10;
    private final double mutationChance = 0.1;
    private ArrayList<GeneticPlayer> winners = new ArrayList<>();//could also be a population of weights... we´ll see
    private  ArrayList<GeneticPlayer> population = new ArrayList<>();
    private final int nbrOfPlayers = 4;
    private final int DIMENSION = 20;

    public static void main(String[] args) {

        WeightCalculator calculator = new WeightCalculator();
        calculator.calculateWeights();
        //TODO: Make tournament method where just one or a few individuals remain. They will have the best weights
        //TODO: Make more reproduction methods (maybe one where the winners occupy a part of the new population and the rest is their offspring
        //TODO: Make mutation methods - One has been made

    }

//-------------------------------------------General methods-------------------------------------------------------

    public void calculateWeights(){
        createPopulation();
        for (int i = 0; i < this.generations; i++){
            winners.clear();//Putting this for now so that the winners from gen i produce only the population for gen i+1
            playGames();

            transitionNextGeneration();
        }

        //now we have to transit to a state where only individual survives


    }

    private void playGames(){
        Random r = new Random();

        //shuffle the populations so people at the beginning may face people in the end
        //Collections.shuffle(this.population);
        //This is not really necessary as we already randomly pick individuals in matchUpAndPlay()

        //we will match up 4 players for a game and remove them from the population
        //the winner will be stored. The winners will later reproduce to fill up the population again.
        //**this winners in real life would be in the population they would reproduce and then die
        //for simplicity sake we are killing them and then reproducing them.
        while(!population.isEmpty()){
            matchUpAndPlay();
        }
    }

    private void matchUpAndPlay(){
        Player[] players = new Player[nbrOfPlayers];
        Random random = new Random();
        //we match up random players from the population
        for(int i=1; i<=nbrOfPlayers; i++){
            int index = random.nextInt(population.size());//select one individual from the ones that are left
            GeneticPlayer player = population.get(i);
            player.setNumber(i);
            players[i-1] = player;

            //delete it from the population
            population.remove(player);
        }


        //let those 4 players play against each other
        SimulatedGame simulation = new SimulatedGame(DIMENSION, players);
        simulation.simulate();

        //store winner
        winners.add((GeneticPlayer) simulation.getWinner());
    }

    private void createPopulation(){
        Random random = new Random();
        //will assign random weights between 0 and 1 to the strategies of every player
        for (int i = 0; i < populationSize; i++){
            GeneticPlayer individual = new GeneticPlayer(i);
            individual.setWeightAddMostCorners(random.nextFloat());
            individual.setWeightBiggestPiece(random.nextFloat());
            individual.setWeightBlocksMostCorners(random.nextFloat());
            individual.setWeightClosestToMiddle(random.nextFloat());
            individual.setWeightFarFromStartingPoint(random.nextFloat());

            population.add(individual);
        }
    }

//---------------------------------Transition to next generation methods-------------------------------------------

    private void transitionNextGeneration(){
        Random r = new Random();
        //fill up the population with winners
        while(population.size()!=populationSize){
            int dadIndex = r.nextInt(winners.size());
            int momIndex = r.nextInt(winners.size());
            GeneticPlayer father = winners.get(dadIndex);
            GeneticPlayer mother = winners.get(momIndex);

            //Reproduction method
            GeneticPlayer kid = reproduceByInterval(father, mother);

            //Mutation method
            mutate(kid);

            population.add(kid);
        }

    }

//------------------------------------------Reproduction methods---------------------------------------------------

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
        float[] kidsWeights= new float[GeneticPlayer.NUMBER_OF_STRATEGIES];
        for (int i = 0; i<GeneticPlayer.NUMBER_OF_STRATEGIES; i++){

            float max=Math.max(weightsFather[i], weightsMother[i]);
            float min=Math.min(weightsFather[i], weightsMother[i]);
            float kidWeight = min + random.nextFloat() * (max - min);
            kidsWeights[i]=kidWeight;
        }

        kid.setWeightsAsArray(kidsWeights);

        return kid;

    }

//--------------------------------------------Mutation methods-----------------------------------------------------

    private void mutate(GeneticPlayer player){
        Random r = new Random();

        float[] weights = player.getWeightsAsArray();

        for (int mutateWeight = 0; mutateWeight < weights.length; mutateWeight++) {
            if (r.nextDouble() <= mutationChance) {
                weights[mutateWeight] = weights[mutateWeight] + (r.nextFloat() * 2 - 1);
            }
        }

    }

}
