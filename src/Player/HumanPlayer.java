package Player;

import DataBase.Piece;

public class HumanPlayer extends Player {

    public HumanPlayer(int number, String name)
    {
        this.number = number;
        this.name= name;
        this.humanPlayer=true;
        this.firstMove=true;
    }

    public HumanPlayer(int number)
    {
        this.number = number;
        this.firstMove=true;
        this.humanPlayer=true;
    }

    public Player clone(){
        Player p = new HumanPlayer(this.number);
        p.setStartingCorner(this.startingCorner);
        for(Piece piece : this.piecesList) p.getPiecesList().add(piece.getPiece());
        return p;
    }
}
