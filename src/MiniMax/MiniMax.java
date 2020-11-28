package MiniMax;

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
    float u = 21;
    int killerMovesLength = 20;

    public MiniMax(Player[] players, Board board){
        this.players = players;
        this.boardOrigin = board;
        maxDepth = players.length*NBR_OF_TURNS;
    }

    public Move getMove(int playerNbr){
        //long start = System.currentTimeMillis(); //start of the timer
        this.rootPlayerNbr = playerNbr;
        this.cutOffMoveOccurence = new ArrayList<Move>(killerMovesLength);
        this.board = boardOrigin.clone();
        MiniMaxNode root = new MiniMaxNode(board,players[playerNbr-1].clone(),maxDepth);
        float[] score = maxN(root,maxDepth,playerNbr,Float.MIN_VALUE);
        for (Move m:cutOffMoveOccurence) {
            System.out.println(m.getOccurence());
        }
        Move bestMove = getBestMove(root,score,playerNbr);
        return bestMove;
    }

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


    private float[] maxN(MiniMaxNode node, int depth, int playerNbr, float alpha){
        //if we have reached a terminal node (leaf) we set the score as the heuristics and then backtrack
        if(depth<=0) {
            updateOcc();
            node.setScore(getScore(node));
            return node.getScore();
        }
        //list of the best score for each player
        float[] best =new float[players.length];
        for (int i = 0; i < best.length; i++) best[i] = Float.MIN_VALUE;
        //check for each child of the current node
        for (Move possibleMove : players[playerNbr-1].possibleMoveSetBoosted(board)){
            //TODO compute first the moves that have been cutoff earlier
            boolean firstTurn = players[playerNbr-1].isFirstMove();
            //create new child
            MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,depth-1,players[playerNbr-1],board);
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
                checkToAddToCutOff(newNode.getMove());
                node.setScore(result);
                return result;
            }
        }
        node.setScore(best);
        return best;

    }

    /*
        private float paranoidSearch(MiniMaxNode node, int depth, int playerNbr, float alpha, float beta) {
            //System.out.println(playerNbr);
            if(depth<=0){
                if(playerNbr==rootPlayerNbr) {
                    return node.getScore();
                }else{
                    System.out.println("STOP RECURSION COND., not possible, smth wrong");
                    //never here because we never reach a terminal node that is not the root player
                    return -node.getScore();
                }
            }else{
                //TODO order the move so that the worst ones are at the end and are pruned faster
                for (Move possibleMove : players[playerNbr-1].possibleMoveSetBoosted(board)){
                    //TODO compute first the moves that have been cutoff earlier
                    MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,depth-1,players[playerNbr-1],board);
                    int nextPlayerNbr = 0;
                    if(playerNbr>=players.length){
                        nextPlayerNbr = 1;
                    }else{
                        nextPlayerNbr = playerNbr+1;
                    }
                    if(playerNbr==rootPlayerNbr || nextPlayerNbr==rootPlayerNbr){
                        alpha = Math.max(alpha, -alphaBeta_Pruning(newNode,depth-1,nextPlayerNbr,-beta,-alpha));
                    }else {
                        alpha = Math.max(alpha, alphaBeta_Pruning(newNode,depth-1,nextPlayerNbr,alpha,beta));
                    }
                    if(alpha>=beta){
                        //checkToAddToCutOff(node.getMove());
                        node.setScore(beta);
                        return beta;
                    }
                }
                //updateOcc();
                node.setScore(alpha);
                return alpha;
            }
        }


     */
    public void updateOcc(){
        for(Move m:cutOffMoveOccurence){
            m.setOccurence(m.getOccurence()-1);
        }
    }

    public void checkToAddToCutOff(Move move){
        if(cutOffMoveOccurence.isEmpty()||cutOffMoveOccurence.size()<killerMovesLength){
            cutOffMoveOccurence.add(move);
        }else{
            if(cutOffMoveOccurence.contains(move)){
                increaseOcc(move);
            }else{
                deleteSmallestOccMove();
                cutOffMoveOccurence.add(move);
            }
        }
    }

    public void increaseOcc(Move move){
        for(Move m: cutOffMoveOccurence){
            if(m.equals(move)){
                m.setOccurence(m.getOccurence()+1);
            }
        }
    }

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


    public float[] getScore(MiniMaxNode node) {
        float maxBlockScore=10, maxCornerScore=7, maxAreaScore = 4; // *weight* of different attribute
        float[] score = new float[players.length];

        score = normalize(score,getBlocksScore(node.getBoard()),maxBlockScore); //total number of block TODO: count only new pieces placed?
        score = normalize(score,getArea(node.getBoard()),maxAreaScore);

        float[] nbrOfCorner = new float[players.length];
        for(int i=0; i<players.length;i++) nbrOfCorner[i]=board.getCorner(players[i].getStartingCorner()).size();
        score = normalize(score,nbrOfCorner,maxCornerScore);

        node.setScore(score);
        return score;
    }

    public float[] getBlocksScore(Board board){
        float[] res = new float[players.length];
        for (int i = 0; i < players.length; i++) {
            res[i] = 0;
        }
        for(int[] line:board.boardArray) for(int i:line) if(i!=0) res[i-1]++;
        return res;
    }

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