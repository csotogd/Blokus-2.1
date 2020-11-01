/*
package Player;

import DataBase.Piece;
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

    */
/**
     *
     * @return the move chosen by the ai
     *//*

    public Move calculateMove(Board board){
       //while we code all the different strategies, make this call the one you want to try
        System.out.println("In calculate move for genetic algorithm");
        HashMap<Move, Float> movesAndScores = new HashMap<Move, Float>();
        //this map will store all the moves and their respective score,
        // scores will be modified throuout the algorithm.
        //see example in far from starting corner
        for (Move move : super.possibleMoveSet(board)){
            movesAndScores.put(move, (float) 0);
        }

        //uncomment to test a strategy

        //the weights is what we will calculate in the genetic algorithm
        //addsMostCorners(1,movesAndScores, board);
        //blocksMostCorners(1,movesAndScores, board);
        closestToMiddle(1,movesAndScores, board);
        //biggestPiece(1,movesAndScores, board);
        //farFromStartingCorner(1,movesAndScores, board);

        float maxScore = 0;
        Move bestMove = null;
        for (Move move : movesAndScores.keySet()){
            if (movesAndScores.get(move) > maxScore){
                maxScore = movesAndScores.get(move);
                bestMove = move;
            }
        }
        if (bestMove == null){
            System.out.println("There is no best move, or every moves' score is 0");
        }
        bestMove.print();

        return bestMove;
    }

    //return type can be changed


    private void blocksMostCorners(float weight,HashMap<Move, Float> movesAndScores, Board board){;}

    private void closestToMiddle(float weight, HashMap<Move, Float> movesAndScores, Board board) {
        //for every move, see how close the closest corner is to the middle.

        //TODO: It seems to have some trouble with the dot piece (I1). We might have to check that
        for (Map.Entry<Move, Float> entry : movesAndScores.entrySet()) {
            //for every move, see which of the corners of the piece is closest to the middle of the board.
            //score will be the minimal distance between any corner of the piece and middle. The closer the shorter the distance
            Move move = entry.getKey();
            ArrayList<Corner> cornerContacts = move.getPiece().getCornersContacts(move.getPosition());

            //I made the distance variables floats, because distance is often not an integer.
            //The Vector2d class unfortunately only handles integers, but because the board is a square, the x middle = y middle
            float middleXandY = (float) (board.getBoardDimension() - 1) / 2;
            float minDistance = (float) Math.sqrt(2*(board.getDIMENSION()*board.getBoardDimension()))/2;//1000;//normalization = maxDistance
            //We put maxDistance as initial value because it is the largest value it can be. Now it can only get smaller.

            for (Corner corner : cornerContacts) {
                Vector2d cornerPosition = corner.getPosition();
                float //distance = cornerPosition.moduleDistance(middle);
                        //The modular distance also spits out an int, so this is a manual module distance that gives a float
                        distance = (float) Math.sqrt(Math.pow(cornerPosition.get_x() - middleXandY, 2) + Math.pow(cornerPosition.get_y() - middleXandY, 2));

                if (distance < minDistance) {
                    minDistance = distance;
                }

                //Alternative for normalization:
                //float maxDistance = (float) Math.sqrt(Math.pow(this.startingCorner.get_x() - middleXandY, 2) + Math.pow(this.startingCorner.get_y() - middleXandY, 2));

                //the score if weight was one needs to be between [0, 1]
                float normalization = (float) Math.sqrt(2*board.getDIMENSION()*board.getBoardDimension())/2;// this equals maxDistance
                float score = weight * (1 - minDistance / normalization);// 1 - normalized minDistance, because the score should be higher when minDistance is lower
                //This calculation is the same as calculating maxDistance - minDistance at every step, choosing the highest result and normalizing that.

                //now the  for each move is updated according to what we calculated and the weight given.
                movesAndScores.put(move, movesAndScores.get(move) + score);
            }
        }
    }

    private void biggestPiece(float weight, HashMap<Move, Float> movesAndScores,Board board){
        for (Map.Entry<Move, Float> entry : movesAndScores.entrySet()){
            Move move = entry.getKey();
            Piece piece = move.getPiece();
            //Max number of blocks in a piece is 5
            float normalization = 5;
            float score = weight * (piece.getNumberOfBlocks()/normalization);

            //add score to the corresponding piece
            movesAndScores.put(move, movesAndScores.get(move) + score);
        }
    }

    //TODO TESTING
    private void addsMostCorners(float weight,HashMap<Move, Float> movesAndScores, Board board) {

        //for every move, see how many corners would be added.

        for (Map.Entry<Move, Float> entry : movesAndScores.entrySet()) {
            Move move = entry.getKey();
            ArrayList<Corner> cornerContacts = move.getPiece().getCornersContacts(move.getPosition());
            //see how many of the corner of the added piece can be used to place another piece.
            */
