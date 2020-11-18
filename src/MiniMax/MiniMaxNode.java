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
    private int score;
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
        score = 0;
    }
/*
    public void expand(Player player){
        for (Move move: player.possibleMoveSet(state)) {
            System.out.println(player.getPlayerNumber());
            MiniMaxNode node = new MiniMaxNode(this,move);
            this.children.add(node);
            node.expand(players[player.getNumber()%players.length]);
        }
    }

 */

    public int getDepth() {
        return depth;
    }

    public int getScore() {
        return score;
    }

    public List<MiniMaxNode> getChildren(){
        return children;
    }
}
