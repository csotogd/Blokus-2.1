package Player;

import DataBase.Piece;

public class MiniMaxPlayer extends BotPlayer{

    public MiniMaxPlayer(int playerNbr){
        super(playerNbr);
    }

    public MiniMaxPlayer clone(){
        MiniMaxPlayer player = new MiniMaxPlayer(this.number);
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
