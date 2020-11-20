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
    }

    public float heuristics(){
        return move.getPiece().getNumberOfBlocks();
    }

    public Move getMove() {
        return move;
    }

    public int getDepth() {
        return depth;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<MiniMaxNode> getChildren(){
        return children;
    }

}
