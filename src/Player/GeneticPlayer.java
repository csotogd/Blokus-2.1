package Player;

import GameBoard.Board;
import GameBoard.Corner;
import Move.Move;
import Tools.Vector2d;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;



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
        for (Move move : super.possibleMoveSet(board)){
            movesAndScores.put(move, new Float(0));
        }


    return null;
    }

    //return type can be changed
    private void addsMostCorners(float weight,HashMap<Move, Float> movesAndScores, Board board){;}

    private void blocksMostCorners(float weight,HashMap<Move, Float> movesAndScores ,Board board){;}

    private void closestToMiddle(float weight, HashMap<Move, Float> movesAndScores,Board board){;}

    private void biggestPiece(float weight, HashMap<Move, Float> movesAndScores,Board board){;}

    //TODO: test it
    //Dont try to imitate what I did here, as it is wrong and not finished
    private void farFromStartingCorner(float weight, HashMap<Move, Float> movesAndScores,Board board){

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



        }

    

    //This is only used at the very end if the game. Only write this strategy when we are done with everything else
    private void endGamePossibleMoves(float weight){;}


}
