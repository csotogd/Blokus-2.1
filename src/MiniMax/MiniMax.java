package MiniMax;

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
    Player[] players;
    Board boardOrigin;
    Board board;
    private final int NBR_OF_TURNS = 1;
    private int maxDepth;
    int rootPlayerNbr;
    ArrayList<Move> cutOffMoveOccurence;
    float u = 30;
    int killerMovesLength = 10;

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
        //long start = System.currentTimeMillis(); //start of the timer
        this.rootPlayerNbr = playerNbr;
        this.cutOffMoveOccurence = new ArrayList<Move>(killerMovesLength);
        this.board = boardOrigin.clone();
        MiniMaxNode root = new MiniMaxNode(board,players[playerNbr-1].clone());
        Move bestMove = null;

        long start = System.currentTimeMillis(); //start of the timer
        if(players[playerNbr-1].getPiecesUsed().size()<0){
            //maxN
            float[] score = maxN(root,maxDepth,playerNbr,Float.MIN_VALUE);
            bestMove = getBestMove(root,score,playerNbr);
            System.out.println("MOVE DONE USING MAX-N");
        }else{
            //paranoid
            float score = paranoidSearch(root,maxDepth,playerNbr,Float.MIN_VALUE,Float.MAX_VALUE);
            System.out.println("best score = " + score);
            bestMove = getpBestMove(root,score,playerNbr);
            System.out.println("MOVE DONE USING PARANOID");
        }
        long stop = System.currentTimeMillis()-start ;//start of the timer
        System.out.println(stop + "millisecond(s)");

        return bestMove;
    }

    private Move getpBestMove(MiniMaxNode root,float score,int rootPlayerNbr){
        for(MiniMaxNode c: root.getChildren()) {
            System.out.println(c.getpScore());
            if (c.getpScore()==score) return new Move(players[rootPlayerNbr-1],c.getMove().getPiece(), c.getMove().getPosition());
        }
        System.out.println("oops"+root.getChildren().size());
        return null;
    }

    /**
     *
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

    /**
     * maxN mutliplayer minimax algorithm
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
        //check for each child of the current node
        ArrayList<Move>possibleMoves = getPossibleMoves(playerNbr);
        for (Move possibleMove : possibleMoves){
            //TODO compute first the moves that have been cutoff earlier
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


    private float paranoidSearch(MiniMaxNode node, int depth, int playerNbr, float alpha, float beta) {
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
        ArrayList<Move>possibleMoves = getPossibleMoves(playerNbr);
        //TODO order the move so that the worst ones are at the end and are pruned faster
        for (Move possibleMove : possibleMoves){
            boolean firstTurn = players[playerNbr-1].isFirstMove();
            //TODO compute first the moves that have been cutoff earlier
            MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,players[playerNbr-1],board);
            int nextPlayerNbr;
            if(playerNbr>=players.length){
                nextPlayerNbr = 1;
            }else{
                nextPlayerNbr = playerNbr+1;
            }
            if(playerNbr==rootPlayerNbr || nextPlayerNbr==rootPlayerNbr){
                alpha = Math.max(alpha, -paranoidSearch(newNode,depth-1,nextPlayerNbr,-beta,-alpha));
            }else{
                alpha = Math.max(alpha, paranoidSearch(newNode,depth-1,nextPlayerNbr,alpha,beta));
            }
            newNode.removeMove();
            if(firstTurn){
                players[playerNbr-1].setFirstMove(true);
            }
            //System.out.println("alpha = " + alpha + " , beta = " + beta);
            if(alpha>=beta){
                //System.out.println("cutoff");
                node.setKillerMoves(newNode.getMove());
                checkToAddToCutOff(newNode.getMove());
                node.setpScore(beta);
                return beta;
            }
        }
        node.setpScore(alpha);
        return alpha;
    }


    /////////////////////////////////
    //KILLER MOVE STRATEGY

    /**
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

    /**
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

    ///////////////////////////////////////////////
    //HEURISTICS

    /**
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

    /**
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


    /**
     * method used to calculate the area of each player after the current move
     * @param board - board of the game
     * @return the area of each player after the current move
     */
    public float[] getArea(Board board){
        float[] res = new float[players.length];
        for(int i=0;i<players.length;i++) {
            int farestX = Integer.MIN_VALUE;
            int farestY = Integer.MIN_VALUE;

            for (Corner corner : board.getCorner(players[i].getStartingCorner())) {
                if (corner.getPosition().get_x() > farestX) farestX = corner.getPosition().get_x();
                if (corner.getPosition().get_y() > farestY) farestY = corner.getPosition().get_y();
            }
            res[i]= (float) Math.sqrt((Math.pow(farestX - players[i].getStartingCorner().get_x(), 2) + Math.pow(farestY - players[i].getStartingCorner().get_y(), 2)));
        }
        return res;
    }


    /**
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

    /**
     * method used to normalize each heursitics
     * @param score - score of each player at the current node being scored
     * @param heuristics - heuristics that is being normalized
     * @param maximum - maximum of the heursitcs
     * @return the score with the heuristics normlized
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

    public float getPScore(MiniMaxNode node){
        int state = players[rootPlayerNbr-1].getPiecesUsed().size();
        float blockScoreW, cornerScoreW, areaScoreW, cornerBlockedW;// *weight* of different attribute

        if(state<6){
            blockScoreW=0.5f; cornerScoreW=0.3f; areaScoreW = 0.15f; cornerBlockedW = 0.05f;
        }else if(state>=6&&state<13){
            blockScoreW=0.15f; cornerScoreW=0.4f; areaScoreW = 0.05f; cornerBlockedW = 0.4f;
        }else{
            blockScoreW=0.2f; cornerScoreW=0.4f; areaScoreW = 0.2f; cornerBlockedW = 0.4f;
        }
        float nbrOfBlocks = 0;
        for (int i = 0; i < board.boardArray.length; i++) {
            for (int j = 0; j < board.boardArray[i].length; j++) {
                if(board.boardArray[i][j]==players[rootPlayerNbr-1].getPlayerNumber()){
                    nbrOfBlocks++;
                }
            }
        }

        float nbrOfCorners = board.getCorner(players[rootPlayerNbr-1].getStartingCorner()).size();

        int farestX = Integer.MIN_VALUE;
        int farestY = Integer.MIN_VALUE;

        for (Corner corner : board.getCorner(players[rootPlayerNbr-1].getStartingCorner())) {
            if (corner.getPosition().get_x() > farestX) farestX = corner.getPosition().get_x();
            if (corner.getPosition().get_y() > farestY) farestY = corner.getPosition().get_y();
        }
        float area = (float) Math.sqrt((Math.pow(farestX - players[rootPlayerNbr-1].getStartingCorner().get_x(), 2) + Math.pow(farestY - players[rootPlayerNbr-1].getStartingCorner().get_y(), 2)));

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
        float blockedCorner = blockCornerNumber;

        /*//NORMALIZATION
        float[] scores = {nbrOfBlocks,nbrOfCorners,area,blockedCorner};
        float max = Float.MAX_VALUE;
        for (int i = 0; i < scores.length; i++) {
            if(scores[i]>max){
                max = scores[i];
            }
        }
        for (int i = 0; i < scores.length; i++) {
            scores[i] = scores[i]/max;
        }*/

        return (nbrOfBlocks*blockScoreW)+(nbrOfCorners*cornerScoreW)+(area*areaScoreW)+(blockedCorner*cornerBlockedW);

    }


    /**
     * used to get the Score of the node for each player
     * @param node - node currently being scored
     * @return the score of the node move for each player
     */
    //TODO add the heursitcs that if the move piece is small then try to predict future moves with highest score
    public float[] getScore(MiniMaxNode node) {
        int state = node.getPlayer().getPiecesUsed().size();
        float maxBlockScore=10, maxCornerScore=7, maxAreaScore = 4, maxCornerBlocked = 9;// *weight* of different attribute

        if(state<6){
            maxBlockScore=15; maxCornerScore=8; maxAreaScore = 4; maxCornerBlocked = 3;
        }else if(state>=6&&state<13){
            maxBlockScore=7; maxCornerScore=10; maxAreaScore = 3; maxCornerBlocked = 10;
        }else{
            maxBlockScore=5; maxCornerScore=10; maxAreaScore = 5; maxCornerBlocked = 10;
        }


        float[] score = new float[players.length];

        score = normalize(score,getBlocksScore(node.getBoard()),maxBlockScore); //total number of block TODO: count only new pieces placed?

        score = normalize(score,getArea(node.getBoard()),maxAreaScore);

        float[] nbrOfCorner = new float[players.length];
        for(int i=0; i<players.length;i++) nbrOfCorner[i]=board.getCorner(players[i].getStartingCorner()).size();
        score = normalize(score,nbrOfCorner,maxCornerScore);

        float[] nbrOfCornerBlocked = blocksMostCorners(node);
        score = normalize(score,nbrOfCornerBlocked,maxCornerBlocked);

        node.setScore(score);
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