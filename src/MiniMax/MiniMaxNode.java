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
    private Move killerMoves;

    /**
     * CONSTRUCTOR for the root (parent point to itself)
     * @param state current state of the game
     */
    public MiniMaxNode(Board state, Player player){
        this.board =state;
        this.parent = this;
        this.move = null;
        this.children = new ArrayList<>();
        this.score = new float[Data.getPlayersName().length];
        this.player = player;
    }

    /**
     * Regular node constructor
     * @param parent parent node
     * @param move move to make to arrive from parent to this node
     */
    public MiniMaxNode(MiniMaxNode parent, Move move,Player player,Board board){
        this.children = new ArrayList<>();
        this.parent = parent;
        this.parent.getChildren().add(this);
        this.move = move;
        this.board = board;
        this.player = player;
        move.writePieceIntoBoard(this.board);
    }


    public void removeMove(){
        for (int i = 0; i < move.getPiece().getShape().length; i++) {
            for (int j = 0; j < move.getPiece().getShape()[0].length; j++) {
                if(move.getPiece().getShape()[i][j]!=0) this.board.boardArray[move.getPosition().get_y()+i][move.getPosition().get_x()+j]=0;
            }
        }
    }



    public Move getMove() {
        return move;
    }

    public void setScore(float[] score) {
        this.score = score;
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

    public Move getKillerMoves() {
        return killerMoves;
    }

    public void setKillerMoves(Move killerMoves) {
        this.killerMoves = killerMoves;
    }
}