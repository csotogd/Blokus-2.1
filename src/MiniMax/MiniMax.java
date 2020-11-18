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
    private final int MAX_DEPTH = 2;
    int currentDepth;
    int rootPlayerNbr;

    public MiniMax(Player[] players, Board board){
        this.players = players;
        this.board = board;
    }

    public void simulate(int playerNbr){
        this.rootPlayerNbr = playerNbr;
        currentDepth=0;
        MiniMaxNode root = new MiniMaxNode(board,players);
        //create first nodes of that player
        alphaBeta(root,root.getDepth(),playerNbr,0,0);
    }

    /*
    //MAX
    public void createNodes(MiniMaxNode rootNode,int playerNbr){
        int turnCounter = 0;
        while(turnCounter<MAX_DEPTH){
            for (Move move:players[playerNbr].possibleMoveSet(board)){
                MiniMaxNode newNode = new MiniMaxNode(rootNode,move);
                rootNode.getChildren().add(newNode);
                newNode.expand(players[playerNbr+1]);
            }
            //System.out.println("next turn");
            turnCounter++;
        }
    }

     */

    private float alphaBeta(MiniMaxNode node,int depth, int playerNbr,float alpha, float beta) {
        if(node.getDepth()==MAX_DEPTH){
            if(playerNbr==rootPlayerNbr) return node.getScore();
            else return -node.getScore();
        }else{
            for (Move possibleMove : players[playerNbr-1].possibleMoveSet(board)){
                MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,depth-1);
                int nextPlayerNbr = 0;
                if(playerNbr>=players.length){
                    currentDepth++;
                    nextPlayerNbr = 1;
                }else{
                    nextPlayerNbr++;
                }
                if(playerNbr==rootPlayerNbr || nextPlayerNbr==rootPlayerNbr){
                    alpha = Math.max(alpha,alphaBeta(newNode,depth-1,playerNbr,-alpha,-beta));
                }else {
                    alpha = Math.max(alpha,alphaBeta(newNode,depth-1,playerNbr,alpha,beta));
                }
                if(alpha>=beta){
                    return beta;
                }
            }
            return alpha;
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
        Board b = new Board(new Player[]{p1, p2});
        MiniMax m = new MiniMax(new Player[]{p1,p2},b);
        m.simulate(p1.getPlayerNumber());
    }


}
