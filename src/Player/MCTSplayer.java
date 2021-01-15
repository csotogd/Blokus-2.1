package Player;

import DataBase.Piece;
import MonteCarlo.MCTS;

public class MCTSplayer extends BotPlayer {

    MCTS mcts;
    public MCTSplayer(int number) {
        super(number);
    }

    public MCTSplayer(int number, String name) {
        super(number, name);
    }

    public MCTS getMcts() {
        return mcts;
    }

    public void setMcts(MCTS mcts) {
        this.mcts = mcts;
    }

    @Override
    public MCTSplayer clone() {
        MCTSplayer player = new MCTSplayer(this.number, this.name);
        player.setStartingCorner(this.startingCorner);
        for(Piece piece : this.piecesList) player.getPiecesList().add(piece.getPiece());
        player.setFirstMove(this.firstMove);
        player.humanPlayer = false;
        player.setColor(this.color);
        player.setPoints(this.points);

        player.setMcts(this.mcts);

        return player;
    }
}
