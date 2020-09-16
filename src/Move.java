import DataBase.Piece;
import Player.Player;
import Tools.Vector2d;
import GameBoard.Board;

import java.util.Vector;

public class Move {
    private Player player;
    private Piece piece;
    private Vector2d position; //Position of the top-left corner of the piece. top-left corner of the board is 0,0



    public Move(Player player, Piece piece, Vector2d position)
    {
        this.player = player;
        this.piece= piece;
        this.position=position;

    }


    //TODO test this method
    /**
     * Has not been tested yet
     * @param board board in which we place want to place the piece
     * @return whether it is possible to place that piece
     */
    public boolean isAllowed(Board board){
        /*
         *  For a move to be allowed, the following 3 conditions have to be TRUE
         *
         * 1. every "block" of the piece should be occupying an empty board space
         * 2. There has to be a piece touching the corner of at least ONE of the blocks
         * 3. NONE of the blocks is in contact with a piece of the same player in a none- CORNER context
         * 4. none of its pieces is out of bounds
         * 5. piece was not used
         */


        return (!piece.isUsed()) && inBounds(board) &&
                emptySpace(board) && cornerContact(board) && noDirectContact(board) ;
    }


    private boolean inBounds(Board board){
    try {
        //every block occupies an empty space?
        for (int i = 0; i < piece.getShape().length; i++) {
            for (int j = 0; j < piece.getShape()[i].length; j++) {
                if (piece.getShape()[i][j] == 0)
                    ; //arbitrary operation to check for out of bounds Exception
            }
        }
    }catch(ArrayIndexOutOfBoundsException e){
            return false;
        }
       return true;
    }


    /**
     * checks if every piece block can be place in an empty square of the board
     * @param board
     * @return
     */
    private boolean emptySpace(Board board){

    //every block occupies an empty space?
    for(int i=0; i<piece.getShape().length; i++){
        for(int j=0; j<piece.getShape()[0].length; j++){
            if(piece.getShape()[i][j]==0)
                ;//that´s not a block
            else{
                if (board.board[i+position.get_x()][j+position.get_y()] != 0)
                    return false;
            }

        }
    }
    return true;

}


    /**
     * checks if any of the pieces block is in direct contact of another piece of the same player
     * @param board
     * @return true if no direct contact exists
     */
    private boolean noDirectContact(Board board){

    //NONE of the blocks is in contact with another piece of the same player in a none- CORNER context?

    for(int i=0; i<piece.getShape().length; i++){
        for(int j=0; j<piece.getShape()[0].length; j++){
            if(piece.getShape()[i][j]==0)
                ;//that´s not a block
            else {
                //if that block is not part of the piece, but has the player id in the board, then it is another
                //piece of the same player
                        //do it for all 4 corners of a piece
                try {
                    if (piece.getShape()[i + 1][j] == 0 &&
                            board.board[position.get_x() + i + 1][position.get_y() + j] == piece.getNumber())
                        return false;
                }catch (IndexOutOfBoundsException e){ ;}

                try {


                    if (piece.getShape()[i - 1][j] == 0 &&
                            board.board[position.get_x() + i - 1][position.get_y() + j] == piece.getNumber())
                        return false;
                }catch(IndexOutOfBoundsException e){}

                try {
                    if (piece.getShape()[i][j + 1] == 0 &&
                            board.board[position.get_x() + i][position.get_y() + j + 1] == piece.getNumber())
                        return false;
                }catch (IndexOutOfBoundsException e){}

                try {


                    if (piece.getShape()[i][j - 1] == 0 &&
                            board.board[position.get_x() + i][position.get_y() + j - 1] == piece.getNumber())
                        return false;
                }catch (IndexOutOfBoundsException e){}

            }

        }
    }
    return true;

}

    /**
     *
     * @param board
     * @return true if there is any direct contact with ANY of the blocks. * (none-limiting blocks including)
     * since it will be used with methods noDirectContact() and emptySpace(), * should not be a problem.
     *
     */
    private boolean cornerContact(Board board){

    for(int i=0; i<piece.getShape().length; i++){
        for(int j=0; j<piece.getShape()[0].length; j++){
            if(piece.getShape()[i][j]==0)
                ;//that´s not a block
            else {
                //if that block is not part of the piece, but has the player id in the board, then it is another
                //piece of the same player
                //do it for all 4 corners of a piece
                try {
                    if (piece.getShape()[i + 1][j + 1] == 0 &&
                            board.board[position.get_x() + i + 1][position.get_y() + j + 1] == piece.getNumber())
                        return true;
                }catch (IndexOutOfBoundsException e){}

                try {

                    if (piece.getShape()[i + 1][j-1] == 0 &&
                            board.board[position.get_x() + i +1 ][position.get_y() + j-1] == piece.getNumber())
                        return true;
                }catch (IndexOutOfBoundsException e){}

                try {
                    if (piece.getShape()[i-1][j+1] == 0 &&
                            board.board[position.get_x() + i-1][position.get_y() + j+1] == piece.getNumber())
                        return true;
                }catch (IndexOutOfBoundsException e){}

                try{
                    if (piece.getShape()[i-1][j-1] == 0 &&
                            board.board[position.get_x() + i-1][position.get_y() + j-1] == piece.getNumber())
                        return true;
                }catch (IndexOutOfBoundsException e){}
            }

        }
    }
    return false;
}

    /**
     *writes the piece into the board,no further calculations.
     * If the move is not possible it will aim to do it anyway.
     * To be used after isALlowed
     */
public void writePieceIntoBoard(Board board) {
    for (int i=0; i<piece.getShape().length; i++){
        for(int j=0; j<piece.getShape()[0].length; j++){
            if ( board.board[position.get_x()+ i][ position.get_y()+ j] !=0)
            board.board[position.get_x()+ i][ position.get_y()+ j] =player.getPlayerNumber();
        }
    }

}

    /**
     * 1. check is piece is allowed
     * 2. writes it into the board
     * 3. marks it as used
     * @param board
     * @return true if it was possible, false if the move was not executed
     */
    public boolean makeMove(Board board){
        if(this.isAllowed(board)) {
            //add piece to the board
            this.writePieceIntoBoard(board);
            piece.setUsed(true);
            return true;
        }
        return false;
        }


}
