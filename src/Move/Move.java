package Move;

import DataBase.Piece;
import DataBase.PieceFactory;
import GameBoard.Corner;
import Player.*;
import Tools.Vector2d;
import GameBoard.Board;

import java.util.ArrayList;

/*
* Wheneverr we are going to define a move, make it, see if it is a possible move, etc....we use this class
* Contains method to check if a move is valid.
* Every time we make a move, a lot of operations are triggered. Every player stores a log of moves, the ui
* is updated, the piece is set as used......
* */
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

        if(piece.isUsed()){
           // System.out.println("piece used");
            return false;
        }else if(!inBounds(board)){
           // System.out.println("out of bound");
            return false;
        }else if(!emptySpace(board)){
            //System.out.println("place occupied");
            return false;
        }else if(!cornerContact(board)&&!firstLegalMove(board)){
            //System.out.println("no corner?");
            return false;
        }else if(!noDirectContact(board)){
           // System.out.println("contact with piece");
            return false;
        }
        return true ;
        //(!piece.isUsed()) && inBounds(board) &&
        //                emptySpace(board) && cornerContact(board) && noDirectContact(board)
    }

    private boolean firstLegalMove(Board board) {
        if(!player.isFirstMove()) return false;
        for (int i = 0; i < piece.getShape().length; i++) {
            for (int j = 0; j < piece.getShape()[0].length; j++) {
                if(piece.getShape()[i][j]!=0 &&
                        position.get_y()+i==player.getStartingCorner().get_y() &&
                        position.get_x()+j==player.getStartingCorner().get_x())
                    return true;
            }
        }return false;
    }

    /**
     * Checks if the current piece with current coordinates are in bound
     * @param board board on which the move is played
     * @return True if in boun,false if out of bound
     */
    private boolean inBounds(Board board){
        if(this.position.get_x()<0||this.position.get_y()<0||
                this.position.get_x()>=board.getDIMENSION()||this.position.get_y()>=board.getDIMENSION()) return false;
        if(this.position.get_x()+piece.getShape()[0].length>board.getDIMENSION()||
                this.position.get_y()+piece.getShape().length>board.getDIMENSION()) return false;
        return true;
    }


    public Move() {
        super();
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
                        board.board[i+position.get_y()][j+position.get_x()] != 0)
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

    for(int x=0;x<piece.getShape()[0].length;x++) {
        for (int y = 0; y < piece.getShape().length; y++) {
            if(piece.getShape()[y][x]!=0){
                if(position.get_y()+y+1<board.getDIMENSION() && board.board[position.get_y()+y+1][position.get_x()+x]==player.getPlayerNumber()) return false;
                if(position.get_y()+y-1>=0 && board.board[position.get_y()+y-1][position.get_x()+x]==player.getPlayerNumber()) return false;
                if(position.get_x()+x+1<board.getDIMENSION() && board.board[position.get_y()+y][position.get_x()+x+1]==player.getPlayerNumber()) return false;
                if(position.get_x()+x-1>=0 && board.board[position.get_y()+y][position.get_x()+x-1]==player.getPlayerNumber()) return false;

                }
            }
        } return true;
    }


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


        for(Corner pieceCorner: piece.getCornersContacts(position)){
            for(Vector2d board_cor:pieceCorner.getToCornerPositions()) {
             /*   if(pieceCorner.getPosition().get_y()==1&&pieceCorner.getPosition().get_x()==3) {
                    System.out.print(board.board[board_cor.get_y()][board_cor.get_x()] == player.getPlayerNumber());
                    System.out.print(isCorner(pieceCorner.getPosition(), board_cor, board));
                }
                System.out.println();*/

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
        for (int i =0 ; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if(board.board[Math.min(p1.get_y(),p2.get_y())+i][Math.min(p1.get_x(), p2.get_x())+j]==player.getPlayerNumber()) count++;
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
        for (int i = 0; i < piece.getShape().length; i++) {
            for (int j = 0; j < piece.getShape()[0].length; j++) {
                if (board.board[position.get_y() + i][position.get_x() + j] == 0 &&
                        piece.getShape()[i][j] != 0)
                    board.board[position.get_y() + i][position.get_x() + j] = player.getPlayerNumber();
            }
        }
        if(player.isFirstMove()) player.setFirstMove(false);
    }

    /**
     * 1. check is piece is allowed, if it is:
     * 2. writes it into the board
     * 3. marks it as used
     * 4. stores the move in the player log
     * @param board
     * @return true if it was possible, false if the move was not executed
     */
    public boolean makeMove(Board board){
        if(this.isAllowed(board)) {
    //        System.out.println("Move allowed");
            //add piece to the board
            this.writePieceIntoBoard(board);
            player.getMoveLog().push(this);
            this.piece.setUsed(true);//TODO erase this none sense line of code, completely useless
            this.player.getPiecesUsed().add(this.piece);
            //System.out.println("number of blocks from make move: "+piece.getNumberOfBlocks());
            if(player.isFirstMove()) player.setFirstMove(false);

            return true;
        }
        else
 //           System.out.println("Move not allowed");
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

    //dont mind this, it´s just testing stuff
    public static void main(String[] args){
        HumanPlayer player1 = new HumanPlayer(1);
        HumanPlayer player2=new HumanPlayer(2);
        Board board= new Board(new Player[]{player1,player2});

        player1.setStartingCorner(new Vector2d(0,0));
        player2.setStartingCorner(new Vector2d(board.getDIMENSION()-1,board.getDIMENSION()-1));
        player1.setPiecesList(PieceFactory.get().getAllPieces());
        player2.setPiecesList(PieceFactory.get().getAllPieces());
        System.out.println(player1.getPiecesList().get(1));
        Piece fpiece=null;
        for(Piece p: player1.getPiecesList()){
            if(p.getLabel().equals("F")) fpiece = p;
        }
        Move move = new Move(player1,fpiece,new Vector2d(3,0));
        System.out.println(move.isAllowed(board));
        Move firstMove = new Move(player1,player1.getPiecesList().get(2),new Vector2d(0,0));
        if(firstMove.isAllowed(board)){
            System.out.println("first move");
            firstMove.makeMove(board);
//            firstMove.writePieceIntoBoard(board);
        }
        //move.writePieceIntoBoard(board);
        for(int[] line : board.board){
            for(int i : line){
                System.out.print(i);
            }
                System.out.println();
        }
//        for(Corner c: fpiece.getCornersContacts(move.getPosition())){
//            System.out.print(c.getPosition().get_x()+" "+c.getPosition().get_y()+" -- ");
//            for(Vector2d complement:c.getToCornerPositions()) System.out.print(complement.get_x()+" "+complement.get_y()+"/");
//            System.out.println();
//        }
        System.out.println((!move.piece.isUsed()) +" "+ move.inBounds(board) +" "+
                move.emptySpace(board) +" "+ move.cornerContact(board) +" "+ move.noDirectContact(board));
        if(move.isAllowed(board)){
            System.out.println("second move");
            move.makeMove(board);
//            firstMove.writePieceIntoBoard(board);
        }


        Move move3 = new Move(player1,player1.getPiecesList().get(3),new Vector2d(6,1));
        if(move3.isAllowed(board)){
            move3.makeMove(board);
        }

        Piece o=null;
        for(Piece p:player2.getPiecesList()){
            if(p.getLabel().equalsIgnoreCase("O4")) o = p;
        }
        System.out.println(o);
        Move movep2 = new Move(player2,o,new Vector2d(18,18));
        System.out.println((!movep2.piece.isUsed()) +" "+ movep2.inBounds(board) +" "+
                movep2.emptySpace(board) +" "+ movep2.cornerContact(board) +" "+ movep2.noDirectContact(board));
        if(movep2.isAllowed(board)){
            movep2.makeMove(board);
        }

        Piece o1 = null;
        for(Piece p : player1.getPiecesList()) if(p.getLabel().equalsIgnoreCase("O4")) o1 = p;
        Move om = new Move(player1,o1,new Vector2d(1,2));
        om.makeMove(board);
        System.out.println((!om.piece.isUsed()) +" "+ om.inBounds(board) +" "+
                om.emptySpace(board) +" "+ om.cornerContact(board) +" "+ om.noDirectContact(board));

        Piece l1 = null;
        for(Piece p : player1.getPiecesList()) if(p.getLabel().equalsIgnoreCase("I5")) l1 = p;
        l1.rotateRight();
        Move lm = new Move(player1,l1,new Vector2d(10,2));
        lm.makeMove(board);

        System.out.println(l1);
        System.out.println((!lm.piece.isUsed()) +" "+ lm.inBounds(board) +" "+
                lm.emptySpace(board) +" "+ lm.cornerContact(board) +" "+ lm.noDirectContact(board));
        for(int[] line : board.board){
            for(int i : line){
                System.out.print(i);
            }
            System.out.println();
        }

    }
public void print(){
    System.out.println( "Player: "+player.getName()+" \tPosition: "+position.get_x()+", "+ position.get_y()+"\t") ;
    System.out.println("piece: ");
    piece.printShape();

}

}
