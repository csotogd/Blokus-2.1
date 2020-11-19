package GA_tools;

import Player.Player;
import Player.*;

import java.util.ArrayList;
import java.util.Arrays;
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
    private int populationSize = 200; //must be multiple of 4
    private final int generations = 100;
    private final double mutationChance = 0.1;
    private ArrayList<GeneticPlayer> winners = new ArrayList<>();//could also be a population of weights... we´ll see
    private  ArrayList<GeneticPlayer> population = new ArrayList<>();
    private final int nbrOfPlayers = 4;
    private final int DIMENSION = 20;

    public static void main(String[] args) {
        //Found this weight set:
        //[0.4533498, 0.073048696, 0.51042575, 1.0362283, 0.8176211]
        //[0.39246097, 0.51849484, 0.5114118, 0.9967747, 0.24054877]

        WeightCalculator calculator = new WeightCalculator();
        calculator.calculateWeights();

        //TODO: Make transition method (maybe one where the winners occupy a part of the new population and the rest is their offspring)
        //TODO: Make more reproduction methods - We now have two
        //TODO: Make mutation methods - One has been made


        //TODO: Try finding weights for each phase one at a time
        //TODO: Try finding weights for every phase at once
    }

//-------------------------------------------General methods-------------------------------------------------------

    /**
     * This is the method that puts all components together and calculates the weights.
     * First, this method calculates all generations and then holds a tournament where only one
     * player will remain. This player will have the best weights.
     */
    public void calculateWeights(){
        createPopulation();
        for (int i = 0; i < this.generations; i++){
            System.out.print("\nBegin of generation " + i + ": 0%");
            winners.clear();//Putting this for now so that the winners from gen i produce only the population for gen i+1

            checkForUnevenPopulation();
            playGames();

            transitionNextGeneration();
        }

        //now we have to transit to a state where only individual survives
        while (populationSize != 1){
            System.out.print("\nBegin of tournament: 0%");
            winners.clear();

            checkForUnevenPopulation();
            playGames();

            tournament();
        }

        System.out.println("\nBest weights: ");
        System.out.println(Arrays.toString(population.get(0).getWeightsAsArray()));
    }

    /**
     * This method plays all games for one generation or tournament round.
     * It also provides a nice interface to see how far into the generation
     * or round it is.
     */
    private void playGames(){
        //Random r = new Random();

        //shuffle the populations so people at the beginning may face people in the end
        //Collections.shuffle(this.population);
        //This is not really necessary as we already randomly pick individuals in matchUpAndPlay()

        //we will match up 4 players for a game and remove them from the population
        //the winner will be stored. The winners will later reproduce to fill up the population again.
        //**this winners in real life would be in the population they would reproduce and then die
        //for simplicity sake we are killing them and then reproducing them.
        String previousPercentage = "0%";
        while(!population.isEmpty()){

            if (!previousPercentage.equals((int) ((1 - (population.size() / (float) populationSize)) * 100) + "%")) {
                for (int i = 0; i < previousPercentage.length(); i++) {
                    System.out.print("\b");
                }
                System.out.print((int) ((1 - (population.size() / (float) populationSize)) * 100) + "%");
                previousPercentage = (int) ((1 - (population.size() / (float) populationSize)) * 100) + "%";
            }

            matchUpAndPlay();
        }
        for (int i = 0; i < previousPercentage.length(); i++) {
            System.out.print("\b");
        }
        System.out.print("100%");
    }

    /**
     * This method picks {@code nbrOfPlayers} players randomly and lets them play against
     * each other. Then, it puts the winner in {@code winners}
     */
    private void matchUpAndPlay(){
        Player[] players = new Player[nbrOfPlayers];
        Random random = new Random();
        //we match up random players from the population
        for(int i=1; i<=nbrOfPlayers; i++){
            int index = random.nextInt(population.size());//select one individual from the ones that are left
            GeneticPlayer player = population.get(index);
            player.setNumber(i);
            players[i-1] = player;

            //delete it from the population
            population.remove(index);
        }

        //let those 4 players play against each other
        SimulatedGame simulation = new SimulatedGame(DIMENSION, players);
        simulation.simulate();

        //store winner
        winners.add((GeneticPlayer) simulation.getWinner());
    }

    /**
     * Creates the initial population, filled with random weights.
     */
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

    /**
     * This method checks whether the population can be evenly divided into x games.
     * If this is not the case, then it adds as many clones of random individuals in the
     * population as necessary to make the population "even" again.
     */
    private void checkForUnevenPopulation(){
        if (populationSize % nbrOfPlayers != 0){
            Random r = new Random();

            for (int i = 0; i < 4 - populationSize % nbrOfPlayers; i++){
                population.add(population.get(r.nextInt(populationSize)).clone());
            }
            populationSize = population.size();
        }
    }

