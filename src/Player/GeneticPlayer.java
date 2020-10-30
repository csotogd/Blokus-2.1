package Player;

import GameBoard.Board;
import GameBoard.Corner;
import Move.Move;
import Tools.Vector2d;

import java.sql.SQLOutput;
import java.util.ArrayList;

//TODO speci

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
    return null;
    }

    //return type can be changed
    private void addsMostCorners(float weight, Board board){;}

    private void blocksMostCorners(float weight, Board board){;}

    private void closestToMiddle(float weight, Board board){;}

    private void biggestPiece(float weight, Board board){;}

    //TODO: test it
    private void farFromStartingCorner(float weight, Board board){

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
