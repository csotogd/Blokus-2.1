package Move;

import DataBase.Piece;
import DataBase.PieceFactory;
import GameBoard.Corner;
import Player.*;
import Tools.Vector2d;
import GameBoard.Board;

import java.util.ArrayList;

public class Move {
    private Player player;
    private Piece piece;//needs to be rotated, mirror as the players wants
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
         *  For a move to be allowed, the following 5 conditions have to be TRUE
         *
         * 1. every "block" of the piece should be occupying an empty board space
         * 2. There has to be a piece touching the corner of at least ONE of the blocks
         * 3. NONE of the blocks is in contact with a piece of the same player in a none- CORNER context
         * 4. none of its blocks is out of bounds
         * 5. piece was not used
         */

        //TODO: add a method or just a variable to see if it's the first piece instead of verifying it in noDirectContact AND cornerContact
        return (!piece.isUsed()) && inBounds(board) &&
                emptySpace(board) && cornerContact(board) && noDirectContact(board) ;
    }


    private boolean inBounds(Board board){


    if(this.position.get_x()<0||this.position.get_y()<0||
            this.position.get_x()>=board.getDIMENSION().get_x()||this.position.get_y()>=board.getDIMENSION().get_y()) return false;
    if(this.position.get_x()+piece.getShape()[0].length>=board.getDIMENSION().get_x()||
            this.position.get_y()+piece.getShape().length>=board.getDIMENSION().get_y()) return false;
    return true;

    }


    /**
     * checks if every piece block can be place in an empty square of the board
     * throws a not caught exception if piece is out of bounds.
     * to be executed together with inBOunds (if this method is after InBounds() in a conditional it will never be executed if is not in bounds)
     * @param board
     * @return
     */
    private boolean emptySpace(Board board){

    //every block occupies an empty space?
    for(int i=0; i<piece.getShape().length; i++){
        for(int j=0; j<piece.getShape()[0].length; j++){
            if(piece.getShape()[i][j]!=0 &&
                    board.board[i+position.get_x()][j+position.get_y()] != 0)
                return false;

        }
    }
    return true;

}


    /**
     * checks if any of the pieces block is in direct contact (none corner) of another piece of the same player
     * @param board
     * @return true if no direct contact exists
     */
    private boolean noDirectContact(Board board){
        //TODO: this bit here can go in a method
    boolean corner = false;
    for(int x=0;x<piece.getShape()[0].length;x++)
        for (int y = 0; y < piece.getShape().length; y++)
            if(position.get_x()+x==player.getStartingCorner().get_x()&&
            position.get_y()+y==player.getStartingCorner().get_y()&&
            piece.getShape()[y][x]!=0) corner=true;
    if(corner)
        return true; //!! it doesn't check if there is already a piece that occupies this position because
    // it assumes it was already checked
    for(int x=0;x<piece.getShape()[0].length;x++) {
        for (int y = 0; y < piece.getShape().length; y++) {
            if(piece.getShape()[position.get_y()+y][position.get_x()+x]!=0){
                if(position.get_y()+y+1<board.getDIMENSION().get_y() && board.board[position.get_y()+y+1][position.get_x()+x]==player.getPlayerNumber()) return false;
                if(position.get_y()+y-1>=0 && board.board[position.get_y()+y-1][position.get_x()+x]==player.getPlayerNumber()) return false;
                if(position.get_x()+x+1<board.getDIMENSION().get_x() && board.board[position.get_y()+y][position.get_x()+x+1]==player.getPlayerNumber()) return false;
                if(position.get_x()+x-1>=0 && board.board[position.get_y()+y][position.get_x()+x-1]==player.getPlayerNumber()) return false;

            }
        }
    }
    return true;

    //NONE of the blocks is in contact with another piece of the same player in a none- CORNER context?

  /*  for(int i=0; i<piece.getShape().length; i++){
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
    return true;        */

}

    /**
     *
     * @param board
     * @return true if there is any direct contact with ANY of the blocks. * (none-limiting blocks including)
     * since it will be used with methods noDirectContact() and emptySpace(), * should not be a problem.
     *
     */
    private boolean cornerContact(Board board){
        /**
         * it gets all corners from the piece with coordinates on the board,
         * it then checks for expected position on the board if it's occupied by a block of the player
         * finally calls isCorner that checks if there is only one block on the board
         */
        //TODO: this bit here as well
        boolean corner = false;
        for(int x=0;x<piece.getShape()[0].length;x++)
            for (int y = 0; y < piece.getShape().length; y++)
                if(position.get_x()+x==player.getStartingCorner().get_x()&&
                        position.get_y()+y==player.getStartingCorner().get_y()&&
                        piece.getShape()[y][x]!=0) corner=true;
        if(corner)
            return true;

        for(Corner pieceCorner: piece.getCornersContacts(position)){
            for(Vector2d board_cor:pieceCorner.getToCornerPositions()) {
                if (board.inBoard(board_cor) &&
                        board.board[board_cor.get_y()][board_cor.get_x()] == player.getPlayerNumber() &&
                        isCorner(pieceCorner.getPosition(), board_cor, board)) return true;
            }
        }
        return false;
 /*   for(int i=0; i<piece.getShape().length; i++){
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
    return false;       */
}

    /**
     * this method checks if two positions on the board is effectively touching corners to corners
     * @param p1 first position
     * @param p2 second position
     * @param board the game board
     * @return
     */
    private boolean isCorner(Vector2d p1, Vector2d p2, Board board) {
        int count=0;
        for (int i = Math.min(p1.get_y(),p2.get_y()); i < 2; i++) {
            for (int j = Math.min(p1.get_x(), p2.get_x()); j < 2; j++) {
                if(board.board[i][j]==player.getPlayerNumber()) count++;
            }
        }
        if(count == 1) return true;
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
            player.getMoveLog().push(this);

            return true;
        }
        else
            System.out.println("Move not allowed");
        return false;
        }

    public Player getPlayer() {
        return player;
    }

    public Piece getPiece() {
        return piece;
    }

    public Vector2d getPosition() {
        return position;
    }

    public static void main(String[] args){
        Board board= new Board(2);
        HumanPlayer player1 = new HumanPlayer(1);
        HumanPlayer player2=new HumanPlayer(2);
        player1.setStartingCorner(new Vector2d(0,0));
        player2.setStartingCorner(new Vector2d(board.getDIMENSION().get_x()-1,board.getDIMENSION().get_y()-1));
        player1.setPiecesList(PieceFactory.get().getAllPieces());
        System.out.println(player1.getPiecesList().get(1));
        Move move = new Move(player1,player1.getPiecesList().get(1),new Vector2d(0,0));
        System.out.println(move.isAllowed(board));

    }
}
