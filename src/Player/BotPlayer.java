package Player;

import DataBase.Piece;
import Move.Move;

//this class would made more sense as an abstract class, try if it is possible later without breaking code in the initialization of the game
public  class BotPlayer extends Player{

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

        public Move calculateMove(){
            System.out.println("calculate move to be called from subclasses of botplayer");
        return null;
        }

    public Player clone(){
        Player p = new BotPlayer(this.number);
        p.setStartingCorner(this.startingCorner);
        for(Piece piece : this.piecesList) p.getPiecesList().add(piece.getPiece());
        p.setFirstMove(this.firstMove);
        return p;
    }

}
