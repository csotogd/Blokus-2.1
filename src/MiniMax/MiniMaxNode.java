package MiniMax;

import GameBoard.Board;

import Move.Move;
import Player.Player;
import java.util.ArrayList;
import java.util.List;

public class MiniMaxNode {
    private MiniMaxNode parent;
    private Board state;
    private Move move;
    private List<MiniMaxNode> children;
    private float score;
    private Player[] players;
    private int depth;
    private boolean wasSetToNegative;

    /**
     * CONSTRUCTOR for the root (parent point to itself)
     * @param state current state of the game
     */
    public MiniMaxNode(Board state, Player[] ps){
        this.state=state;
        this.depth = 0;
        parent = this;
        move = null;
        children = new ArrayList<>();
        score = 0;
        players = ps;
        this.wasSetToNegative = false;
    }

    /**
     * Regular constructor
     * @param parent parent node
     * @param move move to make to arrive from parent to this node
     */
    public MiniMaxNode(MiniMaxNode parent, Move move, int depth){
        this.children = new ArrayList<>();
        this.parent = parent;
        this.move = move;
        this.depth = depth;
        this.state = parent.state;
        this.players = parent.players;
        score = heuristics();
        this.wasSetToNegative = false;
    }

    public float heuristics(){
        return move.getPiece().getNumberOfBlocks();
    }

    public void setNegative(){
        this.score = -score;
        this.wasSetToNegative = true;
    }

    public Move getMove() {
        return move;
    }

    public int getDepth() {
        return depth;
    }

    public float getScore() {
        if(this.wasSetToNegative){
            float negScore = this.score;
            this.score = -score;
            this.wasSetToNegative = false;
            return negScore;
        }
        return score;
    }
    public void setScore(float score) {
        this.score = score;
    }

    public MiniMaxNode getParent(){
        return parent;
    }

}
