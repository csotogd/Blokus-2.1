package MonteCarlo;

import DataBase.PieceFactory;
import GameBoard.Board;
import Move.Move;
import Player.*;
import Tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;

public class MonteCarlo {

    Player[] players;
    Node root;
    Board board;
    int numMoves;

    /**
     * COnstructor
     * @param pls players in the current game, order matters !
     * @param bo board
     */
    public MonteCarlo(Player[] pls, Board bo){
        players= pls;
        board=bo;
        root = new Node(bo, pls);
        numMoves=10;
    }

    public Move simulation(int player, long timeLimit){
        long start = System.currentTimeMillis(); //start of the timer
        root = new Node(board, players);
        if(players[player].getPiecesList().size()>17) root.randomExpandBias(players[player].clone(), numMoves);
        else root.randomExpand(this.players[player].clone(), numMoves);// expand will append a children of every possible move to the root

        while(System.currentTimeMillis()-start<timeLimit){ // while there is still time
            //chose one of the possible move
            Node choosen = root.getChildren().get(0); //choose one node to simulate
            for(Node children : root.getChildren()) {
                if(children.getUCB1()>choosen.getUCB1()) choosen=children;
            }
            //simulate turn by turn until the end -> back propagate score
            choosen.addVisitiedNum(); // update the count of the visited number
            choosen.addScore(choosen.simulation((player+1)%players.length,player));//if we get a win update the score as well
        }
        Node res = root.getChildren().get(0);//choose the most visited node move
        for(Node children : root.getChildren()) System.out.println("player"+(player+1)+": "+children.getMove().getPiece().getLabel()+" "+children.getScore()+" "+ children.getVisitedNum());
        for(Node children : root.getChildren()) if(children.getVisitedNum()>res.getVisitedNum()|| // choose the node according to number of time visited, if equals, check if ratio win/loss is higher
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()>res.getScore())||// if equal again, choose biggest piece
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()==res.getScore()&&children.getMove().getPiece().getNumberOfBlocks()>res.getMove().getPiece().getNumberOfBlocks())) res=children;
        numMoves = root.getVisitedNum()/7; // next time we do simulations, we will explore a number of moves such that we can visit 7 times each
        for(Player p: players) if(p.getPlayerNumber()==res.getMove().getPlayer().getPlayerNumber()) return new Move(p,res.getMove().getPiece(), res.getMove().getPosition());
        return res.getMove();
    }

    public Move simulation(int player, long timeLimit, ArrayList<Move> score_move){
        long start = System.currentTimeMillis(); //start of the timer
        root = new Node(board, players);
        root.expandGA(score_move,numMoves); //expansion of node according to GA scores

        while(System.currentTimeMillis()-start<timeLimit){ // while there is still time
            //chose one of the possible move
            Node choosen = root.getChildren().get(0); //choose one node to simulate
            for(Node children : root.getChildren()) {
                if(children.getUCB1()>choosen.getUCB1()) choosen=children;
            }
            //simulate turn by turn until the end -> back propagate score
            choosen.addVisitiedNum(); // update the count of the visited number
            choosen.addScore(choosen.simulation((player+1)%players.length,player));//if we get a win update the score as well
        }
        Node res = root.getChildren().get(0);//choose the most visited node move

        for(Node children : root.getChildren()) if(children.getVisitedNum()>res.getVisitedNum()||
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()>res.getScore())||
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()==res.getScore()&&children.getMove().getPiece().getNumberOfBlocks()>res.getMove().getPiece().getNumberOfBlocks())) res=children;
        numMoves = root.getVisitedNum()/7;
        for(Player p: players) if(p.getPlayerNumber()==res.getMove().getPlayer().getPlayerNumber()) return new Move(p,res.getMove().getPiece(), res.getMove().getPosition());
        return res.getMove();
    }

    public static void main(String[] args){
        Player p1 = new MiniMaxPlayer(1);
        Player p2 = new MCPlayer(2, "notJo");
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
        MonteCarlo mc = new MonteCarlo(new Player[]{p1,p2,p3,p4},b);

        int i= 0;
        while(i<15){
            //mc = new MonteCarlo(mc.players,b);
            Move move1 = mc.simulation(0,5000);
            if(move1.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());

            //mc = new MonteCarlo(mc.players,b);
            Move move2 = mc.simulation(1,5000);
            if(move2.makeMove(b)) p2.removePiece(move2.getPiece().getLabel());

            //mc = new MonteCarlo(mc.players,b);
            Move move3 = mc.simulation(2,5000);
            if(move3.makeMove(b)) p3.removePiece(move3.getPiece().getLabel());

            //mc = new MonteCarlo(mc.players,b);
            Move move4 = mc.simulation(3,5000);
            if(move4.makeMove(b)) p4.removePiece(move4.getPiece().getLabel());

            b.print();
            i++;
        }

    }



}
