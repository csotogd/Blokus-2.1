package Player;

import DataBase.Piece;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected int number;
    protected List<Piece> piecesList = new ArrayList<Piece>();
    protected String name;
    protected boolean humanPlayer;
    protected Color color;

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
}

