package Player;

import DataBase.Piece;
import GameBoard.Board;
import GameBoard.Corner;
import Move.Move;
import Tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GeneticPlayer extends BotPlayer {
    public static final int NUMBER_OF_STRATEGIES = 5;
    private int turn = 0;

    /*
    weights[0] = addMostCorners
    weights[1] = blocksMostCorners
    weights[2] = closestToMiddle
    weights[3] = biggestPiece
    weights[4] = farFromStartingPoint
     */
    protected float[][] weights = new float[3][5];
    protected int[] phasesStartTurns = new int[2];
    protected float[] currentWeights = weights[0];


    public GeneticPlayer(int number) {
        super(number);
    }

    /**
     *
     * @param number
     * @param weights needs to be of length 5.
     *                every entry represents the weight of a strategy in the following order.
     *         addsMostCorners
     *         blocksMostCorners
     *         closestToMiddle
     *         biggestPiece
     *         farFromStartingCorner
     */
    public GeneticPlayer(int number, float[] weights) {
        super(number);
        this.weights=weights;
    }

    public float[] getWeightsAsArray() {
        return weights;
    }

    public void setWeightsAsArray(float[] weights){
        this.weights=weights;
    }

    public void setWeightAddMostCorners(float weight){
    this.weights[0]=weight;
    }

    public float getWeightAddMostCorners(){
        return this.weights[0];
    }

    public void setWeightBlocksMostCorners(float weight){
        this.weights[1]=weight;
    }

    public float getWeightBlocksMostCorners(){
        return this.weights[1];
    }

    public void setWeightClosestToMiddle(float weight){
        this.weights[2]=weight;
    }

    public float getWeightClosestToMiddle(){
        return this.weights[2];
    }

    public void setWeightBiggestPiece(float weight){
        this.weights[3]=weight;
    }

    public float getWeightBiggestPiece(float weight){
        return this.weights[3];
    }

    public void setWeightFarFromStartingPoint(float weight){
        this.weights[4]=weight;
    }

    public float getWeightFarFromStartingPoint(float weight){
        return this.weights[4];
    }

    public void addTurn(){
        turn++;
    }

    public void resetTurn(){
        turn = 0;
    }




    /*
     *
     * @return the move chosen by the ai
     */


    public Move calculateMove(Board board){
        if (turn == phasesStartTurns[0]){
            currentWeights = weights[1];
        }
        if (turn == phasesStartTurns[1]){
            currentWeights = weights[2];
        }

       //while we code all the different strategies, make this call the one you want to try
        //System.out.println("In calculate move for genetic algorithm");

        float bestScore = -1000;
        ArrayList<Move> bestMoves = new ArrayList<Move>();

        for (Move move : super.possibleMoveSet(board)) {
            float score = 0;
            //uncomment to test a strategy

            //the weights is what we will calculate in the genetic algorithm
            score += addsMostCorners(weights[0], move, board);
            score += blocksMostCorners(weights[1], move, board);
            score += closestToMiddle(weights[2], move, board);
            score += biggestPiece(weights[3], move, board);
            score += farFromStartingCorner(weights[4], move, board);
            /*
            weights[0] = addMostCorners
            weights[1] = blocksMostCorners
            weights[2] = closestToMiddle
            weights[3] = biggestPiece
            weights[4] = farFromStartingPoint
             */

            if (score == bestScore){
                bestMoves.add(move);
            }
            if (score > bestScore){
                bestScore = score;
                bestMoves = new ArrayList<Move>();
                bestMoves.add(move);
            }
        }


        //printBestMove(bestMoves, bestScore);

        Move bestMove = null;
        if (bestMoves.size() == 0){
            //System.out.println("There is no best move, or every moves' score is 0");
        } else {
            bestMove = bestMoves.get(0);
        }

        return bestMove;
    }

    //return type can be changed


    private float blocksMostCorners(float weight, Move move, Board board){
        Piece piece = move.getPiece();
        int[][] shape = piece.getShape();
        Vector2d position = move.getPosition();

        int blockCornerNumber = 0;
        for (int i = 0; i < shape.length; i++){
            for (int j = 0; j < shape[0].length; j++){
                if (shape[i][j] != 0){
                    int nbrCornersBlocked = isDiffPlayerToCorner(i + position.get_y(), j + position.get_x(), board);
                    blockCornerNumber+=nbrCornersBlocked;
                }
            }
        }


        //this is a high estimate, but now we are sure that the unweighted score is between 0 and 1.
        //float normalization = piece.getNumberOfBlocks() * 4;
        //biggest piece has 5 squares and can block at most 3 players per square
        float normalization = 5*3; //
        float score = weight * (blockCornerNumber/normalization);

        return score;
        //movesAndScores.put(move, movesAndScores.get(move) + score);
    }

    /**
     * This method checks whether a certain coordinate is a toCorner of a different player.
     * So it checks if on that square, a different player could make a move
     * @param YPos y position of the square
     * @param XPos x position of the square
     * @param board current game board
     * @return the number of players for which the given square was a corner
     */
    private int isDiffPlayerToCorner(int YPos, int XPos, Board board){
        int[][] grid = board.getBoardArray();
        /*
        We check every corner point of p, here denoted by a c
        grid:
            c 0 c
            0 p 0
            c 0 c
        If we encounter a position c where c is a player number other then this player, then we check whether
        that player can make a move on p. We do this by checking all of the sides of p (denoted by the 0's).
        If one of those sides is equal to the player number (of the opponent), then this player could not make
        a move on p, and this position is not counted. However, if none of the sides are this player number,
        then the opponent could make a move. If this move would be performed, then this square would be taken.
        So we know that this move blocks a corner and we add 1 to the score.

        This method and checkSides are made so the main strategy method doesn't get too long.

        Maybe you can fix the issues by putting this method in the if statement expression, instead of the
        condition. Then let it return an integer instead of a boolean. Just make a new variable at the beginning
        of this method and set it to 0, and then increment that value everytime you reach a point marked with a *
        Then, at the end you return that variable and add it to the blockCornerNumber

        You still need to find something for when one player has p as a toCorner twice:
        1 0 1
        0 p 0
        0 0 0
        when this happens, this corner would be counted twice (after you fixed the previous problem).
        But you would only block one move from player 1. So it should only be counted once, right?

        Good luck!
         */

        boolean[] playerBlocked = new boolean[board.getNumberOfPlayers()];
        //playerBlocked[i] will store true if a corner of player numbered i+1 is a happened at the given square.
        //at most we can have 3 corners, one for each player except the current one

        try {
            int topLeft = grid[YPos - 1][XPos - 1];
            if (topLeft != 0 && topLeft != number && checkSides(YPos, XPos, grid, topLeft))
                playerBlocked[topLeft-1]=true;
        } catch (ArrayIndexOutOfBoundsException e){

        }
        try {
            int topRight = grid[YPos - 1][XPos + 1];
            if (topRight != 0 && topRight != number && checkSides(YPos, XPos, grid, topRight))
                playerBlocked[topRight-1]=true;
        } catch (ArrayIndexOutOfBoundsException e){

        }
        try {
            int bottomLeft = grid[YPos + 1][XPos - 1];
            if (bottomLeft != 0 && bottomLeft != number && checkSides(YPos, XPos, grid, bottomLeft))
                playerBlocked[bottomLeft-1]=true;
        } catch (ArrayIndexOutOfBoundsException e){

        }
        try {
            int bottomRight = grid[YPos + 1][XPos + 1];
            if (bottomRight != 0 && bottomRight != number && checkSides(YPos, XPos, grid, bottomRight))
                playerBlocked[bottomRight-1]=true;
        } catch (ArrayIndexOutOfBoundsException e){

        }
        int nbrOfBlocks = 0;
        for(int i = 0; i < playerBlocked.length; i++)
            if (playerBlocked[i])
                nbrOfBlocks++;
        return nbrOfBlocks;
    }

    /**
     * Checks whether a toCorner is actually valid (a piece can be placed there)
     * @param YPos y position of the square
     * @param XPos x position of the square
     * @param grid the board.board variable
     * @param playerNumber The number of the player who has the toCorner
     * @return true when all direct sides of (Ypos, XPos) are not playerNumber
     */
    private boolean checkSides(int YPos, int XPos, int[][] grid, int playerNumber){
        return (XPos - 1 < 0 || grid[YPos][XPos - 1] != playerNumber) &&//check left
                (YPos - 1 < 0 || grid[YPos - 1][XPos] != playerNumber) &&//check top
                (XPos + 1 >= grid[0].length || grid[YPos][XPos + 1] != playerNumber) &&//check right
                (YPos + 1 >= grid.length || grid[YPos + 1][XPos] != playerNumber);//check bottom
    }




    private float closestToMiddle(float weight, Move move, Board board) {
        //for every move, see how close the closest corner is to the middle.

        //TODO: It seems to have some trouble with the dot piece (I1). We might have to check that

        //for every move, see which of the corners of the piece is closest to the middle of the board.
        //score will be the minimal distance between any corner of the piece and middle. The closer the shorter the distance
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
        }

        //Alternative for normalization:
        //float maxDistance = (float) Math.sqrt(Math.pow(this.startingCorner.get_x() - middleXandY, 2) + Math.pow(this.startingCorner.get_y() - middleXandY, 2));

        //the score if weight was one needs to be between [0, 1]
        float normalization = (float) Math.sqrt(2*board.getDIMENSION()*board.getBoardDimension())/2;// this equals maxDistance
        float score = weight * (1 - minDistance / normalization);// 1 - normalized minDistance, because the score should be higher when minDistance is lower
        //This calculation is the same as calculating maxDistance - minDistance at every step, choosing the highest result and normalizing that.

        //now the  for each move is updated according to what we calculated and the weight given.
        return score;
        //movesAndScores.put(move, movesAndScores.get(move) + score);
    }

    private float biggestPiece(float weight, Move move, Board board){
        Piece piece = move.getPiece();
        //Max number of blocks in a piece is 5
        float normalization = 5;
        float score = weight * (piece.getNumberOfBlocks()/normalization);

        //add score to the corresponding piece
        //movesAndScores.put(move, movesAndScores.get(move) + score);
        return score;
    }

    //TODO TESTING
    private float addsMostCorners(float weight, Move move, Board board) {

        //for every move, see how many corners would be added.

        ArrayList<Corner> cornerContacts = move.getPiece().getCornersContacts(move.getPosition());
        //see how many of the corner of the added piece can be used to place another piece.
        /*
        * so for example a piece in the shape of an 'L might have 2 corners, however
        * one is in contact with one piece of the same colour(otherwise L would no be able to be placed)
        * The other corner might be free, or also in contact with a piece, we need to check that.
        * If one corner was free then we would assign 1, if 2 we would assign 2 and so on.....
        */


        int addedFreeCorners=0;
        for(Corner corner: cornerContacts ){
            //check they are 0 and not a BOARD corner.

            for(Vector2d toCornerPosition : corner.getToCornerPositions()){

                try {
                    if (board.boardArray[toCornerPosition.get_y()][toCornerPosition.get_x()] == 0){
                        addedFreeCorners++; //if the corner is empty and free to use
                    }
                    else if(board.boardArray[toCornerPosition.get_y()][toCornerPosition.get_x()]==this.number){
                        addedFreeCorners--; //you blocked a self-corner, at least one is blocked to put the piece there
                        //you might be adding a piece with 6 blocking corners, then that is a negative score
                       // System.out.println("blocked self corner");
                    }

                }catch (IndexOutOfBoundsException e){
                    ;//a free corner outside of the board makes no sense
                }
            }
        }
        //if (move.getPiece().getLabel().equals("X"))
        //System.out.println();

        //the score if weight was one needs to be between [-1, 1]
        //If it is negative it means, we added no new corners and we blocked some we had
        float normalization = 6; //the max number of added corner is 7, if we use the '+' piece and they are all free
        float score = weight * (addedFreeCorners / normalization);

        //now the  for each move is updated according to what we calculated and the weight given.
        //movesAndScores.put(move, movesAndScores.get(move) + score);
        return score;
    }


