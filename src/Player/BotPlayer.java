package Player;

import DataBase.Piece;

public class BotPlayer extends Player{

    public BotPlayer(int number)
    {
        this.number = number;
        this.humanPlayer=false;
        this.firstMove= true;
    }




        public BotPlayer(int number, String name)
        {
            this.number = number;
            this.humanPlayer=false;
            this.firstMove= true;

        }

    public Player clone(){
        Player p = new BotPlayer(this.number);
        p.setStartingCorner(this.startingCorner);
        for(Piece piece : this.piecesList) p.getPiecesList().add(piece.getPiece());
        p.setFirstMove(this.firstMove);
        return p;
    }

}
