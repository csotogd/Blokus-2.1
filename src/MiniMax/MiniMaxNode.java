package MiniMax;

import GameBoard.Board;

import Move.Move;
import Player.Player;
import java.util.ArrayList;
import java.util.List;

public class MiniMaxNode {
    private MiniMaxNode parent;
    private Board board;
    private Move move;
    private List<MiniMaxNode> children;
    private float score;
    private Player player;
    private int depth;
    private boolean wasSetToNegative;

    /**
     * CONSTRUCTOR for the root (parent point to itself)
     * @param state current state of the game
     */
    public MiniMaxNode(Board state, Player player){
        this.board =state;
        this.depth = 0;
        this.parent = this;
        this.move = null;
        this.children = new ArrayList<>();
        this.score = 0;
        this.player = player;
        this.wasSetToNegative = false;
    }

    /**
     * Regular constructor
     * @param parent parent node
     * @param move move to make to arrive from parent to this node
     */
    public MiniMaxNode(MiniMaxNode parent, Move move, int depth,Player player){
        this.children = new ArrayList<>();
        this.parent = parent;
        this.move = move;
        this.depth = depth;
        this.board = parent.board;
        this.player = player;
        this.score = heuristics();
        this.wasSetToNegative = false;
    }

    public float heuristics(){
        return move.getPiece().getNumberOfBlocks();
    }

    public void setNegative(){
        this.score = -this.score;
        this.wasSetToNegative = true;
    }

    public float getScore() {
        if(this.wasSetToNegative){
            float negScore = this.score;
            this.score = -this.score;
            this.wasSetToNegative = false;
            return negScore;
        }
        return score;
    }

    public Move getMove() {
        return move;
    }

    public int getDepth() {
        return depth;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public MiniMaxNode getParent(){
        return parent;
    }

}
