package MonteCarlo;

import GameBoard.Board;
import Move.Move;
import Player.Player;

public class MonteCarlo {

    Player[] players;
    Node root;

    /**
     * COnstructor
     * @param pls players in the current game, order matters !
     * @param bo board
     */
    public MonteCarlo(Player[] pls, Board bo){
        players= pls;
        root = new Node(bo);
    }

    public Move simulation(int player, long timeLimit){
        long start = System.currentTimeMillis();
        root.expand(this.players[player]);
        while(System.currentTimeMillis()-start<timeLimit){
            //chose one of the possible move
            //simulate turn by turn until the end -> back propagate score
        }
        return null;
    }
}
