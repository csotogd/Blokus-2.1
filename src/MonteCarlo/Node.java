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
    private int visitiedNum;
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
        visitiedNum=0;
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
        state = parent.getState().clone();
        players = new Player[parent.getPlayers().length];
        int count =0;
        for(Player p : parent.getPlayers()) { //copy the players but take care to remove the piece played
            players[count++]=p.clone();
            if(players[count-1].getPlayerNumber()==move.getPlayer().getPlayerNumber()) {
                players[count - 1].removePiece(move.getPiece().getLabel());
                players[count - 1].setNotFirstMove(); //THIS LINE SHOULDNT BE NECESSARY YET IT IS
            }
        }
        move.writePieceIntoBoard(state);
        children = new ArrayList<>();
        visitiedNum = 0;
        score = 0;
        ucbScore = (double)move.getPiece().getNumberOfBlocks()/10.0;
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

    /** TODO: complete this method
     * MARTIN here we do the random moves until end game and return the score
     *
     * @return
     */
    public int simulation(int playerturn, int playerOfInterest){
        int countPass=0; //number of time a player has passed during a simulation
        Board board = state.clone(); //clone the board
        Player[] temp = new Player[players.length];//clone the players
        for(Player p : players) temp[p.getPlayerNumber()-1]=p.clone();
        while(countPass<players.length){ //while not everyone has passed in a turn
            if(playerturn==0) countPass=0; // beginning of the turn, nobody has passed yet
            Move move = temp[playerturn].randomPossibleMove(board); //random move
            if(move!=null) {
                move.writePieceIntoBoard(board);
                temp[playerturn].removePiece(move.getPiece().getLabel());
                temp[playerturn].setNotFirstMove(); //THIS LINE SHOULD NOT BE NECESSARY YET IT IS
            }else{
                countPass++;
            }
            playerturn = (playerturn+1)%players.length;
        }
        //board.print();
        int[] playerScores=new int[players.length];
        for(Player p: temp) for(Piece piece: p.getPiecesList()) playerScores[p.getPlayerNumber()-1]+=piece.getNumberOfBlocks();

       // System.out.println(move.getPiece().getLabel()+" "+playerScores[0]+" "+playerScores[1]+" "+playerScores[2]+" "+playerScores[3]);
        for(int score:playerScores) if(playerScores[playerOfInterest]>score) return 0;//loss
        return 1;//win

    }

    public double getUCB1(){
        return ucbScore;
    }

    public double computeUCB(){
        ucbScore= (double)score/(double)visitiedNum + Node.c*Math.sqrt(Math.log(parent.getVisitiedNum())/(double)visitiedNum)+(double)move.getPiece().getNumberOfBlocks()/10.0;
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

    public int getVisitiedNum() {
        return visitiedNum;
    }

    public void addVisitiedNum() {
        this.visitiedNum ++;
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
