package MiniMax;

import DataBase.PieceFactory;
import GameBoard.Board;
import Player.Player;
import Move.Move;
import Tools.Vector2d;
import Player.*;

public class MiniMax {
    Player[] players;
    Board board;
    private final int NBR_OF_TURNS = 1;
    private int maxDepth;
    int rootPlayerNbr;

    public MiniMax(Player[] players, Board board){
        this.players = players;
        this.board = board;
        maxDepth = players.length*NBR_OF_TURNS;
    }

    public Move getMove(int playerNbr){
        this.rootPlayerNbr = playerNbr;
        MiniMaxNode root = new MiniMaxNode(board,players);
        //create first nodes of that player
        return alphaBeta_Pruning(root,root.getDepth(),playerNbr,Float.MIN_VALUE,Float.MAX_VALUE).getMove();
    }

    private MiniMaxNode alphaBeta_Pruning(MiniMaxNode node, int depth, int playerNbr, float alpha, float beta) {
        //System.out.println(playerNbr);
        if(depth==maxDepth){
            if(playerNbr==rootPlayerNbr) return node;
            else node.setNegative(); return node;
        }else{
            for (Move possibleMove : players[playerNbr-1].possibleMoveSet(board)){
                node = new MiniMaxNode(node,possibleMove,depth+1);
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
                    node.setScore(beta);
                    return node;
                }
            }
            node.setScore(alpha);
            return node;
        }
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
            if(move1.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());
            b.print();

            Move move2 = m.getMove(p2.getPlayerNumber());
            if(move2.makeMove(b)) p2.removePiece(move1.getPiece().getLabel());
            b.print();

            Move move3 = m.getMove(p3.getPlayerNumber());
            if(move3.makeMove(b)) p3.removePiece(move1.getPiece().getLabel());
            b.print();

            Move move4 = m.getMove(p4.getPlayerNumber());
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
