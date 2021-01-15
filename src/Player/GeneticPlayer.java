package Player;

import DataBase.Piece;
import GameBoard.Board;
import GameBoard.Corner;
import Move.Move;
import Tools.Vector2d;

import java.util.ArrayList;


public class GeneticPlayer extends BotPlayer {

    public static final int NUMBER_OF_STRATEGIES = 5;
    public static final int NUMBER_OF_PHASES = 2;
    private int turn = 1;
    private int phase = 0;
    private int customDepth;
    private boolean depthIsCustom = false;

    /*
    weights[0] = addMostCorners
    weights[1] = blocksMostCorners
    weights[2] = closestToMiddle
    weights[3] = biggestPiece
    weights[4] = farFromStartingPoint
     */
    protected float[][] weights = new float[][]{
            {0.5312532f, 0.38739195f, 0.6310179f, 0.80074483f, 0.43988678f},
    { 0.03257805f, 0.21643633f, 0.56497073f, 0.8927979f, 0.68175447f},
    {0.06342363f, 0.5615145f, 0.099959135f, 0.94023997f, 0.013810515f},
                                                                            };
    protected int[] phasesStartTurns = {5,12};
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
    public GeneticPlayer(int number, float[][] weights) {
        super(number);
        this.weights=weights;
    }

    public int getPhase(){
        determinePhase();
        return phase;
    }

    public int[] getPhasesStartTurns(){
        return phasesStartTurns;
    }

    public void setPhasesStartTurns(int[] phasesStartTurns){
        this.phasesStartTurns = phasesStartTurns;
    }

    public float[] getCurrentWeightsAsArray() {
        return currentWeights;
    }

    public float[][] getWeightsAsArray(){
        return weights;
    }

    public void setCurrentWeightsAsArray(float[] weights, int phase){
        this.weights[phase]=weights;
    }

    public void setWeightsAsArray(float[][] weights){
        this.weights = weights;
    }

    public void setWeightAddMostCorners(float weight, int phase){
        this.weights[phase][0]=weight;
    }

    public float getWeightAddMostCorners(int phase){
        return this.weights[phase][0];
    }

    public void setWeightBlocksMostCorners(float weight, int phase){
        this.weights[phase][1]=weight;
    }

    public float getWeightBlocksMostCorners(int phase){
        return this.weights[phase][1];
    }

    public void setWeightClosestToMiddle(float weight, int phase){
        this.weights[phase][2]=weight;
    }

    public float getWeightClosestToMiddle(int phase){
        return this.weights[phase][2];
    }

    public void setWeightBiggestPiece(float weight, int phase){
        this.weights[phase][3]=weight;
    }

    public float getWeightBiggestPiece(int phase){
        return this.weights[phase][3];
    }

    public void setWeightFarFromStartingPoint(float weight, int phase){
        this.weights[phase][4]=weight;
    }

    public float getWeightFarFromStartingPoint(int phase){
        return this.weights[phase][4];
    }

    public void addTurn(){
        turn++;
    }

    public void resetTurn(){
        turn = 0;
    }

    protected void determinePhase(){
        if (turn >= phasesStartTurns[0] && turn < phasesStartTurns[1]){
            currentWeights = weights[1];

            phase = 1;
        }else if (turn >= phasesStartTurns[1]){
            currentWeights = weights[2];
            phase = 2;
        }else {
            currentWeights = weights[0];
            phase = 0;
        }
        //System.out.println("Phase" +phase);
    }

    public void setDepth(int depth){
        customDepth = depth;
        depthIsCustom = true;
    }

    public void setDepthToDefault(){
        depthIsCustom = false;
    }

    public boolean getDepthIsCuston(){
        return depthIsCustom;
    }

    public void setDepthIsCustom(boolean depthIsCustom) {
        this.depthIsCustom = depthIsCustom;
    }

    public int getCustomDepth() {
        return customDepth;
    }

    public void setCustomDepth(int customDepth) {
        this.customDepth = customDepth;
    }

