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

    public MiniMax(Player[] players, Board board){
        this.players = players;
        this.board = board;
    }

    public void simulate(int playerNbr){
        Player[] newPlayers = new Player[players.length-1];
        int counter =0;
        for (Player player:players) {
            if(player.getNumber()-1!=playerNbr){
                newPlayers[counter++]=player;
            }
        }
        MiniMaxNode root = new MiniMaxNode(board,newPlayers);
        //create first nodes of that player
        createNodes(root,playerNbr);
    }

    //MAX
    public void createNodes(MiniMaxNode rootNode,int playerNbr){
        int turnCounter = 0;
        while(turnCounter<MAX_DEPTH){
            for (Move move:players[playerNbr].possibleMoveSet(board)){
                MiniMaxNode newNode = new MiniMaxNode(rootNode,move,turnCounter);
                rootNode.getChildren().add(newNode);
                newNode.expand(players[playerNbr+1]);
            }
            //System.out.println("next turn");
            turnCounter++;
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
        m.simulate(p1.getPlayerNumber()-1);
    }


}