/**
     * strategy that calculates how far away from the starting corner a move is
     *
     * @param weight
     * @param move
     * @param board

 */
    private float farFromStartingCorner(float weight, Move move, Board board){

        //for every move, see which of the corner of the piece is furthest away from the starting corner.
        //score will be the distance between it and the corner. The further away the greater the distance
        ArrayList<Corner> cornerContacts = move.getPiece().getCornersContacts(move.getPosition());
        float maxDistance=-1;
        for(Corner corner : cornerContacts){
            Vector2d cornerPosition=corner.getPosition();
            float distance = cornerPosition.moduleDistance(this.startingCorner);
            //note that module distance will prioritize the side, as
            // a 4 squared 'I' will return a higher score that a 4 squared 'L'
            // take note of these for the future

            //System.out.println("corner position:" ); cornerPosition.printVector();
            //System.out.println("module distance:"+distance);
            if(distance > maxDistance){
                maxDistance = distance;
            }

        }

        //the score if weight was one needs to be between [0, 1]
        float normalization= (float) Math.sqrt(2*Math.pow(board.getDIMENSION(),2));
        //float normalization=1;
        float score = weight*(maxDistance/normalization);

        //now the  for each move is updated according to what we calculated and the weight given.
        //movesAndScores.put(move, movesAndScores.get(move) + score);
        return score;
/*
        //commented alternative IGNORE THIS!!!
        ArrayList<Move> possibleMoves = super.possibleMoveSet(board);
        for (Move move: possibleMoves){
            //for every move, see which of the corner of the piece is furthest awy from the starting corner.
            //score will be the distance between it and the corner. The further away the greater the distance
            ArrayList<Corner> cornerContacts=move.getPiece().getCornersContacts(move.getPosition());
            float maxDistance=-1;
            for(Corner corner: cornerContacts ){
                Vector2d cornerPosition=corner.getPosition();
                float distance= cornerPosition.moduleDistance(this.startingCorner);
                //note that module distance will prioritize the sides as a 4 squared 'I' will score higher than

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



    private void printBestMove(ArrayList<Move> bestMoves, float score){
        if (bestMoves.size() > 0){
            Move maxMove = bestMoves.get(0);//Just taking the first item in the list if there's a tie
            System.out.println("best move: ");
            System.out.println("score: " + score);
            System.out.println("piece: " + maxMove.getPiece().getLabel());
            maxMove.getPiece().printShape();
            System.out.println("coordinates: ");
            maxMove.getPosition().printVector();
            System.out.println();
            System.out.println();
        }
    }

    @Override
    public GeneticPlayer clone() {
        GeneticPlayer player = new GeneticPlayer(number);
        player.startingCorner=startingCorner;
        player.setWeightsAsArray(weights.clone());
        for(Piece p:piecesList) player.getPiecesList().add(p.clone());
        player.setFirstMove(isFirstMove());

        return player;
    }
}