    /**
     *
     * @param board The state for which the ai will try to find a move
     * @return the move chosen by the ai
     */
    public Move calculateMove(Board board){
        determinePhase();
        //System.out.println("in turn "+turn + "weights");
        /*
        for (int i = 0; i < this.currentWeights.length; i++) {

                System.out.println(this.currentWeights[i]);
            }
*/

       //while we code all the different strategies, make this call the one you want to try
        //System.out.println("In calculate move for genetic algorithm");


        int depth = phase+1;
        if (depthIsCustom){
            depth = customDepth;
        }

        //New method test
        chosenMove = null;
        expandAndEvaluate(board.clone(), this, depth);
        if (chosenMove != null && chosenMove.getPlayer().getPlayerNumber() != getPlayerNumber()){
            System.out.println("wtf");
        }
        if (chosenMove == null || chosenMove.getPlayer() != this){
            //System.out.println("huh, maybe this player has no moves left?");
            chosenMove = null;
        }
        //chosenMove.print();

        /*
        float bestScore = -1000;
        ArrayList<Move> bestMoves = new ArrayList<Move>();

        for (Move move : super.possibleMoveSet(board)) {
            float score = 0;
            //uncomment to test a strategy

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
             *//*

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
        }*/

        return chosenMove;
    }


    //This variable is used to track which move eventually gets chosen
    private Move chosenMove = null;
    /**
     * Recursive method
     * This method goes through all possible moves from the given player. For each move, it calculates a score.
     * Then it performs this move and tries to find the score of the best move of the next player on this newly acquired board.
     * If the score for this best move is high, then that is bad for the current player. So we subtract this score from
     * the score of the move of the current player. Then, the performed move will be undone and it tries the next move.
     * When all moves have been tried, the move with the best score gets chosen.
     * @param board The state for which this method tries to find the best move
     * @param player the player for which this method tries to find the best move
     * @param depth The depth this method will go into the search tree
     */
    protected float expandAndEvaluate(Board board, Player player, int depth){
        //Recursion business
        depth--;
        if (depth == -1){
            return 0;
        }

        int nextPlayerNbr = player.getPlayerNumber() + 1;
        if (nextPlayerNbr > board.getNumberOfPlayers()){
            nextPlayerNbr = 1;
        }
        Player nextPlayer = board.getPlayers()[nextPlayerNbr - 1];

        ArrayList<Move> moves = player.possibleMoveSet(board);
        //If there are no legal moves left, skip this turn
        if (moves.isEmpty()){
            player.setSkippedLastMove(true);

            if (noOneMoved(board.getPlayers())){
                //Maybe some score business
                int points = 0;
                for (Player p : board.getPlayers()){
                    if (p.getPlayerNumber() == player.getPlayerNumber()) {
                        points += countPointsPlayer(p);
                    } else{
                        points -= countPointsPlayer(p);
                    }
                }

                player.setSkippedLastMove(false);
                return points;//maybe *100 because points are the most important thing?
            }

            //Go to next player (they might still have moves)
            float points = expandAndEvaluate(board, nextPlayer, depth);
            player.setSkippedLastMove(false);
            return points;
        }

        ArrayList<Piece> pieces = (ArrayList<Piece>) player.getPiecesList();

        float maxScore = -100000;
        Move bestMove = null;

        for (Move move : moves){
            //We have to pick out the used piece like this, because possibleMoveSet() returns cloned objects.
            //This way we can better keep track of which pieces have been used
            String pieceLabel = move.getPiece().getLabel();
            Piece usedPiece = null;
            for (Piece piece : pieces){
                if (piece.getLabel().equals(pieceLabel)){
                    usedPiece = piece;
                    break;
                }
            }
            //If the piece in this move is already used, we can't use it again
            if (usedPiece.isUsed()){
                continue;
            }

            //Scoring business
            //Have to check whether the scoring will still work when calculating for different players
            float score = 0;
            score += addsMostCorners(currentWeights[0], move, board, player);
            score += blocksMostCorners(currentWeights[1], move, board);
            score += closestToMiddle(currentWeights[2], move, board);
            score += biggestPiece(currentWeights[3], move, board);
            score += farFromStartingCorner(currentWeights[4], move, board);

            //Track whether it's the first move
            boolean moveNbrOne = player.isFirstMove();
            //Perform move
            move.writePieceIntoBoard(board);
            usedPiece.setUsed(true);
            player.getMoveLog().push(move);

            //Do recursive call
            score -= expandAndEvaluate(board, nextPlayer, depth);

            //Undo move
            move.removePieceFromBoard(board);
            player.setFirstMove(moveNbrOne);
            usedPiece.setUsed(false);
            player.getMoveLog().pop();

            //check whether this is currently the best move
            if (score > maxScore){
                maxScore = score;
                bestMove = move;
            }
        }

        chosenMove = bestMove;

        //return only the score of the best move
        return maxScore;
    }

