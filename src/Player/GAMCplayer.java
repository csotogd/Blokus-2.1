package Player;

import DataBase.Piece;
import GameBoard.Board;
import MonteCarlo.MonteCarlo;
import Move.Move;

import java.util.*;


public class GAMCplayer extends GeneticPlayer {

    private MonteCarlo mc;


    /**
     * Constructor
     * @param number number of the player
     */
    public GAMCplayer(int number) {
        super(number);
    }

    /**
     * Method to be called when we want to find a move to play
     * @param board board on which the move has to be played
     * @param timelimit in ms, timelimit for the exploration
     * @return best move computed
     */
    public Move getBestMove(Board board, int timelimit){
        ArrayList<Move> best_moves = calculateMoves(board);
        boolean firstmove = isFirstMove();
        Move move = mc.simulation(this.number-1, timelimit,best_moves);
        this.setFirstMove(firstmove);
        return new Move(this,move.getPiece(),move.getPosition());
    }

    /**
     *
     * @param board
     * @return
     */
    public ArrayList<Move> calculateMoves(Board board){
        determinePhase();
    /*    System.out.println("in GAMC\t   IN PHASE "+getPhase());
        for (int i = 0; i <5 ; i++) {
            System.out.print(currentWeights[i]+" ");
        }
        System.out.println();

     */

        HashMap<Move, Float> score_moves=new HashMap<Move, Float>();
        for (Move move : super.possibleMoveSet(board)) { //compute the score of each possible move
            float score = 0;
            //the weights is what we will calculate in the genetic algorithm
            score += addsMostCorners(currentWeights[0], move, board);
            score += blocksMostCorners(currentWeights[1], move, board);
            score += closestToMiddle(currentWeights[2], move, board);
            score += biggestPiece(currentWeights[3], move, board);
            score += farFromStartingCorner(currentWeights[4], move, board);
            /*
            weights[0] = addMostCorners
            weights[1] = blocksMostCorners
            weights[2] = closestToMiddle
            weights[3] = biggestPiece
            weights[4] = farFromStartingPoint
             */

            score_moves.put(move, score*-1); //TODO: reverse order in other way if possible?
        }
        ArrayList<Move> sorted= sortByValue(score_moves); // sort the moves by their score

        return sorted;
    }

    /**
     * Sort the hashmap by the score value
     * @param hm hashmap to be sorted
     * @return an arraylist of the moves sorted
     */
    public static ArrayList<Move> sortByValue(HashMap<Move, Float> hm) {
        List<Map.Entry<Move, Float> > list = new LinkedList<>(hm.entrySet());

        // Sort the list 
        Collections.sort(list, (o1, o2) -> (o1.getValue()).compareTo(o2.getValue()));

        // put data from sorted list to ArrayList
        ArrayList<Move> best_moves= new ArrayList<>();
        for (Map.Entry<Move, Float> aa : list) {
            best_moves.add(aa.getKey());
        }
        return best_moves;
    }

    public void setMc(MonteCarlo mc) {
        this.mc = mc;
    }

    public GAMCplayer clone(){
        GAMCplayer player = new GAMCplayer(number);
        player.startingCorner=startingCorner;
        player.setWeightsAsArray(weights.clone());
        for(Piece p:piecesList) player.getPiecesList().add(p.clone());
        player.setFirstMove(isFirstMove());

        return player;
    }
}
