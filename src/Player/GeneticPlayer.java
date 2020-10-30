package Player;

import Move.Move;

import java.sql.SQLOutput;

public class GeneticPlayer extends BotPlayer {

    public GeneticPlayer(int number) {
        super(number);
    }

    /**
     *
     * @return the move chosen by the ai
     */
    public Move calculateMove(){
       //while we code all the different strategies, make this call the one you want to try
        System.out.println("In calculate move for genetic algorithm");
    return null;
    }

    //return type can be changed
    private void addsMostCorners(float weight){;}

    private void blocksMostCorners(float weight){;}

    private void closestToMiddle(float weight){;}

    private void biggestPiece(float weight){;}

    //This is only used at the very end if the game. Only write this strategy when we are done with everything else
    private void endGamePossibleMoves(float weight){;}


}