    protected boolean noOneMoved(Player[] players){
        for (Player player: players){
            if(!player.getSkippedLastMove())
                return false;
        }
        return true;
    }

    public int countPointsPlayer(Player player){
        //System.out.println("sim end");
        int points=0;
        int piecesPlaced=0;
        for (Piece piece : player.getPiecesList()){
            if (piece.isUsed()){
                piecesPlaced++;
            } else{
                points -= piece.getNumberOfBlocks();
            }
        }

        if (piecesPlaced == 21){//if I check if number of unused pieces is 0, the size might be 1 when it should be 0 cause the deletion is done in the ui logic after the make move...but just maybe, I have not checked
            Piece lastPiece = player.getMoveLog().peek().getPiece();
            if(lastPiece.getNumberOfBlocks() == 1)
                points+=20;
            else
                points+=15;

        }

        return points;
    }


    protected float blocksMostCorners(float weight, Move move, Board board){
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

        if (YPos - 1 >= 0 && XPos - 1 >= 0){
            int topLeft = grid[YPos - 1][XPos - 1];
            if (topLeft != 0 && topLeft != number && checkSides(YPos, XPos, grid, topLeft))
                playerBlocked[topLeft-1]=true;
        }
        if (YPos - 1 >= 0 && XPos + 1 < grid[0].length){
            int topRight = grid[YPos - 1][XPos + 1];
            if (topRight != 0 && topRight != number && checkSides(YPos, XPos, grid, topRight))
                playerBlocked[topRight-1]=true;
        }
        if (YPos + 1 < grid.length && XPos - 1 >= 0){
            int bottomLeft = grid[YPos + 1][XPos - 1];
            if (bottomLeft != 0 && bottomLeft != number && checkSides(YPos, XPos, grid, bottomLeft))
                playerBlocked[bottomLeft-1]=true;
        }
        if (YPos + 1 < grid.length && XPos + 1 < grid[0].length) {
            int bottomRight = grid[YPos + 1][XPos + 1];
            if (bottomRight != 0 && bottomRight != number && checkSides(YPos, XPos, grid, bottomRight))
                playerBlocked[bottomRight-1]=true;
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




    protected float closestToMiddle(float weight, Move move, Board board) {
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

    protected float biggestPiece(float weight, Move move, Board board){
        Piece piece = move.getPiece();
        //Max number of blocks in a piece is 5
        float normalization = 5;
        float score = weight * (piece.getNumberOfBlocks()/normalization);

        //add score to the corresponding piece
        //movesAndScores.put(move, movesAndScores.get(move) + score);
        return score;
    }

    //TODO TESTING
    protected float addsMostCorners(float weight, Move move, Board board, Player player) {

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

                if (toCornerPosition.get_y() >= 0 && toCornerPosition.get_y() < board.getDIMENSION() &&
                    toCornerPosition.get_x() >= 0 && toCornerPosition.get_x() < board.getDIMENSION()) {
                    if (board.boardArray[toCornerPosition.get_y()][toCornerPosition.get_x()] == 0) {
                        addedFreeCorners++; //if the corner is empty and free to use
                    } else if (board.boardArray[toCornerPosition.get_y()][toCornerPosition.get_x()] == player.getPlayerNumber()) {
                        addedFreeCorners--; //you blocked a self-corner, at least one is blocked to put the piece there
                        //you might be adding a piece with 6 blocking corners, then that is a negative score
                        // System.out.println("blocked self corner");
                    }
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
    protected float farFromStartingCorner(float weight, Move move, Board board){

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
        player.setName(this.name);
        player.setStartingCorner(this.startingCorner);
        for(Piece piece : this.piecesList) player.getPiecesList().add(piece.getPiece());
        player.setFirstMove(this.firstMove);
        player.humanPlayer = false;
        player.setColor(this.color);
        player.setPoints(this.points);

        for (int i = 0; i < player.getWeightsAsArray().length; i++){
            player.getWeightsAsArray()[i] = this.weights[i].clone();
        }
        player.turn = this.turn;
        player.phase = this.phase;
        player.customDepth = this.customDepth;
        player.depthIsCustom = this.depthIsCustom;

        return player;
    }


}
