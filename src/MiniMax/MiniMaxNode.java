package MiniMax;

import DataBase.Data;
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
    private float[] score;
    private Player player;
    private int depth;
    private boolean wasSetToNegative;
    private boolean visited;

    /**
     * CONSTRUCTOR for the root (parent point to itself)
     * @param state current state of the game
     */
    public MiniMaxNode(Board state, Player player,int depth){
        this.board =state;
        this.depth = depth;
        this.parent = this;
        this.move = null;
        this.children = new ArrayList<>();
        this.score = new float[Data.getPlayersName().length];
        this.player = player;
        this.wasSetToNegative = false;
        this.visited = false;
    }

    /**
     * Regular node constructor
     * @param parent parent node
     * @param move move to make to arrive from parent to this node
     */
    public MiniMaxNode(MiniMaxNode parent, Move move, int depth,Player player,Board board){
        this.children = new ArrayList<>();
        this.parent = parent;
        this.parent.getChildren().add(this);
        this.move = move;
        this.depth = depth;
        this.board = board;
        this.player = player;
        this.wasSetToNegative = false;
        this.visited = false;
    }






    public Move getMove() {
        return move;
    }

    public int getDepth() {
        return depth;
    }

    public void setScore(float[] score) {
        this.score = score;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public List<MiniMaxNode> getChildren() {
        return children;
    }

    public MiniMaxNode getParent() {
        return parent;
    }

    public Board getBoard() {
        return board;
    }

    public float[] getScore() {
        return score;
    }

    public Player getPlayer() {
        return player;
    }
}
