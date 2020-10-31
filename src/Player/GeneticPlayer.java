package Player;

import GameBoard.Board;
import GameBoard.Corner;
import Move.Move;
import Tools.Vector2d;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GeneticPlayer extends BotPlayer {

    public GeneticPlayer(int number) {
        super(number);
    }

    /**
     *
     * @return the move chosen by the ai
     */
    public Move calculateMove(Board board){
       //while we code all the different strategies, make this call the one you want to try
        System.out.println("In calculate move for genetic algorithm");
        HashMap<Move, Float> movesAndScores = new HashMap<Move, Float>();
        //this map will store all the moves and their respective score,
        // scores will be modified throuout the algorithm.
        //see example in far from starting corner
        for (Move move : super.possibleMoveSet(board)){
            movesAndScores.put(move, new Float(0));
        }
        
        //the weights is what we will calculate in the genetic algorithm
        addsMostCorners(1,movesAndScores, board);
        blocksMostCorners(1,movesAndScores, board);
        closestToMiddle(1,movesAndScores, board);
        biggestPiece(1,movesAndScores, board);
        farFromStartingCorner(1,movesAndScores, board);

    return null;
    }

    //return type can be changed
    private void addsMostCorners(float weight,HashMap<Move, Float> movesAndScores, Board board){;}

    private void blocksMostCorners(float weight,HashMap<Move, Float> movesAndScores ,Board board){;}

    private void closestToMiddle(float weight, HashMap<Move, Float> movesAndScores,Board board){;}

    private void biggestPiece(float weight, HashMap<Move, Float> movesAndScores,Board board){;}

    //TODO: test it

    /**
     * strategy that calculates how far away from the starting corner a move is
     *
     * @param weight
     * @param movesAndScores
     * @param board
     */
    private void farFromStartingCorner(float weight, HashMap<Move, Float> movesAndScores,Board board){

        for (Map.Entry<Move, Float> entry : movesAndScores.entrySet()) {
            //for every move, see which of the corner of the piece is furthest away from the starting corner.
            //score will be the distance between it and the corner. The further away the greater the distance
            Move move=entry.getKey();
            ArrayList<Corner> cornerContacts=move.getPiece().getCornersContacts(move.getPosition());
            int maxDistance=-1;
            for(Corner corner: cornerContacts ){
                Vector2d cornerPosition=corner.getPosition();
                int distance= cornerPosition.moduleDistance(this.startingCorner);
                if(distance>maxDistance){
                    maxDistance=distance;
                }

            }

            //the score if weight was one needs to be between [0, 1]
            float normalization= (float) Math.sqrt(2*(board.getDIMENSION()^2));
            float score = weight*(maxDistance/normalization);

            //now the  for each move is updated according to what we calculated and the weight given.
            movesAndScores.put(move, movesAndScores.get(move) + score);

        }

        //commented alternative IGNORE THIS!!!
/*
        ArrayList<Move> possibleMoves = super.possibleMoveSet(board);
        for (Move move: possibleMoves){
            //for every move, see which of the corner of the piece is furthest awy from the starting corner.
            //score will be the distance between it and the corner. The further away the greater the distance
            ArrayList<Corner> cornerContacts=move.getPiece().getCornersContacts(move.getPosition());
            int maxDistance=-1;
            for(Corner corner: cornerContacts ){
                Vector2d cornerPosition=corner.getPosition();
                int distance= cornerPosition.moduleDistance(this.startingCorner);
                if(distance>maxDistance){
                    maxDistance=distance;
                }

            }
            float score = maxDistance;
            //TODO assign score to move....discuss how that will be done

            }

*/




        }

    

    //This is only used at the very end if the game. Only write this strategy when we are done with everything else
    private void endGamePossibleMoves(float weight){;}


}
