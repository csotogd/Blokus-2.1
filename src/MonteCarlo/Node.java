package MonteCarlo;

import DataBase.Piece;
import GameBoard.Board;
import Move.Move;
import Player.Player;

import java.util.*;

public class Node {
    private Node parent;
    private Board state;
    private Move move;
    private List<Node> children;
    private int visitedNum;
    private int score;
    private static double c=3;
    private Player[] players;
    private double ucbScore;

    /**
     * CONSTRUCTOR for the root (parent point to itself)
     * @param state current state of the game
     */
    public Node(Board state, Player[] ps){
        this.state=state;
        parent = this;
        move = null;
        children = new ArrayList<>();
        visitedNum =0;
        score = 0;
        players = new Player[ps.length];
        int count =0;
        for(Player p : ps) players[count++]=p.clone();

    }

    public Player[] getPlayers() {
        return players;
    }


    /**
     * Regular constructor
     * @param parent parent node
     * @param move move to make to arrive from parent to this node
     */
    public Node(Node parent, Move move){
        this.parent=parent;
        this.move = move;

        //children = new ArrayList<>();
        visitedNum = 0;
        score = 0;
        ucbScore = (double)move.getPiece().getNumberOfBlocks();
    }

    /**
     * create the children of the current state
     * @param player
     */
    public boolean expand(Player player){
        for(Move m : player.possibleMoveSet(state)) children.add(new Node(this,m));
        if(children.size()>0) return true;
        return false;
    }

    /**
     * Expand with random moves a state
     * @param player the player for which we want to expand the moves
     * @return true if it was successful, false otherwise
     */
    public boolean randomExpand(Player player, int numMoves){

        for(int i=0;i<15;i++){
            Node n = new Node(this,player.randomPossibleMoveClone(state, player.getPiecesList()));
            if(!children.contains(n)) children.add(n);
        }
        if(children.size()>0) return true;
        return false;
    }

    /**
     * Expand with random moves bias towards bigger piece
     * @param player the player for which we want to expand the moves
     * @return true if it was successful, false otherwise
     */
    public boolean randomExpandBias(Player player){
        List biggestPieces;
        if(player.getPiecesList().size()<10){
            biggestPieces = player.getPiecesList();
        }else{
            biggestPieces = getBiggestPieces(player);
        }
        for(int i=0;i<10;i++){
            Move m = player.randomPossibleMoveClone(state, biggestPieces);
            if(m!=null){
                children.add(new Node(this,m));
            }
        }
        if(children.size()>0) return true;
        return false;
    }



    public List<Piece> getBiggestPieces(Player player){
        int maxNbrOfBlocks = Integer.MIN_VALUE;
        for (Piece piece:player.getPiecesList()) {
            if(piece.getNumberOfBlocks()>maxNbrOfBlocks){
                maxNbrOfBlocks = piece.getNumberOfBlocks();
            }
        }
        List<Piece> biggestPieces = new ArrayList<>();
        int diff = 0;
        while(biggestPieces.size()<10&&diff<=5){
            for (Piece piece:player.getPiecesList()) {
                if(piece.getNumberOfBlocks()==maxNbrOfBlocks-diff){
                    biggestPieces.add(piece);
                }
            }
            if(biggestPieces.size()<10){
                diff++;
            }
        }
        return biggestPieces;
    }

    /**
     * Simulate a play until it can't find a move for any player
     * @return the final score for the playerOfInterest (1 for a win)
     */
    public int simulation(int playerturn, int playerOfInterest){
        initializeNode();// make the move, copy players
        int countPass=0; //number of time a player has passed during a simulation
        Board board = state.clone(); //clone the board
     //   Player[] temp = new Player[players.length];//clone the players
     //   for(Player p : players) temp[p.getPlayerNumber()-1]=p.clone();
        //TODO max nbr of iteration implementation needed
        while(countPass<players.length){ //while not everyone has passed in a turn
            if(playerturn==0) countPass=0; // beginning of the turn, nobody has passed yet
            Move move = players[playerturn].randomPossibleMove(board); //random move
            if(move!=null) {
                move.writePieceIntoBoard(board);
                players[playerturn].getPiecesList().remove(move.getPiece());
            }else{
                countPass++;
            }
            playerturn = (playerturn+1)%players.length;
        }
        //board.print();
        int[] playerScores=new int[players.length];
        for(Player p: players) for(Piece piece: p.getPiecesList()) playerScores[p.getPlayerNumber()-1]+=piece.getNumberOfBlocks();

       // System.out.println(move.getPiece().getLabel()+" "+playerScores[0]+" "+playerScores[1]+" "+playerScores[2]+" "+playerScores[3]);
        for(int score:playerScores) if(playerScores[playerOfInterest]>score) return 0;//loss
        return 1;//win
    }

    public void initializeNode(){
        state = parent.getState().clone();
        players = new Player[parent.getPlayers().length];
        int count =0;
        for(Player p : parent.getPlayers()) { //copy the players but take care to remove the piece played
            players[count++]=p.clone();
            if(players[count-1].getPlayerNumber()==move.getPlayer().getPlayerNumber()) {
                players[count - 1].removePiece(move.getPiece().getLabel());
                players[count - 1].setNotFirstMove();
            }
        }
        move.writePieceIntoBoard(state);
    }

    public double getUCB1(){
        if(visitedNum>0) return computeUCB();
        return ucbScore;
    }

    public double computeUCB(){
        ucbScore= (double)score/(double) visitedNum + Node.c*Math.sqrt(Math.log(2*parent.getVisitedNum())/(double) visitedNum);
        return ucbScore;
    }

    public List<Node> getChildren(){
        return this.children;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Board getState() {
        return state;
    }

    public void setState(Board state) {
        this.state = state;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public int getVisitedNum() {
        return visitedNum;
    }

    public void addVisitiedNum() {
        this.visitedNum++;
        if(parent!=this) {
            parent.addVisitiedNum();
            computeUCB();
        }
    }

    public int getScore() {
        return score;
    }

    public void addScore() {
        this.score ++;
        if(parent!=this) parent.addScore();
    }
}
