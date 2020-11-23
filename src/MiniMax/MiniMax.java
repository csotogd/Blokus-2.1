package MiniMax;

import DataBase.PieceFactory;
import GameBoard.Board;
import Player.Player;
import Move.Move;
import Tools.Vector2d;
import Player.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MiniMax {
    Player[] players;
    Board board;
    private final int NBR_OF_TURNS = 1;
    private int maxDepth;
    int rootPlayerNbr;
    HashMap<Integer,Move> cutOffMoveOccurence;

    public MiniMax(Player[] players, Board board){
        this.players = players;
        /*
        for(Player player:players){
            player.reOrderPieceList();
        }
         */
        this.board = board;
        maxDepth = players.length*NBR_OF_TURNS;
    }

    public Move getMove(int playerNbr){
        //long start = System.currentTimeMillis(); //start of the timer
        this.rootPlayerNbr = playerNbr;
        this.cutOffMoveOccurence = new HashMap<>();
        MiniMaxNode root = new MiniMaxNode(board,players[playerNbr-1]);
        //create first nodes of that player
        Move move = alphaBeta_Pruning(root,root.getDepth(),playerNbr,Float.MIN_VALUE,Float.MAX_VALUE).getMove();
        //System.out.println(System.currentTimeMillis()-start);
        return move;
    }

    private MiniMaxNode alphaBeta_Pruning(MiniMaxNode node, int depth, int playerNbr, float alpha, float beta) {
        if(depth==maxDepth){
            if(playerNbr==rootPlayerNbr) return node;
            else node.setNegative(); return node;
        }else{
            //TODO order the move so that the worst ones are at the end and are pruned faster
            for (Move possibleMove : players[playerNbr-1].possibleMoveSetBoosted(board)){
                //TODO compute first the moves that have been cutoff earlier
                node = new MiniMaxNode(node,possibleMove,depth+1,players[playerNbr-1]);
                int nextPlayerNbr = 0;
                if(playerNbr>=players.length){
                    nextPlayerNbr = 1;
                }else{
                    nextPlayerNbr = playerNbr+1;
                }
                if(playerNbr==rootPlayerNbr || nextPlayerNbr==rootPlayerNbr){
                    alpha = Math.max(alpha, alphaBeta_Pruning(node.getParent(),depth+1,nextPlayerNbr,-beta,-alpha).getScore());
                }else {
                    alpha = Math.max(alpha, alphaBeta_Pruning(node.getParent(),depth+1,nextPlayerNbr,alpha,beta).getScore());
                }
                if(alpha>=beta){
                    //cutoff
                    checkToAddToCutOff(node.getMove());
                    node.setScore(beta);
                    return node;
                }
            }
            //updateOcc();
            node.setScore(alpha);
            return node;
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
        Board b = new Board(new Player[]{p1, p2,p3,p4});
        MiniMax m = new MiniMax(new Player[]{p1,p2,p3,p4},b);

        int i= 0;
        while(i<15){
            Move move1 = m.getMove(p1.getPlayerNumber());
            System.out.println(Arrays.toString(m.cutOffMoveOccurence.keySet().toArray()));
            if(move1.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());
            b.print();

            Move move2 = m.getMove(p2.getPlayerNumber());
            System.out.println(Arrays.toString(m.cutOffMoveOccurence.keySet().toArray()));
            if(move2.makeMove(b)) p2.removePiece(move1.getPiece().getLabel());
            b.print();

            Move move3 = m.getMove(p3.getPlayerNumber());
            System.out.println(Arrays.toString(m.cutOffMoveOccurence.keySet().toArray()));
            if(move3.makeMove(b)) p3.removePiece(move1.getPiece().getLabel());
            b.print();

            Move move4 = m.getMove(p4.getPlayerNumber());
            System.out.println(Arrays.toString(m.cutOffMoveOccurence.keySet().toArray()));
            if(move4.makeMove(b)) p4.removePiece(move1.getPiece().getLabel());
            b.print();

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
