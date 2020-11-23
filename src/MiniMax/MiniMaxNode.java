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
    private float score;
    private Player player;
    private int depth;
    private boolean wasSetToNegative;

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
        this.score = 0;
        this.player = player;
        this.wasSetToNegative = false;
    }

    /**
     * Regular node constructor
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
        //biggest piece heuristic
        int nbrOfBlocks = move.getPiece().getNumberOfBlocks();
        //closest to middle heuristic
        int area = player.getArea(board);
        //Adds most corners heuristic
        int nbrOfCorner = board.getCorner(player.getStartingCorner()).size();
        //the current state of the game
        int state = player.getPiecesUsed().size();
        List<Integer> heuristics = new ArrayList<>();
        heuristics.add(nbrOfBlocks);heuristics.add(area);heuristics.add(nbrOfCorner);
        //normalize the heuristics (to be able to use weight percentage)
        heuristics = normalize(heuristics);
        if(state<7){
            //beginning of the game
            return (0.1f*heuristics.get(0)+0.6f*heuristics.get(1)+0.3f*heuristics.get(2));
        }else if(state>=7&&state<12){
            //middle game
            return (0.1f*heuristics.get(0)+0.6f*heuristics.get(1)+0.3f*heuristics.get(2));
        }else{
            //end game
            return (0.1f*heuristics.get(0)+0.6f*heuristics.get(1)+0.3f*heuristics.get(2));
        }
    }

    public List<Integer> normalize(List<Integer>heuristics){
        int max = Integer.MIN_VALUE;
        for (Integer i:heuristics) {
            if(i>=max)max = i;
        }
        for (int i = 0;i<heuristics.size();i++) {
            heuristics.set(i,heuristics.get(i)/max);
        }
        return heuristics;
    }

    public void setNegative(){
        this.score = -this.score;
    }

    public float getScore() {
        if(this.wasSetToNegative){
            float negScore = this.score;
            this.score = -negScore;
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
