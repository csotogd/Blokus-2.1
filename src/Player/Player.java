package Player;

import DataBase.Piece;
import Move.Move;
import Tools.Vector2d;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class Player {
    protected int number;
    protected List<Piece> piecesList = new ArrayList<Piece>();
    protected String name;
    protected boolean humanPlayer;
    protected Color color;
    protected Vector2d startingCorner;
    protected boolean skippedLastMove=false; //If the LAST move was not made due to no possible moves
    protected int points=0;

    protected Stack <Move> moveLog = new Stack<>();

    //TODO: implement logic of finding the appropriate corner, and find the location in the code to do it

    public int getPlayerNumber(){
        return this.number;
    }

    public String getName() {
        return name;
    }

    public List<Piece> getPiecesList() {
        return piecesList;
    }

    public void setPiecesList(List<Piece> piecesList) {
        this.piecesList = piecesList;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isHumanPlayer()
    {
        return this.humanPlayer == true;
    }

    public Color getColor() {
        return color;
    }

    public int getNumber() {
        return number;
    }
    public Stack<Move> getMoveLog() {
        return moveLog;
    }

    public boolean getSkippedLastMove() {
        return skippedLastMove;
    }

    public void setSkippedLastMove(boolean skippedLastMove) {
        this.skippedLastMove = skippedLastMove;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Vector2d getStartingCorner() { return startingCorner; }
}

