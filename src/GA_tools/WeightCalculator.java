package GA_tools;

import Player.Player;
import Player.*;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final float[] targetWeights={ 0.3924f, 0.522f, 0.49f, 0.98038f, 0.863f} ;//this arre the weights that won´t change for 2 of the 3 phases
    private int alteredPhase=0;   // the phase for which we will alter the weights in case we do

    public static void main(String[] args) {
        //Found these weight sets:
        //[0.4533498, 0.073048696, 0.51042575, 1.0362283, 0.8176211]
        //[0.39246097, 0.51849484, 0.5114118, 0.9967747, 0.24054877]
        //[0.88642323, 0.9891394, 0.27855617, 1.9546838, 0.60788333]
        //[0.75693494, 0.7551954, 1.0880454, 1.3939637, 0.81373096]

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

        //ALL THE COLLAPSE PHASES METHODS ASSUME THAT AT THE START THE WEIGHTS FOR ALL THE PHASES WILL BE IDENTICAL
        createPopulation();
        for (int i = 0; i < this.generations; i++){
            System.out.print("\nBegin of generation " + i + ": 0%");
            winners.clear();//Putting this for now so that the winners from gen i produce only the population for gen i+1

            checkForUnevenPopulation();
            playGames();

            transitionNextGeneration();

            printPartOfPopulation(20);
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
        for (int i=0; i<3; i++){
            System.out.print("Phase "+i+": ");
            System.out.println(Arrays.toString(population.get(0).getWeightsAsArray()[i]));}
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

    private void createPopulation(){
        //createPopulationDifferentPhases();
       // createPopulationCollapsePhases();
        createPopulationOneRandomTwoFixed(alteredPhase);

        randomizePhasesStartTurns();
    }

    /**
     * Creates the initial population, filled with random weights.
     */
    private void createPopulationDifferentPhasesRandom(){
        Random random = new Random();
        //will assign random weights between 0 and 1 to the strategies of every player
        for (int i = 0; i < populationSize; i++) {
            GeneticPlayer individual = new GeneticPlayer(i);
            for (int phase = 0; phase < 3; phase++) {
                individual.setWeightAddMostCorners(random.nextFloat(), phase);
                individual.setWeightBiggestPiece(random.nextFloat(), phase);
                individual.setWeightBlocksMostCorners(random.nextFloat(), phase);
                individual.setWeightClosestToMiddle(random.nextFloat(), phase);
                individual.setWeightFarFromStartingPoint(random.nextFloat(), phase);
            }
            population.add(individual);
        }
    }

    /**
     * creates a population where the weights of 2 phases equal the target ones, they are fixed,
     * the only random one is the phase we tell the method. SO it really will be the only phase affecting the final output
     *
     * @param randomPhase the phase that will have random weights
     */
    private void createPopulationOneRandomTwoFixed(int randomPhase){
        Random random = new Random();
        //will assign random weights between 0 and 1 to the strategies of every player
        for (int i = 0; i < populationSize; i++) {
            GeneticPlayer individual = new GeneticPlayer(i);
            for (int phase = 0; phase < 3; phase++) {
                if (phase == randomPhase) {
                    individual.setWeightAddMostCorners(random.nextFloat(), phase);
                    individual.setWeightBiggestPiece(random.nextFloat(), phase);
                    individual.setWeightBlocksMostCorners(random.nextFloat(), phase);
                    individual.setWeightClosestToMiddle(random.nextFloat(), phase);
                    individual.setWeightFarFromStartingPoint(random.nextFloat(), phase);
                } else {
                    individual.setWeightAddMostCorners(targetWeights[0], phase);
                    individual.setWeightBlocksMostCorners(targetWeights[1], phase);
                    individual.setWeightClosestToMiddle(targetWeights[2], phase);
                    individual.setWeightBiggestPiece(targetWeights[3], phase);
                    individual.setWeightFarFromStartingPoint(targetWeights[4], phase);
                }
            }
            population.add(individual);
        }
    }

    /**
     * Every individual is assigned weights in the following way:
     * For every strategy, its weight will be the same in all phases
     *
     * for instance:
     * [0.3 0.9, 0.1, 0.7, 0.2] phase 1
     * [0.3 0.9, 0.1, 0.7, 0.2] phase 2
     * [0.3 0.9, 0.1, 0.7, 0.2] phase 3
     */
    private void createPopulationCollapsePhases(){
        Random random = new Random();
        //will assign random weights between 0 and 1 to the strategies of every player
        for (int i = 0; i < populationSize; i++) {
            GeneticPlayer individual = new GeneticPlayer(i);

            float w0=random.nextFloat();
            float w1=random.nextFloat();
            float w2=random.nextFloat();
            float w3=random.nextFloat();
            float w4=random.nextFloat();

            for (int phase = 0; phase < 3; phase++) {
                individual.setWeightAddMostCorners(w0, phase);
                individual.setWeightBiggestPiece(w1, phase);
                individual.setWeightBlocksMostCorners(w2, phase);
                individual.setWeightClosestToMiddle(w3, phase);
                individual.setWeightFarFromStartingPoint(w4, phase);
            }
            population.add(individual);
        }
    }

    private void randomizePhasesStartTurns(){
        Random r = new Random();

        for (int i = 0; i < populationSize; i++){
            GeneticPlayer individual = population.get(i);

            int[] phasesStartTurns = new int[2];
            phasesStartTurns[0] = r.nextInt(5) + 4;
            phasesStartTurns[1] = r.nextInt(5) + 9;

            individual.setPhasesStartTurns(phasesStartTurns);
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

    private void checkPhasesOrder(int[] phasesStartTurns){
        //If phase 1 starts after phase 2, it doesn't make sense. So if that happens, just swap them.
        if (phasesStartTurns[0] > phasesStartTurns[1]){
            int temp = phasesStartTurns[0];
            phasesStartTurns[0] = phasesStartTurns[1];
            phasesStartTurns[1] = temp;
        }
    }

    private void printPartOfPopulation(int partSize){
        for (int i = 0; i < partSize; i++){
            System.out.println("player " + i + ": ");
            for (int phase=0; phase<3; phase++){
                System.out.print("Phase "+phase+": ");
                System.out.println(Arrays.toString(population.get(i).getWeightsAsArray()[phase]));
            }
            System.out.println("phasesStartTurns: " + Arrays.toString(population.get(i).getPhasesStartTurns()));
            System.out.println();
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
            GeneticPlayer kid = reproduce(father, mother);
            phasesReproduceByInterval(father, mother, kid);
            //phasesReproduceByChromosomes(father, mother, kid);

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

    private GeneticPlayer reproduce(GeneticPlayer father, GeneticPlayer mother){
        //return this.reproduceByIntervalCollapsePhases(father,mother);
       return this.reproduceByIntervalDifferentPhases(father,mother, alteredPhase);
    }

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
    private GeneticPlayer reproduceByIntervalCollapsePhases(GeneticPlayer father, GeneticPlayer mother){
        Random random = new Random();
        float[][] weightsFather= father.getWeightsAsArray();
        float[][] weightsMother= mother.getWeightsAsArray();
        GeneticPlayer kid = new GeneticPlayer(0); //this number shouldn´t mind
        float[][] kidsWeights= new float[3][GeneticPlayer.NUMBER_OF_STRATEGIES];


        for (int i = 0; i<GeneticPlayer.NUMBER_OF_STRATEGIES; i++){
            //since all weights will be the same for every phase awhen this is used, it is ok to index with either 0 1 or 2
            float max=Math.max(weightsFather[0][i], weightsMother[0][i]);
            float min=Math.min(weightsFather[0][i], weightsMother[0][i]);
            float kidWeight = min + random.nextFloat() * (max - min);
            kidsWeights[0][i]=kidWeight;
            kidsWeights[1][i]=kidWeight;
            kidsWeights[2][i]=kidWeight;
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
        float[] weightsFather = father.getCurrentWeightsAsArray();
        float[] weightsMother = mother.getCurrentWeightsAsArray();
        GeneticPlayer kid = new GeneticPlayer(0); //this number shouldn´t mind
        float[] kidsWeights = new float[GeneticPlayer.NUMBER_OF_STRATEGIES];
        for (int i = 0; i < kidsWeights.length; i++){
            if (r.nextBoolean()){
                kidsWeights[i] = weightsFather[i];
            } else{
                kidsWeights[i] = weightsMother[i];
            }
        }

        kid.setCurrentWeightsAsArray(kidsWeights, 0);

        return kid;
    }

    /**
     * Reproduction strategy for the variable {@code phasesStartTurns}. Same concept as {@code reproduceByChromosomes}
     * @param father
     * @param mother
     * @param kid
     */
    private void phasesReproduceByChromosomes(GeneticPlayer father, GeneticPlayer mother, GeneticPlayer kid){
        Random r = new Random();

        int[] fatherPhasesTurns = father.getPhasesStartTurns();
        int[] motherPhasesTurns = mother.getPhasesStartTurns();
        int[] kidsPhasesTurns = new int[GeneticPlayer.NUMBER_OF_PHASES];
        for (int i = 0; i < kidsPhasesTurns.length; i++){
            if (r.nextBoolean()){
                kidsPhasesTurns[i] = fatherPhasesTurns[i];
            } else{
                kidsPhasesTurns[i] = motherPhasesTurns[i];
            }
        }

        checkPhasesOrder(kidsPhasesTurns);

        kid.setPhasesStartTurns(kidsPhasesTurns);
    }

    private void phasesReproduceByInterval(GeneticPlayer father, GeneticPlayer mother, GeneticPlayer kid){
        Random r = new Random();

        int[] fatherPhasesTurns = father.getPhasesStartTurns();
        int[] motherPhasesTurns = mother.getPhasesStartTurns();
        int[] kidsPhasesTurns = new int[GeneticPlayer.NUMBER_OF_PHASES];
        for (int i = 0; i < kidsPhasesTurns.length; i++){
            int max = Math.max(fatherPhasesTurns[i], motherPhasesTurns[i]);
            int min = Math.min(fatherPhasesTurns[i], motherPhasesTurns[i]);
            kidsPhasesTurns[i] = min + r.nextInt(max - min);
        }

        checkPhasesOrder(kidsPhasesTurns);

        kid.setPhasesStartTurns(kidsPhasesTurns);
    }

    //----------------------------------Reproduction methods for PHASES SEPARATELY----------------------------------//
    /**
     * one reproducing strategy
     * Reproducing strategy by interval:
     * for every strategy  construct an interval in with the parents weights:
     * [min(weightFather, weightMother), max(weightFather, weightMother),]
     * then the kid´s weight will be a random  point in that interval
     *
     * will only change the weights of ONE SELECTED phase.
     *
     * phase can  be 0, 1 or 2
     *
     * @param father One randomly picked father of the kid
     * @param mother One randomly picked mother of the kid
     * @return A new individual that will be introduced in the population
     */
    private GeneticPlayer reproduceByIntervalDifferentPhases(GeneticPlayer father, GeneticPlayer mother, int phase){
        Random random = new Random();
        float[][] weightsFather = father.getWeightsAsArray();
        float[][] weightsMother= mother.getWeightsAsArray();
        GeneticPlayer kid = new GeneticPlayer(0); //this number shouldn´t mind

        //weights of 2 phases will be the same as the parent one
        float[] kidsWeightsforDesiredPhase= new float[GeneticPlayer.NUMBER_OF_STRATEGIES];
        float [][] kidsWeights= new float[3][GeneticPlayer.NUMBER_OF_STRATEGIES];

        //update weights for that phase.

        //else keep it the same as it was
        for (int i = 0; i<GeneticPlayer.NUMBER_OF_STRATEGIES; i++){
            //get weights only for one desired phase
            float max=Math.max(weightsFather[phase][i], weightsMother[phase][i]);
            float min=Math.min(weightsFather[phase][i], weightsMother[phase][i]);
            float kidWeight = min + random.nextFloat() * (max - min);

            kidsWeightsforDesiredPhase[i]=kidWeight;
        }


        for (int i=0; i<3; i++) {//set every weight of kid
            if(i==phase)
            //kid gets reproduced weights for one phase
            kidsWeights[phase] = kidsWeightsforDesiredPhase;

            else{
                kidsWeights[i]= this.targetWeights;
                //for the other two phases weights are kept constant

            }
        }

        kid.setWeightsAsArray(kidsWeights);

        return kid;

    }





//--------------------------------------------Mutation methods-----------------------------------------------------

    private void mutate(GeneticPlayer player){
        //this.mutateWeightsCollapsePhases(player);
       // this.mutateWeightsDifferentPhases(player);
        this.mutateWeightsOnlyDesiredPhase(player, alteredPhase);
        //this.mutatePhases(player);

        //do not use mutateWeightCollapsePhases with mutateWeightsDifferentPhases at the same time!

    }


    /**
     * Randomly mutates the weights of a given player. The chance that a weight will be mutated
     * is denoted by {@code mutationChance}. If a weight gets mutated, then a random number between
     * -1 and 1 will be added to this weight.
     * It can mutate any of the 5x3 weights that are there, for every of the 15, mutation is considered individually
     * @param player The player that will potentially be mutated
     */
    private void mutateWeightsDifferentPhases(GeneticPlayer player){
        Random r = new Random();

        float[][] weights = player.getWeightsAsArray();

        //Every weight has an equal opportunity to be mutated
        for(int phase=0; phase<3; phase++) {
            for (int mutateWeight = 0; mutateWeight < weights[0].length; mutateWeight++) {
                if (r.nextDouble() <= mutationChance) {
                    //We mutate by adding or subtracting a value between 0 and 1
                    //We can always change this.
                    weights[phase][mutateWeight] += r.nextFloat() * 2 - 1;
                }
            }
        }
    }

    /**
     * Randomly mutates the weights of a given player. The chance that a weight will be mutated
     * is denoted by {@code mutationChance}. If a weight gets mutated, then a random number between
     * -1 and 1 will be added to this weight.
     * It can mutate any of the 5x3 weights that are there, for every of the 15, utation is considered individually
     * @param player The player that will potentially be mutated
     */
    private void mutateWeightsOnlyDesiredPhase(GeneticPlayer player, int alteredPhase){
        Random r = new Random();

        float[][] weights = player.getWeightsAsArray();

        //Every weight has an equal opportunity to be mutated

            for (int mutateWeight = 0; mutateWeight < weights[0].length; mutateWeight++) {
                if (r.nextDouble() <= mutationChance) {
                    //We mutate by adding or subtracting a value between 0 and 1
                    //We can always change this.
                    weights[alteredPhase][mutateWeight] += r.nextFloat() * 2 - 1;
                }

        }
    }


    /**
     * Randomly mutates the weights of a given player. The chance that a weight will be mutated
     * is denoted by {@code mutationChance}. If a weight gets mutated, then a random number between
     * -1 and 1 will be added to this weight.
     * It can mutate any of the 5 weights that are there, so if a certain weight is mutated
     * it will be mutated for all phases.
     *
     * This will be used when we are trying to reproduce the old functionality where we have no phases.
     * @param player The player that will potentially be mutated
     */
    private void mutateWeightsCollapsePhases(GeneticPlayer player){
        Random r = new Random();

        float[][] weights = player.getWeightsAsArray();

        //Every weight has an equal opportunity to be mutated

            for (int mutateWeight = 0; mutateWeight < weights[0].length; mutateWeight++) {
                if (r.nextDouble() <= mutationChance) {
                    //We mutate by adding or subtracting a value between 0 and 1
                    //We can always change this.
                    float mutationChange=r.nextFloat() * 2 - 1;
                    weights[0][mutateWeight] += mutationChange;
                    weights[1][mutateWeight] += mutationChange;
                    weights[2][mutateWeight] += mutationChange;
                    //weight changed for the 3 phases

                }
            }


    }



    /**
     * mutates the starting turn for strategies
     * @param player
     */
    private void mutatePhases(GeneticPlayer player){
        Random r = new Random();
        int[] phasesStartTurns = player.getPhasesStartTurns();

        for (int mutateTurn = 0; mutateTurn < phasesStartTurns.length; mutateTurn++){
            if (r.nextDouble() <= mutationChance) {
                phasesStartTurns[mutateTurn] += r.nextInt(2) - 1;
            }
        }

        checkPhasesOrder(phasesStartTurns);
    }








}