//---------------------------------Transition to next generation methods-------------------------------------------

    /**
     * Fills the population again with the offspring of the winners.
     * Offspring gets weights determined by two random parents. Then, the
     * offspring may or may not be randomly mutated.
     */
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

    /**
     * Advances to the next round of a tournament.
     */
    private void tournament(){
        populationSize /= nbrOfPlayers;
        population.addAll(winners);
    }

//------------------------------------------Reproduction methods---------------------------------------------------

    /**
     * one reproducing strategy
     * Reproducing strategy by interval:
     * for every strategy  construct an interval in with the parents weights:
     * [min(weightFather, weightMother), max(weightFather, weightMother),]
     * then the kid´s weight will be a random  point in that interval
     * @param father One randomly picked father of the kid
     * @param mother One randomly picked mother of the kid
     * @return A new individual that will be introduced in the population
     */
    private GeneticPlayer reproduceByInterval(GeneticPlayer father, GeneticPlayer mother){
        Random random = new Random();
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

    /**
     * A reproduction strategy
     * Reproduce by chromosomes:
     * Randomly divides the parents' weights to the kid's weights.
     * There is a 50% chance for a kid's weight to come from the father
     * and a 50% chance for it to come from the mother
     * @param father One randomly picked father of the kid
     * @param mother One randomly picked mother of the kid
     * @return A new individual that will be introduced in the population
     */
    private GeneticPlayer reproduceByChromosomes(GeneticPlayer father, GeneticPlayer mother){
        Random r = new Random();
        float[] weightsFather = father.getWeightsAsArray();
        float[] weightsMother = mother.getWeightsAsArray();
        GeneticPlayer kid = new GeneticPlayer(0); //this number shouldn´t mind
        float[] kidsWeights = new float[GeneticPlayer.NUMBER_OF_STRATEGIES];
        for (int i = 0; i < kidsWeights.length; i++){
            if (r.nextBoolean()){
                kidsWeights[i] = weightsFather[i];
            } else{
                kidsWeights[i] = weightsMother[i];
            }
        }

        kid.setWeightsAsArray(kidsWeights);

        return kid;
    }

//--------------------------------------------Mutation methods-----------------------------------------------------

    /**
     * Randomly mutates the weights of a given player. The chance that a weight will be mutated
     * is denoted by {@code mutationChance}. If a weight gets mutated, then a random number between
     * -1 and 1 will be added to this weight.
     * @param player The player that will potentially be mutated
     */
    private void mutate(GeneticPlayer player){
        Random r = new Random();

        float[] weights = player.getWeightsAsArray();

        //Every weight has an equal opportunity to be mutated
        for (int mutateWeight = 0; mutateWeight < weights.length; mutateWeight++) {
            if (r.nextDouble() <= mutationChance) {
                //We mutate by adding or subtracting a value between 0 and 1
                //We can always change this.
                weights[mutateWeight] = weights[mutateWeight] + (r.nextFloat() * 2 - 1);
            }
        }

    }

}
