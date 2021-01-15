package Player;

import DataBase.Piece;

public class MiniMaxPlayer extends BotPlayer{
    private String playerType;

    public MiniMaxPlayer(int playerNbr, String playerType){
        super(playerNbr);
        this.playerType = playerType;
    }

    public MiniMaxPlayer(int playerNbr){
        super(playerNbr);
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public String getPlayerType() {
        return playerType;
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

        player.setPlayerType(this.playerType);

        return player;
    }
}
