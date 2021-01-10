package MiniMax;

import DataBase.Data;
import DataBase.Piece;
import DataBase.PieceFactory;
import GameBoard.Board;
import GameBoard.Corner;
import Player.Player;
import Move.Move;
import Tools.Vector2d;
import Player.*;

import java.util.*;

public class MiniMax {
    Player[] players;//list of the players in the game
    Board boardOrigin;//origin object of the board
    Board board;//board being being "modified"
    private final int NBR_OF_TURNS = 1;//nbr of turn for the depth of the tree
    private int maxDepth;//max depth of the tree
    int rootPlayerNbr;//nbr of the player that needs to move
    float u = 0;//MAX-N SEARCH - upperbound of the sum of the score of each player (in order to cutoff nodes)
    ArrayList<Move> cutOffMoveOccurence;//KILLER MOVE STRATEGY (max-N) - list of the moves being cutoff the most during the pruning
    ArrayList<Move>[] cutOffMoveOccurenceP;//KILLER MOVE STRATEGY (Paranoid) - list of the moves of each player being cutoff the most during the pruning
    int killerMovesLength = 10;//KILLER MOVE STRATEGY - nbr max of cutoff moves
    private MiniMaxNode root;

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
    protected float[] maxHeuristics = new float[]{
            6,15,1,5,1
    };


    protected int[] phasesStartTurns = {5,12};

    /**
     * constructor of the algo class
     * @param players - list of the game players
     * @param board - object of the game board
     */
    public MiniMax(Player[] players, Board board){
        this.players = players;
        this.boardOrigin = board;
        maxDepth = players.length*NBR_OF_TURNS;
    }

    /**
     * method called when we want to calculate and get the move of a specific player
     * @param playerNbr - nbr of the player that has to make a move
     * @return
     */
    public Move getMove(int playerNbr){
        this.rootPlayerNbr = playerNbr;
        this.board = boardOrigin.clone();
        this.root = new MiniMaxNode(board,players[playerNbr-1].clone());

        Move bestMove;
        long start = System.currentTimeMillis(); //start of the timer
        if(Data.getPlayerTypes()[rootPlayerNbr-1]=="MiniMax-MaxN Player"){
            int state = players[playerNbr-1].getPiecesUsed().size();
            this.u = 0;
            if (state >= phasesStartTurns[0] && state < phasesStartTurns[1]){
                for (int i = 0; i < weights[0].length; i++) {
                    this.u+=weights[0][i]*maxHeuristics[i];
                }
            }else if (state >= phasesStartTurns[1]){
                for (int i = 0; i < weights[0].length; i++) {
                    this.u+=weights[1][i]*maxHeuristics[i];
                }
            }else {
                for (int i = 0; i < weights[0].length; i++) {
                    this.u+=weights[2][i]*maxHeuristics[i];
                }
            }
            //maxN
            this.cutOffMoveOccurence = new ArrayList<Move>(killerMovesLength);
            float[] score = maxN(root,maxDepth,playerNbr,Float.MIN_VALUE);
            System.out.print("best score = ");
            for (int i = 0; i < score.length; i++) {
                System.out.print(score[i] + ",");
            }
            System.out.println();
            bestMove = getBestMove(root,score,playerNbr);
            System.out.println("MOVE DONE USING MAX-N");
        }else{
            //paranoid
            this.cutOffMoveOccurenceP = new ArrayList[players.length];
            for (int i = 0; i < cutOffMoveOccurenceP.length; i++) {
                cutOffMoveOccurenceP[i] = new ArrayList<Move>();
            }
            float score = paranoidSearch(root,maxDepth,playerNbr,Float.MIN_VALUE,Float.MAX_VALUE);
            System.out.println("best score = " + score);
            bestMove = getpBestMove(root,score,playerNbr);
            System.out.println("MOVE DONE USING PARANOID");
        }
        long stop = System.currentTimeMillis()-start ;//end of the timer
        System.out.println(stop + "millisecond(s)");

        return bestMove;
    }
    /////////////////////////////////////
    //SEARCHES

