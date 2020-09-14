package Player;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected int number;
    protected List<Piece> piecesList = new ArrayList<Piece>();
    protected String name;
    protected boolean humanPlayer;

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

    public boolean isHumanPlayer()
    {
        return this.humanPlayer == true;
    }
}

