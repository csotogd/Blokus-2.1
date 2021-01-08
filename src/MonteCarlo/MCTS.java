package MonteCarlo;
import DataBase.Piece;
import DataBase.PieceFactory;
import GameBoard.Board;
import Move.Move;
import Player.*;
import Tools.Vector2d;

import java.util.*;

public class MCTS {
    Player[] players;
    Node root;
    Board board;
    int numMoves;

    /**
     * COnstructor
     * @param pls players in the current game, order matters !
     * @param bo board
     */
    public MCTS(Player[] pls, Board bo){
        players= pls;
        board=bo;
        root = new Node(bo, pls);
        numMoves=10;
    }

    public Move simulation(int player, long timeLimit){
        long start = System.currentTimeMillis(); //start of the timer
        root = new Node(board, players);
//        if(players[player].getPiecesList().size()>17) root.randomExpandBias(players[player], numMoves);
//        else root.randomExpand(this.players[player], numMoves);// expand will append a children of every possible move to the root
        expandGreedily(root,player,numMoves);
        if(root.getChildren().size()==0){
            root.expand(players[player]);
            System.out.println("full expand"+ root.getChildren().size());
            if(root.getChildren().size()==0) return null;
        }
        List<Node> toExpand=new ArrayList<Node>();
        toExpand.addAll(root.getChildren());
        while(System.currentTimeMillis()-start<timeLimit){ // while there is still time
            //chose one of the possible move
            Node choosen =null;
            if(toExpand.size()==0) {
                choosen = root.getChildren().get(0); //choose one node to simulate
                for (Node children : root.getChildren()) {
                    if (children.getUCB1() > choosen.getUCB1()) choosen = children;
                }
            }else{
                choosen = toExpand.remove(0);
            }
            while(choosen.getChildren()!=null&&choosen.getChildren().size()!=0){
                Node old = choosen;
                choosen = choosen.getChildren().get(0); //choose one node to simulate
                for (Node children : old.getChildren()) {
                    if (children.getUCB1() > choosen.getUCB1()) choosen = children;
                }
            }
            if(choosen.getVisitedNum()!=0){
                choosen.initChildren();
//                choosen.randomExpand(players[(choosen.getDepth()+1)%players.length],numMoves);//expandOne
                expandGreedily(choosen, player,numMoves/2);
                toExpand.addAll(choosen.getChildren());
//                System.out.println(choosen.getDepth()+"  "+player);
                if(toExpand.size()!=0) choosen = toExpand.remove(0);
            }
            //simulate turn by turn until the end -> back propagate score
            choosen.addVisitiedNum(); // update the count of the visited number
            choosen.addScore(choosen.simulation((choosen.getDepth()+player+1)%players.length,player));//if we get a win update the score as well
        }
        Node res = root.getChildren().get(0);//choose the most visited node move
        for(Node children : root.getChildren()) System.out.println("player"+(player+1)+": "+children.getMove().getPiece().getLabel()+" "+children.getScore()+" "+ children.getVisitedNum());
        for(Node children : root.getChildren()) if(children.getVisitedNum()>res.getVisitedNum()|| // choose the node according to number of time visited, if equals, check if ratio win/loss is higher
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()>res.getScore())||// if equal again, choose biggest piece
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()==res.getScore()&&children.getMove().getPiece().getNumberOfBlocks()>res.getMove().getPiece().getNumberOfBlocks())) res=children;
        numMoves = root.getVisitedNum()/7; // next time we do simulations, we will explore a number of moves such that we can visit 7 times each
