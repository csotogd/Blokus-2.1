package MiniMax;

import DataBase.PieceFactory;
import GameBoard.Board;
import Player.Player;
import Move.Move;
import Tools.Vector2d;
import Player.*;

import java.util.*;

public class MiniMax {
    Player[] players;
    Board board;
    private final int NBR_OF_TURNS = 1;
    private int maxDepth;
    int rootPlayerNbr;
    HashMap<Integer,Move> cutOffMoveOccurence;
    ArrayList<Float> best;
    float u;

    public MiniMax(Player[] players, Board board){
        this.players = players;
        this.board = board;
        maxDepth = players.length*NBR_OF_TURNS;
    }

    public Move getMove(int playerNbr){
        //long start = System.currentTimeMillis(); //start of the timer
        this.rootPlayerNbr = playerNbr;
        this.cutOffMoveOccurence = new HashMap<>();
        this.best = new ArrayList<>();
        MiniMaxNode root = new MiniMaxNode(board,players[playerNbr-1],maxDepth);
        alphaBeta_Pruning(root,root.getDepth(),playerNbr,Float.MIN_VALUE,Float.MAX_VALUE);
        //return findBestMove(root);
        return findBestMove(root);
    }

    private Move findBestMove(MiniMaxNode node){
        System.out.println("depth = " + node.getDepth() + ", score = " + node.getScore());
        if(node.getDepth()==0){
            System.out.println("depth = 0");
            return node.getMove();
        }else{
            for(MiniMaxNode n:node.getChildren()) {
                System.out.println(n.getScore());
                if(n.getScore()==node.getScore()){
                    findBestMove(n);
                }
            }
        }
        System.out.println("not possible");
        return null;
    }

    public MiniMaxNode minimax(MiniMaxNode currentNode, int depth,int playerNbr, float alpha, float beta) {
        if (depth == 0) {
            return currentNode;
        }
        //Assuming the AI is purple player
        MiniMaxNode bestNode = null;
        if (playerNbr==rootPlayerNbr) {
            float value = -10000000;
            for (Move possibleMove:players[playerNbr-1].possibleMoveSetBoosted(board)) {
                MiniMaxNode newNode = new MiniMaxNode(currentNode,possibleMove,depth-1,players[playerNbr-1],board);
                float nodeValue = minimax(newNode,playerNbr+1, depth - 1, alpha, beta).getScore();
                    if (nodeValue > value) {
                        value = nodeValue;
                        bestNode = newNode;
                    }
                    alpha = Math.max(alpha, value);
                    if (alpha >= beta) {
                        break;
                    }

            }
            return bestNode;

        } else {
            float value = 10000000;
            for (Move possibleMove:players[playerNbr-1].possibleMoveSetBoosted(board)) {
                MiniMaxNode newNode = new MiniMaxNode(currentNode,possibleMove,depth-1,players[playerNbr-1],board);
                float nodeValue = minimax(newNode, depth - 1, rootPlayerNbr,alpha, beta).getScore();
                    if (nodeValue < value) {
                        value = nodeValue;
                        bestNode = newNode;
                    }
                    beta = Math.min(beta, value);
                    if (beta <= alpha) {
                        break;
                    }

            }
            return bestNode;
        }

    }

    private MiniMaxNode miniMax2P(MiniMaxNode node, int depth, int playerNbr,float alpha,float beta){
        if(depth<=0){
            return node;
        }
        if(playerNbr==rootPlayerNbr){
            float bestVal = Float.MIN_VALUE;
            for (Move possibleMove:players[playerNbr-1].possibleMoveSetBoosted(board)){
                MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,depth-1,players[playerNbr-1],board);
                bestVal = Math.max(bestVal,miniMax2P(newNode,depth-1,playerNbr+1,alpha,beta).getScore());
                alpha = Math.max(alpha,bestVal);
                if(beta<=alpha){
                    break;
                }
            }
            node.setScore(bestVal);
            return node;
        }else{
            float bestVal = Float.MAX_VALUE;
            for (Move possibleMove:players[playerNbr-1].possibleMoveSetBoosted(board)){
                MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,depth-1,players[playerNbr-1],board);
                bestVal = Math.min(bestVal,miniMax2P(newNode,depth-1,rootPlayerNbr,alpha,beta).getScore());
                beta = Math.min(beta,bestVal);
                if(beta<=alpha){
                    break;
                }
            }
            node.setScore(bestVal);
            return node;
        }

    }

    private MiniMaxNode maxN(MiniMaxNode node, int depth, int playerNbr, float alpha){
        if(depth<=0) {
            u = node.getScore();
            return node;
        }
        best.clear();
        best.add(Float.MIN_VALUE);

    }


    private float alphaBeta_Pruning(MiniMaxNode node, int depth, int playerNbr, float alpha, float beta) {
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

    public void updateOcc(){
        for(Map.Entry<Integer, Move> entry : cutOffMoveOccurence.entrySet()){
            int oldOcc = entry.getKey();
            Move oldMove = entry.getValue();
            cutOffMoveOccurence.remove(entry);
            cutOffMoveOccurence.put(oldOcc-1,oldMove);
        }
    }

    public void checkToAddToCutOff(Move move){
        if(cutOffMoveOccurence.isEmpty()||cutOffMoveOccurence.size()<5){
            cutOffMoveOccurence.put(1,move);
        }else{
            if(cutOffMoveOccurence.containsValue(move)){
                increaseOcc(move);
            }else{
                replaceSmallestOcc(move);
            }
        }
    }

    public void increaseOcc(Move move){
        int previousOcc = 0;
        for(Map.Entry<Integer, Move> entry : cutOffMoveOccurence.entrySet()){
            if(entry.getValue().equals(move)){
                previousOcc = entry.getKey();
                cutOffMoveOccurence.remove(entry);
            }
        }
        cutOffMoveOccurence.put(++previousOcc,move);
    }

    public void replaceSmallestOcc(Move move){
        int min = Integer.MAX_VALUE;
        for(Integer i:cutOffMoveOccurence.keySet()){
            if(i<min)min = i;
        }
        cutOffMoveOccurence.remove(min);
        cutOffMoveOccurence.put(1,move);
    }

    public static void main(String[] args) {
        Player p1 = new HumanPlayer(1, "jo");
        Player p2 = new HumanPlayer(2, "notJo");
        Player p3 = new HumanPlayer(3, "jo2");
        Player p4 = new HumanPlayer(4, "notJo2");
        p1.setStartingCorner(new Vector2d(0, 0));
        p2.setStartingCorner(new Vector2d(19, 0));
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
            if(move2.makeMove(b)) p2.removePiece(move1.getPiece().getLabel());
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

    /*
    Move move1 = m.simulate(p1.getPlayerNumber());
            if(move1.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());

            Move move2 = m.simulate(p1.getPlayerNumber());
            if(move2.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());

            Move move3 = m.simulate(p1.getPlayerNumber());
            if(move3.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());

            Move move4 = m.simulate(p1.getPlayerNumber());
            if(move4.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());
     */


}