/*so for example a piece in the shape of an 'L might have 2 corners, however
            * one is in contact with one piece of the same colour(otherwise L would no be able to be placed)
            * The other corner might be free, or also in contact with a piece, we need to check that.
            * If one corner was free then we would assign 1, if 2 we would assign 2 and so on.....
            * *//*



            int addedFreeCorners=0;
            for(Corner corner: cornerContacts ){
                //check they are 0 and not a BOARD corner.

                for(Vector2d toCornerPosition : corner.getToCornerPositions()){

                    try {
                        if (board.board[toCornerPosition.get_y()][toCornerPosition.get_x()] == 0){
                            addedFreeCorners++; //if the corner is empty and free to use
                    }
                        else if(board.board[toCornerPosition.get_y()][toCornerPosition.get_x()]==this.number){
                            addedFreeCorners--; //you blocked a self-corner, at least one is blocked to put the piece there
                            //you might be adding a pioece with 6 blocking corners, then that is a negative score
                           // System.out.println("blocked self corner");
                        }

                    }catch (IndexOutOfBoundsException e){
                        ;//a free corner outside of the board makes no sense
                    }
                }
            }
            if (move.getPiece().getLabel().equals("X"))
            System.out.println();

            //the score if weight was one needs to be between [-1, 1]
            //If it is negative it means, we added no new corners and we blocked some we had
            float normalization = 6; //the max number of added corner is 7, if we use the '+' piece and they are all free
            float score = weight * (addedFreeCorners / normalization);

            //now the  for each move is updated according to what we calculated and the weight given.
            movesAndScores.put(move, movesAndScores.get(move) + score);


        }
    }

    //TODO: test it

    */
/**
     * strategy that calculates how far away from the starting corner a move is
     *
     * @param weight
     * @param movesAndScores
     * @param board
     *//*

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
                System.out.println("corner position:" ); cornerPosition.printVector();
                System.out.println("module distance:"+distance);
                if(distance>maxDistance){
                    maxDistance=distance;
                }

            }

            //the score if weight was one needs to be between [0, 1]
            //float normalization= (float) Math.sqrt(2*(board.getDIMENSION()^2));
            float normalization=1;
            float score = weight*(maxDistance/normalization);

            //now the  for each move is updated according to what we calculated and the weight given.
            movesAndScores.put(move, movesAndScores.get(move) + score);

        }

        //commented alternative IGNORE THIS!!!
*/
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

*//*





        }

    

    //This is only used at the very end if the game. Only write this strategy when we are done with everything else
    private void endGamePossibleMoves(float weight){;}



    private void printBestMove(HashMap<Move, Float> movesAndScores){
        float max=-100;
        Move maxMove=null;
        for (Map.Entry<Move, Float> entry : movesAndScores.entrySet()){
            if(entry.getValue()>=max) {
                max = entry.getValue();
                maxMove=entry.getKey();
            }
        }
        System.out.println("best move: ");
        System.out.println("score: "+movesAndScores.get(maxMove));
        System.out.println("piece: "+maxMove.getPiece().getLabel());
        maxMove.getPiece().printShape();
        System.out.println("cordinates: ");maxMove.getPosition().printVector();
        System.out.println();
        System.out.println();
    }
}
*/
