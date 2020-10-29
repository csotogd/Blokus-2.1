package MonteCarlo;

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
    private static double c=2;

    /**
     * CONSTRUCTOR for the root (parent point to itself)
     * @param state current state of the game
     */
    public Node(Board state){
        this.state=state;
        parent = this;
        move = null;
        children = new ArrayList<>();
        visitiedNum=0;
        score = 0;
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
     * @param p
     * @return
     */
    public int simulation(Player p){
        return 0;
    }

    public double getUCB1(){
        return (double)score/(double)visitiedNum + Node.c*Math.sqrt(Math.log(parent.getVisitiedNum())/(double)visitiedNum);
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