    /**FOR THE PARANOID SEARCH
     * method used to get the best move for the root player after performing the pruning
     * @param root
     * @param score
     * @param rootPlayerNbr
     * @return
     */
    private Move getpBestMove(MiniMaxNode root,float score,int rootPlayerNbr){
        for(MiniMaxNode c: root.getChildren()) {
            if (c.getpScore()==-score) return new Move(players[rootPlayerNbr-1],c.getMove().getPiece(), c.getMove().getPosition());
        }
        System.out.println("oops"+root.getChildren().size());
        return null;
    }

    /** FOR THE MAX-N SEARCH
     * method used to get the best move for the root player after performing the pruning
     * @param root - root node
     * @param score - best score
     * @param rootPlayerNbr - nbr of the player that has to make a move
     * @return the best move of the minimax tree
     */
    private Move getBestMove(MiniMaxNode root,float[]score,int rootPlayerNbr){
        for(MiniMaxNode c: root.getChildren()) {
            boolean same = true;
            for (int i = 0; i < score.length; i++) {
                if (c.getScore()[i]!=score[i]) same=false;
            }
            if(same) return new Move(players[rootPlayerNbr-1],c.getMove().getPiece(), c.getMove().getPosition());
        }
        System.out.println("oops"+root.getChildren().size());
        return null;
    }