//        System.out.println("visit:"+res.getVisitedNum()+"\nscore:"+res.getScore()+"\nmove:"+res.getMove().getPiece()+"@"+res.getMove().getPosition());
        for(Player p: players) if(p.getPlayerNumber()==res.getMove().getPlayer().getPlayerNumber()) return new Move(p,res.getMove().getPiece(), res.getMove().getPosition());
        return res.getMove();
    }

    public boolean expandGreedily(Node n, int player,int numMoves){
        Player p = n.getPlayers()[(n.getDepth()+1+player)%players.length];
        List<Move> moves = p.possibleMoveSetUpdate(n.getState(), numMoves);
//        System.out.println(moves.size());
        if(moves.size()>numMoves){
            double[] score= new double[moves.size()];
            int counter=0;
            for(Move m: moves) score[counter++]=((double)m.getPiece().getNumberOfBlocks());
            //score with closest to middle under 4-5 turns
            if(p.getPiecesList().size()>17){
                double maxDist = Math.sqrt(Math.pow(board.getDIMENSION()/2,2)*2);
                for (int i = 0; i < moves.size(); i++) {
                    score[i]+= maxDist-Math.sqrt(Math.pow(board.getDIMENSION()/2-(moves.get(i).getPosition().get_x()+(moves.get(i).getPiece().getShape()[0].length)/2.0),2)+
                            Math.pow(board.getDIMENSION()/2-(moves.get(i).getPosition().get_y()+(moves.get(i).getPiece().getShape().length)/2.0),2));
                }
            }
            //score with numMoves increase
            if(!p.isFirstMove()&&p.getUnplayablePiece().size()>0){
                Board bclone = n.getState().clone();
                for (int i = 0; i < moves.size(); i++) {
                    write(moves.get(i),bclone);
                    //score with numMoves increase
                    for(Piece piece: p.getUnplayablePiece()){
                        if(bclone.fitOnBoard(piece,p)) score[i]+=1;
                    }
                    unwrite(moves.get(i),bclone);
                }
            }
            if(p.getPiecesList().size()<18){
                //block corners
                Board bclone = n.getState().clone();
                for (int i = 0; i < moves.size(); i++) {
                    //block corners
                    HashSet<Vector2d> opponentsCorners = getOpponentsCorners(p.getStartingCorner(),bclone,players);
                    for (int j = 0; j < moves.get(i).getPiece().getShape().length; j++) {
                        for (int k = 0; k < moves.get(i).getPiece().getShape()[0].length; k++) {
                            if(moves.get(i).getPiece().getShape()[j][k]!=0 && opponentsCorners.contains(moves.get(i).getPosition().add(new Vector2d(j,k))))score[i]+=1;
                        }
                    }

                }
            }
            moves= getBest(moves,score,numMoves);
        }
        List<Node> children = n.getChildren();
        if(children==null) children = new ArrayList<Node>();
        for(Move m:moves) children.add(new Node(n, m));
        return true;
    }

    public List<Move> getBest(List<Move> moves,double[] score, int numMoves){
        final int size = moves.size();

        final int[] sortedIndex = new int[size];
        for (int i = 0; i < size; i++)
            sortedIndex[i] = i;

        boolean sorted;
        do {
            sorted = true;
            int bubble = sortedIndex[0];
            for (int i = 0; i < size - 1; i++) {
                if (score[bubble] > score[sortedIndex[i + 1]]) {
                    sortedIndex[i] = sortedIndex[i + 1];
                    sortedIndex[i + 1] = bubble;
                    sorted = false;
                } else {
                    bubble = sortedIndex[i + 1];
                }
            }
        } while (!sorted);
        ArrayList<Move> best = new ArrayList<>();
        for (int i = 0; i < numMoves; i++) {
            best.add(moves.get(sortedIndex[sortedIndex.length-1-i]));
        }
        return best;
    }

    public void write(Move m, Board b){
        for (int i = 0; i < m.getPiece().getShape().length; i++) {
            for (int j = 0; j < m.getPiece().getShape()[0].length; j++) {
                if(m.getPiece().getShape()[i][j]!=0) b.boardArray[m.getPosition().get_y()+i][m.getPosition().get_x()+j]=m.getPlayer().getPlayerNumber();
            }
        }
    }
    public void unwrite(Move m, Board b){
        for (int i = 0; i < m.getPiece().getShape().length; i++) {
            for (int j = 0; j < m.getPiece().getShape()[0].length; j++) {
                if(m.getPiece().getShape()[i][j]!=0) b.boardArray[m.getPosition().get_y()+i][m.getPosition().get_x()+j]=0;
            }
        }
    }


    public HashSet<Vector2d> getOpponentsCorners(Vector2d startingPosition, Board board, Player[] players){
        HashSet<Vector2d> corners = new HashSet<>();
        for(Player p: players){
            if(!p.getStartingCorner().equals(startingPosition)){
                if(!p.isFirstMove()){
                    boolean[][] checked = new boolean[board.getDIMENSION()][board.getDIMENSION()];
                    findCorners(board, checked,p.getStartingCorner(),corners,p.getPlayerNumber());
                }
            }
        }

//        System.out.println("cornersize"+corners.size());
        return corners;
    }



//Dont spend too much time trying to understand it, just know what it is used for
    /**
     * helper method to find corners and add them to the arraylist
     * @param checked position on the board that is already checked
     * @param position current coordinates
     * @param corners arraylist that contains the corners
     * @param player id of the player
     */
