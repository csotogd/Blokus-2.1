package Player;

import DataBase.Piece;
import GameBoard.Board;
import Move.Move;


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
            this.name = name;

        }

        public Move calculateMove(Board board){
            System.out.println("calculate move to be called from subclasses of botplayer");
        return null;
        }

    public BotPlayer clone(){
        BotPlayer player = new BotPlayer(this.number);
        player.setName(this.name);
        player.setStartingCorner(this.startingCorner);
        for(Piece piece : this.piecesList) player.getPiecesList().add(piece.getPiece());
        player.setFirstMove(this.firstMove);
        player.humanPlayer = false;
        player.setColor(this.color);
        player.setPoints(this.points);

        return player;
    }

}
