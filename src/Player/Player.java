package Player;

import DataBase.Piece;
import Tools.Vector2d;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected int number;
    protected List<Piece> piecesList = new ArrayList<Piece>();
    protected String name;
    protected boolean humanPlayer;
    protected Color color;
    protected Vector2d startingCorner;//TODO: implement logic of finding the appropriate corner, and find the location in the code to do it

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

    public Vector2d getStartingCorner() { return startingCorner; }
}