    /**
     * maxN multiplayer minimax algorithm
     * @param node - current node being investigated
     * @param depth - current depth of the algo
     * @param playerNbr - current player nbr
     * @param alpha - current alpha of the minimax algo
     * @return the best score of the root player
     */
    private float[] maxN(MiniMaxNode node, int depth, int playerNbr, float alpha){
        //if we have reached a terminal node (leaf) we set the score as the heuristics and then backtrack
        if(depth<=0) {
            node.setScore(getScore(node));
            return node.getScore();
        }
        //list of the best score for each player
        float[] best =new float[players.length];
        for (int i = 0; i < best.length; i++) best[i] = Float.MIN_VALUE;
        //check for each child of the current node using the killer moves strategy
        ArrayList<Move>possibleMoves = getPossibleMoves(playerNbr);
        for (Move possibleMove : possibleMoves){
            boolean firstTurn = players[playerNbr-1].isFirstMove();
            //create new child
            MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,players[playerNbr-1],board);
            int nextPlayerNbr;
            if(playerNbr>=players.length){
                nextPlayerNbr = 1;
            }else{
                nextPlayerNbr = playerNbr+1;
            }
            //get the best list of that child
            float[] result = maxN(newNode, depth-1, nextPlayerNbr, best[playerNbr-1]);
            newNode.removeMove();
            if(firstTurn) players[playerNbr-1].setFirstMove(true);
            //if the score of the current player in the current node in the tree is smaller than the score of the current player in the node child
            //then there is a cutoff, and we keep the score of the child
            if(result[playerNbr-1]>best[playerNbr-1]){
                best = result;
            }
            //if the score of the current player in the current node in the tree is bigger than the score of the current player in the node parent
            //then we set the score of the current node as the one of the child
            //cutoff
            if(result[playerNbr-1]>=this.u-alpha) {
                node.setKillerMoves(newNode.getMove());
                checkToAddToCutOff(newNode.getMove());
                node.setScore(result);
                return result;
            }
        }
        node.setScore(best);
        return best;
    }

    /** PARANOID SEARCH
     * Paranoid multiplayer minimax algorithm
     * @param node - current node being investigated
     * @param depth - current depth of the algo
     * @param playerNbr - current player nbr
     * @param alpha - current alpha of the minimax algo
     * @param beta - current beta of the algo
     * @return the best score of the root player
     */
    private float paranoidSearch(MiniMaxNode node, int depth, int playerNbr, float alpha, float beta) {
        //if we reach the leaves, we set the score as the heuristics and then backtrack
        if(depth<=0){
            if(playerNbr==rootPlayerNbr) {
                node.setpScore(getPScore(node));
                return node.getpScore();
            }else{
                System.out.println("STOP RECURSION COND., not possible, smth wrong");
                //never here because we never reach a terminal node that is not the root player
                node.setpScore(-getPScore(node));
                return node.getpScore();
            }
        }
        //check for each child of the current node using the killer moves strategy
        ArrayList<Move>possibleMoves = getPossibleMovesP(playerNbr);
        for (Move possibleMove : possibleMoves){
            boolean firstTurn = players[playerNbr-1].isFirstMove();
            //create new child
            MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,players[playerNbr-1],board);
            //calculate the next player nbr
            int nextPlayerNbr;
            if(playerNbr>=players.length){
                nextPlayerNbr = 1;
            }else{
                nextPlayerNbr = playerNbr+1;
            }
            //check whether the current or next player are the root player, and then calculate the new alpha value
            if(playerNbr==rootPlayerNbr || nextPlayerNbr==rootPlayerNbr){
                alpha = Math.max(alpha, -paranoidSearch(newNode,depth-1,nextPlayerNbr,-beta,-alpha));
            }else{
                alpha = Math.max(alpha, paranoidSearch(newNode,depth-1,nextPlayerNbr,alpha,beta));
            }
            newNode.removeMove();
            if(firstTurn){
                players[playerNbr-1].setFirstMove(true);
            }
            //cutoff
            if(alpha>=beta){
                node.setKillerMoves(newNode.getMove());
                checkToAddToCutOffP(newNode.getMove());
                node.setpScore(beta);
                return beta;
            }
        }
        node.setpScore(alpha);
        return alpha;
    }


    /////////////////////////////////
    //KILLER MOVE STRATEGY

    /** FOR THE MAX-N SEARCH
     * METHOD USED FOR THE KILLER MOVE STRATEGY THAT MINIMIZES THE NUMBER OF ITERATIONS
     * @param playerNbr - nbr of the player that has to make a move
     * @return a list of first the cutoff nodes (KILLER MOVES STARTEGY) and then all the other possible moves of that player
     */
    private ArrayList<Move> getPossibleMoves(int playerNbr){
        ArrayList<Move> possibleMovesBoosted = new ArrayList<Move>();
        ArrayList<Move> possibleMoves = players[playerNbr-1].possibleMoveSet(board);
        for (Move move:cutOffMoveOccurence) {
            if(possibleMoves.contains(move)){
                possibleMovesBoosted.add(move);
                possibleMoves.remove(move);
            }
        }
        possibleMovesBoosted.addAll(possibleMoves);
        return possibleMovesBoosted;
    }

    /** FOR THE MAX-N SEARCH
     * METHOD USED FOR THE KILLER MOVE STRATEGY THAT UPDATE THE LIST OF THE CUTOFF MOVE OCCURENCE
     * @param move that is a cutoff in our algo
     */
    public void checkToAddToCutOff(Move move){
        if(cutOffMoveOccurence.isEmpty()){
            cutOffMoveOccurence.add(move);
        }else{
            if (cutOffMoveOccurence.contains(move)) {
                move.setOccurence(move.getOccurence() + 1);
            }else if (cutOffMoveOccurence.size() < killerMovesLength) {
                cutOffMoveOccurence.add(move);
            }else{
                deleteSmallestOccMove();
                //replace the move in the cutoff list that has the fewest occurence with the current cutoff move
                cutOffMoveOccurence.add(move);
            }
        }
    }

    /** FOR THE MAX-N SEARCH
     * delete the move in the cutoff list that has the fewest occurence (will be replaced by a new one)
     */
    public void deleteSmallestOccMove(){
        Move smallestMove = null;
        int min = Integer.MAX_VALUE;
        for(Move m : cutOffMoveOccurence){
            if(m.getOccurence()<min){
                min = m.getOccurence();
                smallestMove = m;
            }
        }
        cutOffMoveOccurence.remove(smallestMove);
    }


    /** FOR THE PARANOID SEARCH
     * METHOD USED FOR THE KILLER MOVE STRATEGY THAT MINIMIZES THE NUMBER OF ITERATIONS
     * @param playerNbr - nbr of the player that has to make a move
     * @return a list of first the cutoff nodes (KILLER MOVES STARTEGY) and then all the other possible moves of that player
     */
    private ArrayList<Move> getPossibleMovesP(int playerNbr){
        ArrayList<Move> possibleMovesBoosted = new ArrayList<Move>();
        ArrayList<Move> possibleMoves = players[playerNbr-1].possibleMoveSet(board);
        for (Move move:cutOffMoveOccurenceP[playerNbr-1]) {
            if(possibleMoves.contains(move)){
                possibleMovesBoosted.add(move);
                possibleMoves.remove(move);
            }
        }
        possibleMovesBoosted.addAll(possibleMoves);
        return possibleMovesBoosted;
    }

    /** FOR THE PARANOID SEARCH
     * METHOD USED FOR THE KILLER MOVE STRATEGY THAT UPDATE THE LIST OF THE CUTOFF MOVE OCCURENCE
     * @param move that is a cutoff in our algo
     */
    public void checkToAddToCutOffP(Move move){
        if(cutOffMoveOccurenceP[move.getPlayer().getNumber()-1].isEmpty()){
            cutOffMoveOccurenceP[move.getPlayer().getNumber()-1].add(move);
        }else{
            if (cutOffMoveOccurenceP[move.getPlayer().getNumber()-1].contains(move)) {
                move.setOccurence(move.getOccurence() + 1);
            }else if (cutOffMoveOccurenceP[move.getPlayer().getNumber()-1].size() < killerMovesLength) {
                cutOffMoveOccurenceP[move.getPlayer().getNumber()-1].add(move);
            }else{
                deleteSmallestOccMoveP(move.getPlayer().getNumber()-1);
                //replace the move in the cutoff list that has the fewest occurence with the current cutoff move
                cutOffMoveOccurenceP[move.getPlayer().getNumber()-1].add(move);
            }
        }
    }

    /** FOR THE PARANOID SEARCH
     * delete the move in the cutoff list that has the fewest occurence (will be replaced by a new one)
     */
    public void deleteSmallestOccMoveP(int playerNbr){
        Move smallestMove = null;
        int min = Integer.MAX_VALUE;
        for(Move m : cutOffMoveOccurenceP[playerNbr]){
            if(m.getOccurence()<min){
                min = m.getOccurence();
                smallestMove = m;
            }
        }
        cutOffMoveOccurenceP[playerNbr].remove(smallestMove);
    }

    ///////////////////////////////////////////////
    //HEURISTICS

    /** FOR THE MAX-N SEARCH
     * method used to get the nbr of corner blocked by the move of the node for each player
     * @param node current node being scored
     * @return the nbr of corner blocked by the move of the node for each player
     */
    private float[] blocksMostCorners(MiniMaxNode node){
        float[] score = new float[players.length];
        for(int k = 0;k<players.length;k++){
            Piece piece = node.getMove().getPiece();
            int[][] shape = piece.getShape();
            Vector2d position = node.getMove().getPosition();

            int blockCornerNumber = 0;
            for (int i = 0; i < shape.length; i++){
                for (int j = 0; j < shape[0].length; j++){
                    if (shape[i][j] != 0){
                        int nbrCornersBlocked = isDiffPlayerToCorner(i + position.get_y(), j + position.get_x(), board,players[k].getPlayerNumber());
                        blockCornerNumber+=nbrCornersBlocked;
                    }
                }
            }
            score[k] = blockCornerNumber;
        }

        return score;
    }

    /** FOR THE MAX-N SEARCH
     * This method checks whether a certain coordinate is a toCorner of a different player.
     * So it checks if on that square, a different player could make a move
     * @param YPos y position of the square
     * @param XPos x position of the square
     * @param board current game board
     * @return the number of players for which the given square was a corner
     */
    private int isDiffPlayerToCorner(int YPos, int XPos, Board board, int playerNbr){
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
            if (topLeft != 0 && topLeft != playerNbr && checkSides(YPos, XPos, grid, topLeft))
                playerBlocked[topLeft-1]=true;
        } catch (ArrayIndexOutOfBoundsException e){

        }
        try {
            int topRight = grid[YPos - 1][XPos + 1];
            if (topRight != 0 && topRight != playerNbr && checkSides(YPos, XPos, grid, topRight))
                playerBlocked[topRight-1]=true;
        } catch (ArrayIndexOutOfBoundsException e){

        }
        try {
            int bottomLeft = grid[YPos + 1][XPos - 1];
            if (bottomLeft != 0 && bottomLeft != playerNbr && checkSides(YPos, XPos, grid, bottomLeft))
                playerBlocked[bottomLeft-1]=true;
        } catch (ArrayIndexOutOfBoundsException e){

        }
        try {
            int bottomRight = grid[YPos + 1][XPos + 1];
            if (bottomRight != 0 && bottomRight != playerNbr && checkSides(YPos, XPos, grid, bottomRight))
                playerBlocked[bottomRight-1]=true;
        } catch (ArrayIndexOutOfBoundsException e){

        }
        int nbrOfBlocks = 0;
        for(int i = 0; i < playerBlocked.length; i++)
            if (playerBlocked[i])
                nbrOfBlocks++;
        return nbrOfBlocks;
    }

    /** FOR THE MAX-N SEARCH
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

    /** FOR THE MAX-N SEARCH
     *used to get the nbr of blocks for each player
     * @param board - board of the game
     * @return the nbr of blocks for each player
     */
    public float[] getBlocksScore(Board board){
        float[] res = new float[players.length];
        for (int i = 0; i < players.length; i++) {
            res[i] = 0;
        }
        for(int[] line:board.boardArray) for(int i:line) if(i!=0) res[i-1]++;
        return res;
    }

    /** FOR THE MAX-N SEARCH
     * method used to get the area between the move and the center for each player
     * @param node - node currently being scored
     * @return the area between the move and the center for each player
     */
    private float[] getCloseToCenter(MiniMaxNode node){
        MiniMaxNode current = node;
        float[]score = new float[players.length];
        while(current!=root){
            Move move = current.getMove();
            double maxDist = Math.sqrt(Math.pow(board.getDIMENSION() / 2, 2) * 2);
            score[current.getPlayer().getNumber()-1] = (float) ((maxDist - Math.sqrt(Math.pow(board.getDIMENSION() / 2 - (move.getPosition().get_x() + (move.getPiece().getShape()[0].length) / 2.0), 2) +
                    Math.pow(board.getDIMENSION() / 2 - (move.getPosition().get_y() + (move.getPiece().getShape().length) / 2.0), 2)))/maxDist);
            current = current.getParent();
        }



        return score;
    }

    /** FOR THE MAX-N SEARCH
     * method used to get the area between the move and the starting point for each player
     * @param node - node currently being scored
     * @return the area between the move and the starting point for each player
     */
    private float[] getFarest(MiniMaxNode node){
        MiniMaxNode current = node;
        float[]score = new float[players.length];
        while(current!=root) {
            Player p = current.getPlayer();
            Move move = current.getMove();
            double maxDist = 0;
            for (Corner c : move.getPiece().getCornersContacts(move.getPosition())) {
                double dist = Math.sqrt(Math.pow(c.getPosition().get_x() - p.getStartingCorner().get_x(), 2) + Math.pow(c.getPosition().get_y() - p.getStartingCorner().get_y(), 2));
                if (dist > maxDist) maxDist = dist;
            }
            score[current.getPlayer().getNumber()-1] = (float) (maxDist / Math.sqrt(board.getDIMENSION() * 2));
            current = current.getParent();
        }
        return score;
    }

    /** FOR THE MAX-N SEARCH
     * method used to normalize each heuristics
     * @param score - score of each player at the current node being scored
     * @param heuristics - heuristics that is being normalized
     * @param maximum - maximum of the heuristics
     * @return the score with the heuristics normalized
     */
    public float[] normalize(float[]score,float[]heuristics,float maximum){
        float total = 0;
        for (int i = 0;i<heuristics.length;i++) {
            total+= heuristics[i];
        }
        for (int i = 0;i<heuristics.length;i++) {
            score[i]+=heuristics[i]*maximum/total;
        }
        return score;
    }

    /** FOR THE MAX-N SEARCH
     * used to get the Score of the node for each player
     * @param node - node currently being scored
     * @return the score of the node move for each player
     */
    public float[] getScore(MiniMaxNode node) {
        float[] score = new float[players.length];
        //GET THE BEST WEIGHT
        float[] wei;
        int state = node.getPlayer().getPiecesUsed().size();
        if (state >= phasesStartTurns[0] && state < phasesStartTurns[1]){
            wei = this.weights[0];
        }else if (state >= phasesStartTurns[1]){
            wei = this.weights[1];
        }else {
            wei = this.weights[2];
        }

        //ADD MOST CORNERS
        float[] nbrOfCorner = new float[players.length];
        for(int i=0; i<players.length;i++) nbrOfCorner[i]=board.getCorner(players[i].getStartingCorner()).size();
        score = normalize(score,nbrOfCorner,wei[0]/maxHeuristics[0]);

        //BLOCK MOST CORNERS
        float[] nbrOfCornerBlocked = blocksMostCorners(node);
        score = normalize(score,nbrOfCornerBlocked,wei[1]/maxHeuristics[1]);

        //CLOSEST TO MIDDLE
        score = normalize(score,getCloseToCenter(node),wei[2]/maxHeuristics[2]);

        //BIGGEST PIECE
        score = normalize(score,getBlocksScore(node.getBoard()),wei[3]/maxHeuristics[3]);

        //FAR FROM STARTING POINT
        score = normalize(score,getFarest(node),wei[4]/maxHeuristics[4]);

        node.setScore(score);
        return score;
    }


    /** FOR THE PARANOID SEARCH
     * method returns and set the heuristic score to the current node
     * @param node current node being scored
     * @return the heuristic score of the current node
     */
    public float getPScore(MiniMaxNode node){
        int state = players[rootPlayerNbr-1].getPiecesUsed().size();
        float[] heuristics = new float[5];

        //ADD MOST CORNERS
        heuristics[0] = (board.getCorner(players[rootPlayerNbr-1].getStartingCorner()).size())/6;

        Piece piece = node.getMove().getPiece();
        int[][] shape = piece.getShape();
        Vector2d position = node.getMove().getPosition();
        int blockCornerNumber = 0;
        for (int i = 0; i < shape.length; i++){
            for (int j = 0; j < shape[0].length; j++){
                if (shape[i][j] != 0){
                    int nbrCornersBlocked = isDiffPlayerToCorner(i + position.get_y(), j + position.get_x(), board,players[rootPlayerNbr-1].getPlayerNumber());
                    blockCornerNumber+=nbrCornersBlocked;
                }
            }
        }
        //BLOCK MOST CORNERS
        heuristics[1] =  blockCornerNumber/15;

        //CLOSEST TO MIDDLE
        Move move = node.getMove();
        double maxDist = Math.sqrt(Math.pow(board.getDIMENSION() / 2, 2) * 2);
        heuristics[2] =  (float) ((maxDist - Math.sqrt(Math.pow(board.getDIMENSION() / 2 - (move.getPosition().get_x() + (move.getPiece().getShape()[0].length) / 2.0), 2) +
                Math.pow(board.getDIMENSION() / 2 - (move.getPosition().get_y() + (move.getPiece().getShape().length) / 2.0), 2)))/maxDist);

        //BIGGEST PIECE
        float nbrOfBlocks = 0;
        for (int i = 0; i < board.boardArray.length; i++) {
            for (int j = 0; j < board.boardArray[i].length; j++) {
                if(board.boardArray[i][j]==players[rootPlayerNbr-1].getPlayerNumber()){
                    nbrOfBlocks++;
                }
            }
        }
        heuristics[3]=nbrOfBlocks/5;

        //FAR FROM STARTING POINT
        Player p = node.getPlayer();
        double maxDist1 = 0;
        for(Corner c: move.getPiece().getCornersContacts(move.getPosition())){
            double dist = Math.sqrt(Math.pow(c.getPosition().get_x()-p.getStartingCorner().get_x(),2)+Math.pow(c.getPosition().get_y()-p.getStartingCorner().get_y(),2));
            if(dist>maxDist1) maxDist1=dist;
        }
        heuristics[4] = (float)(maxDist1/Math.sqrt(board.getDIMENSION()*2));

        //PUT THE HEURISTICS TOGETHER DEPENDING ON THE STATE OF THE GAME
        float score = 0;
        if (state >= phasesStartTurns[0] && state < phasesStartTurns[1]){
            for (int i = 0; i < weights[0].length; i++) {
                score+=weights[0][i]*heuristics[i];
            }
        }else if (state >= phasesStartTurns[1]){
            for (int i = 0; i < weights[1].length; i++) {
                score+=weights[1][i]*heuristics[i];
            }
        }else {
            for (int i = 0; i < weights[2].length; i++) {
                score+=weights[2][i]*heuristics[i];
            }
        }
        return score;
    }

    ////////////////////////////////
    //TESTING

    public static void main(String[] args) {
        Player p1 = new HumanPlayer(1, "jo");
        Player p2 = new HumanPlayer(2, "notJo");
        Player p3 = new HumanPlayer(3, "jo2");
        Player p4 = new HumanPlayer(4, "notJo2");
        p1.setStartingCorner(new Vector2d(0, 0));
        //p2.setStartingCorner(new Vector2d(19, 0));
        p2.setStartingCorner(new Vector2d(19, 19));
        p3.setStartingCorner(new Vector2d(19, 19));
        p4.setStartingCorner(new Vector2d(0, 19));
        p1.setPiecesList(PieceFactory.get().getAllPieces());
        p2.setPiecesList(PieceFactory.get().getAllPieces());
        p3.setPiecesList(PieceFactory.get().getAllPieces());
        p4.setPiecesList(PieceFactory.get().getAllPieces());

        Data.setPlayerTypes(new String[]{"MiniMax-MaxN Player","MiniMax-Paranoid Player"});

        Board b = new Board(new Player[]{p1, p2});
        MiniMax m = new MiniMax(new Player[]{p1,p2},b);

        int i= 0;
        while(i<15){
            Move move1 = m.getMove(p1.getPlayerNumber());
            if(move1.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());
            b.print();


            Move move2 = m.getMove(p2.getPlayerNumber());
            if(move2.makeMove(b)) p2.removePiece(move2.getPiece().getLabel());
            b.print();

/*
            Move move3 = m.getMove(p3.getPlayerNumber());
            if(move3.makeMove(b)) p3.removePiece(move1.getPiece().getLabel());
            b.print();

            Move move4 = m.getMove(p4.getPlayerNumber());
            System.out.println(Arrays.toString(m.cutOffMoveOccurence.keySet().toArray()));
            if(move4.makeMove(b)) p4.removePiece(move1.getPiece().getLabel());
            b.print();
 */
            i++;
        }


    }
}