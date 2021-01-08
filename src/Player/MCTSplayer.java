package Player;

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
}
