package Player;

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
}
