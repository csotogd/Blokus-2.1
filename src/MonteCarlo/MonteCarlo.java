package MonteCarlo;

import DataBase.PieceFactory;
import GameBoard.Board;
import Move.Move;
import Player.*;
import Tools.Vector2d;

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
        if(players[player].getPiecesList().size()>17) root.randomExpandBias(players[player].clone());
        else root.randomExpand(this.players[player].clone(), numMoves);// expand will append a children of every possible move to the root
        //TODO : not expand every possible move ?
//        System.out.println("p"+player+" "+root.getChildren().size()); // for debug purpose print the number of possible move

        //System.out.println((System.currentTimeMillis()-start)+"ms"); // how long did we take to expand/visit every node?
        while(System.currentTimeMillis()-start<timeLimit){ // while there is still time
            //chose one of the possible move
            Node choosen = root.getChildren().get(0); //choose one node to simulate
            for(Node children : root.getChildren()) {
                if(children.getUCB1()>choosen.getUCB1()) choosen=children;
            }
            //System.out.println("ucbscore: "+choosen.getUCB1()+" / " +choosen.getMove().getPiece().getLabel());
            //simulate turn by turn until the end -> back propagate score
            choosen.addVisitiedNum(); // update the count of the visited number
            if(choosen.simulation((player+1)%players.length,player)>0) choosen.addScore();//if we get a win update the score as well
        }
        Node res = root.getChildren().get(0);//choose the most visited node move
        for(Node children : root.getChildren()) System.out.println("player"+(player+1)+": "+children.getMove().getPiece().getLabel()+" "+children.getScore()+" "+ children.getVisitedNum());
        for(Node children : root.getChildren()) if(children.getVisitedNum()>res.getVisitedNum()||
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()>res.getScore())||
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()==res.getScore()&&children.getMove().getPiece().getNumberOfBlocks()>res.getMove().getPiece().getNumberOfBlocks())) res=children;
        for(Player p: players) if(p.getPlayerNumber()==res.getMove().getPlayer().getPlayerNumber()) return new Move(p,res.getMove().getPiece(), res.getMove().getPosition());
        numMoves = root.getVisitedNum()/7;
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