//TODO : not use inboard function as it takes twice as many evaluations as necessary
    private void findCorners(Board board,boolean[][] checked, Vector2d position, HashSet<Vector2d> corners, int player) {

        if(board.inBoard(position) && !checked[position.get_y()][position.get_x()]){
            checked[position.get_y()][position.get_x()]=true;
            if(board.boardArray[position.get_y()][position.get_x()]==player){
                boolean top=true, down=true, left=true, right=true; //NOT occupied by own blocks ie"free"
                //if top block is inside the board limit we can check whether it is the player's block
                if(board.inBoard(position.add(new Vector2d(0,-1))) && board.boardArray[position.get_y()-1][position.get_x()]==player){
                    top=false;//top is occupied by own block, continue the recursion at that block
                    findCorners(board, checked, position.add(new Vector2d(0,-1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(0,1))) && board.boardArray[position.get_y()+1][position.get_x()]==player){
                    down=false;
                    findCorners(board, checked, position.add(new Vector2d(0,1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(-1,0))) && board.boardArray[position.get_y()][position.get_x()-1]==player){
                    left=false;
                    findCorners(board, checked, position.add(new Vector2d(-1,0)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(1,0))) && board.boardArray[position.get_y()][position.get_x()+1]==player){
                    right=false;
                    findCorners(board, checked, position.add(new Vector2d(1,0)), corners, player);
                }


                if(top && left
                        && board.inBoard(new Vector2d(position.get_x()-1, position.get_y()-1))
                        && board.boardArray[position.get_y()-1][position.get_x()-1]==0){
                    corners.add(position.add(new Vector2d(-1,-1)));
                }
                if(top && right
                        && board.inBoard(new Vector2d(position.get_x()+1, position.get_y()-1))
                        && board.boardArray[position.get_y()-1][position.get_x()+1]==0){
                        corners.add(position.add(new Vector2d(1,-1)));

                }
                if(down && left
                        && board.inBoard(new Vector2d(position.get_x()-1, position.get_y()+1))
                        && board.boardArray[position.get_y()+1][position.get_x()-1]==0){
                        corners.add(position.add(new Vector2d(-1,1)));

                }
                if(down && right
                        && board.inBoard(new Vector2d(position.get_x()+1, position.get_y()+1))
                        && board.boardArray[position.get_y()+1][position.get_x()+1]==0){
                        corners.add(position.add(new Vector2d(1,1)));

                }

                //now check for corners
                if(board.isOccupiedBy(position.add(new Vector2d(1,1)),player)) findCorners(board, checked,position.add(new Vector2d(1,1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(-1,1)),player)) findCorners(board,checked,position.add(new Vector2d(-1,1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(-1,-1)),player)) findCorners(board,checked,position.add(new Vector2d(-1,-1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(1,-1)),player)) findCorners(board,checked,position.add(new Vector2d(1,-1)),corners,player);

            }

        }
    }

    public static void main(String[] args){
        int time = 1500;
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
        MCTS mc = new MCTS(new Player[]{p1,p2,p3,p4},b);

        int i= 0;
        boolean[] passed = new boolean[4];
        int passcount =0;
        while(passcount<4){
            //mc = new MonteCarlo(mc.players,b);
            if(!passed[0]) {
                Move move1 = mc.simulation(0, time);
                if (move1!=null&&move1.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());
                else {
                    passed[0]=true;
                    passcount++;
                }
                if (move1==null) System.out.println(p1.possibleMove(b));
            }

            //mc = new MonteCarlo(mc.players,b);
            if(!passed[1]) {
                Move move2 = mc.simulation(1,time);
                if(move2!=null&&move2.makeMove(b)) p2.removePiece(move2.getPiece().getLabel());
                else {
                    passed[1]=true;
                    passcount++;
                }
                if (move2==null) System.out.println(p2.possibleMove(b));
            }

            //mc = new MonteCarlo(mc.players,b);
            if(!passed[2]) {
                Move move3 = mc.simulation(2,time);
                if(move3!=null&&move3.makeMove(b)) p3.removePiece(move3.getPiece().getLabel());
                else {
                    passed[2]=true;
                    passcount++;
                }
                if (move3==null) System.out.println(p3.possibleMove(b));
            }

            //mc = new MonteCarlo(mc.players,b);
            if(!passed[3]) {
                Move move4 = mc.simulation(3,time);
                if(move4!=null&&move4.makeMove(b)) p4.removePiece(move4.getPiece().getLabel());
                else {
                    passed[3]=true;
                    passcount++;
                }
                if (move4==null) System.out.println(p4.possibleMove(b));
            }

            b.print();
            i++;
        }

    }




}
