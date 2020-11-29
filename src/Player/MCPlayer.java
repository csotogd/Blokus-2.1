package Player;

import MonteCarlo.MonteCarlo;

public class MCPlayer extends BotPlayer{
    MonteCarlo mc;
    public MCPlayer(int number,String name) {
        super(number,name);
    }

    public MonteCarlo getMc() {
        return mc;
    }

    public void setMc(MonteCarlo mc) {
        this.mc = mc;
    }
}
