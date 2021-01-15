package Player;

import DataBase.Piece;
import MonteCarlo.MonteCarlo;

public class MCPlayer extends BotPlayer{
    MonteCarlo mc;
    public MCPlayer(int number,String name) {
        super(number,name);
    }

    public MCPlayer(int number){
        super(number);
    }

    public MonteCarlo getMc() {
        return mc;
    }

    public void setMc(MonteCarlo mc) {
        this.mc = mc;
    }

    @Override
    public MCPlayer clone() {
        MCPlayer player = new MCPlayer(this.number, this.name);
        player.setStartingCorner(this.startingCorner);
        for(Piece piece : this.piecesList) player.getPiecesList().add(piece.getPiece());
        player.setFirstMove(this.firstMove);
        player.humanPlayer = false;
        player.setColor(this.color);
        player.setPoints(this.points);

        player.setMc(this.mc);

        return player;
    }
}
