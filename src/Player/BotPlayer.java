package Player;

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


}
