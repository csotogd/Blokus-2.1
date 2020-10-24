package MonteCarlo;

import GameBoard.Board;
import Move.Move;
import Player.Player;

public class MonteCarlo {

    Player[] players;
    Board board;

    /**
     * COnstructor
     * @param pls players in the current game, order matters !
     * @param bo board
     */
    public MonteCarlo(Player[] pls, Board bo){
        players= pls;
        board = bo;
    }

    public Move simulation(int player, long timeLimit){
        long start = System.currentTimeMillis();
        int[][] dboard = new int[board.board.length][board.board[0].length];
        for (int i = 0; i < board.board.length; i++) { //make a copy of the board
            System.arraycopy(board.board[i],0,dboard[i],0,board.board[0].length);
        }
        //make a copy of each player
        //make a list of all possible moves
        while(System.currentTimeMillis()-start<timeLimit){
            //chose one of the possible move
            //simulate turn by turn until the end -> back propagate score
        }
        return null;
    }
}
