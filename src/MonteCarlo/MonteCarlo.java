package MonteCarlo;

import DataBase.PieceFactory;
import GameBoard.Board;
import Move.Move;
import Player.*;
import Tools.Vector2d;

public class MonteCarlo {

    Player[] players;
    Node root;

    /**
     * COnstructor
     * @param pls players in the current game, order matters !
     * @param bo board
     */
    public MonteCarlo(Player[] pls, Board bo){
        players= pls;
        root = new Node(bo, pls);
    }

    public Move simulation(int player, long timeLimit){
        long start = System.currentTimeMillis();
        root.expand(this.players[player]);

        for(Node children : root.getChildren()){
            int score = children.simulation((player+1)%players.length, player);
            children.addVisitiedNum();
            if(score >0) children.addScore();
        }
        while(System.currentTimeMillis()-start<timeLimit){
            //chose one of the possible move
            Node choosen = root.getChildren().get(0);
            for(Node children : root.getChildren()) {
                if(children.getUCB1()>choosen.getUCB1()) choosen=children;
            }
            //simulate turn by turn until the end -> back propagate score
            choosen.addVisitiedNum();
            int score = choosen.simulation((player+1)%players.length,player);
            if(score>0) choosen.addScore();

        }
        Node res = root.getChildren().get(0);
        for(Node children : root.getChildren()) System.out.println(children.getMove().getPiece().getLabel()+" "+children.getScore()+" "+ children.getVisitiedNum());
        for(Node children : root.getChildren()) if(children.getVisitiedNum()>res.getVisitiedNum()) res=children;
        return res.getMove();
    }

    public static void main(String[] args){
        Player p1 = new HumanPlayer(1, "jo");
        Player p2 = new HumanPlayer(2, "notJo");
        Player p3 = new HumanPlayer(3, "jo2");
        Player p4 = new HumanPlayer(4, "notJo2");
        p1.setStartingCorner(new Vector2d(0,0));
        p2.setStartingCorner(new Vector2d(19,0));
        p3.setStartingCorner(new Vector2d(19,19));
        p4.setStartingCorner(new Vector2d(0,19));
        p1.setPiecesList(PieceFactory.get().getAllPieces());
        p2.setPiecesList(PieceFactory.get().getAllPieces());
        p3.setPiecesList(PieceFactory.get().getAllPieces());
        p4.setPiecesList(PieceFactory.get().getAllPieces());
        Board b = new Board(new Player[]{p1,p2,p3,p4});
        MonteCarlo mc ;

        int i= 0;
        while(i<15){
            mc = new MonteCarlo(new Player[]{p1,p2,p3,p4},b);
            Move move1 = mc.simulation(0,3000);
            move1.writePieceIntoBoard(b);
            p1.removePiece(move1.getPiece().getLabel());
            mc = new MonteCarlo(new Player[]{p1,p2,p3,p4},b);
            Move move2 = mc.simulation(1,3000);
            move2.writePieceIntoBoard(b);
            p2.removePiece(move2.getPiece().getLabel());

            mc = new MonteCarlo(new Player[]{p1,p2,p3,p4},b);
            Move move3 = mc.simulation(2,3000);
            move3.writePieceIntoBoard(b);
            p3.removePiece(move3.getPiece().getLabel());
            mc = new MonteCarlo(new Player[]{p1,p2,p3,p4},b);
            Move move4 = mc.simulation(3,3000);
            move4.writePieceIntoBoard(b);
            p4.removePiece(move4.getPiece().getLabel());
            b.print();
            i++;
        }

    }

}
