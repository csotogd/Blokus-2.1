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
    private static double c=8;
    private Player[] players;

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
            if(players[count-1].getPlayerNumber()==move.getPlayer().getPlayerNumber())
                players[count-1].removePiece(move.getPiece().getLabel());
        }
        move.writePieceIntoBoard(state);
        children = new ArrayList<>();
        visitiedNum=0;
        score = 0;
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
        int countPass=0;
        Board board = state.clone();
        while(countPass<players.length){
            if(playerturn==0) countPass=0;
            Move move = players[playerturn].randomPossibleMove(board);
            if(move!=null) {
                move.writePieceIntoBoard(board);
                players[playerturn].removePiece(move.getPiece().getLabel());
            }else{
                countPass++;
            }
            playerturn = (playerturn+1)%players.length;
        }

        int[] playerScores=new int[players.length];
        for(Player p: players) for(Piece piece: p.getPiecesList()) playerScores[p.getPlayerNumber()-1]+=piece.getNumberOfBlocks();

        //System.out.println(move.getPiece().getLabel()+" "+playerScores[0]+" "+playerScores[1]+" "+playerScores[2]+" "+playerScores[3]);
        for(int score:playerScores) if(playerScores[playerOfInterest]>score) return 0;
        return 1;

    }

    public double getUCB1(){
        return (double)score/(double)visitiedNum + Node.c*Math.sqrt(Math.log(parent.getVisitiedNum())/(double)visitiedNum);
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
        if(parent!=this) parent.addVisitiedNum();
    }

    public int getScore() {
        return score;
    }

    public void addScore() {
        this.score ++;
        if(parent!=this) parent.addScore();
    }
}
