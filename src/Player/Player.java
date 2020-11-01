package Player;

import DataBase.Piece;
import DataBase.PieceFactory;
import GameBoard.Board;
import GameBoard.Corner;
import Move.Move;
import Tools.Vector2d;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public abstract class Player {
    protected int number;
    protected List<Piece> piecesList = new ArrayList<Piece>();//list of pieces
    protected List<Piece> piecesUsed = new ArrayList<Piece>(); //list of pieces used
    protected String name;
    protected boolean humanPlayer;
    protected Color color;
    protected Vector2d startingCorner;//corner where the player starts the game (0,0) for player 1
    protected boolean skippedLastMove=false; //If the LAST move was not made due to no possible moves
    protected int points=0;
    protected boolean firstMove; // first time the player plays, he can place a piece in his starting corner
    protected Stack <Move> moveLog = new Stack<>();

    public boolean isFirstMove() {
        if(firstMove) return true;
        return false;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public void setNotFirstMove() {
        this.firstMove = false;
    }

    public int getPlayerNumber(){
        return this.number;
    }

    public String getName() {
        return name;
    }

    public List<Piece> getPiecesList() {
        return piecesList;
    }

    public void setPiecesList(List<Piece> piecesList) {
        this.piecesList = piecesList;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isHumanPlayer()
    {
        return this.humanPlayer == true;
    }

    public Color getColor() {
        return color;
    }

    public int getNumber() {
        return number;
    }
    public Stack<Move> getMoveLog() {
        return moveLog;
    }

    public boolean getSkippedLastMove() {
        return skippedLastMove;
    }

    public void setSkippedLastMove(boolean skippedLastMove) {
        this.skippedLastMove = skippedLastMove;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setStartingCorner(Vector2d startingCorner) { this.startingCorner = startingCorner; }

    public Vector2d getStartingCorner() {
        if(startingCorner==null) System.out.println("Please set the starting corner at the appropriate corner !!");
        return startingCorner; }

    public void setName(String name) {
        this.name = name;
    }

    public abstract Player clone();

    public void removePiece(String label){
        Piece pieceToRemove = null;
        for(Piece p: piecesList) if(p.getLabel().equalsIgnoreCase(label)){
            pieceToRemove=p;
            break;
        }
        piecesList.remove(pieceToRemove);
    }

    /**
     * For every possible corner, checks if any of the piece unused pieces of a player can be placed.
     * @return true if the actual can do at least one move with the pieces he has left in the current situation of the board.
     */
    public boolean possibleMove(Board board){
        //TODO for phase 2 we will probably make this move an arraylist with the moves or something similar instead of a boolean
        ArrayList<Corner> cornersOnBOard = board.getCorner(this.getStartingCorner());

        /*************DEBUGGING*********
         System.out.println("possible empty spaces touching corner for "+ name+" :   ");

         for (Corner corner : cornersOnBOard)
         for (Vector2d empty: corner.getToCornerPositions())
         empty.printVector();
         *******************/

        for (Piece piecetoClone: this.getPiecesList()){
            if (!piecetoClone.isUsed()) {
                Piece piece = piecetoClone.clone(); // we clone it cause we rotate it and we do not want that to affect the real piece displayed
                for (int i = 0; i < piece.getTotalConfig(); i++) { //TODO calculate only for permutations of a piece, for instance 1 instead of 4
                    piece.rotateLeft(); //try all possible rotations
                    //get all the corners for that piece.
                    if(i==piece.getNbRotation()-1) piece.rotateUpsideDown(); //TODO is done, but have to be tested
                    for (Corner pieceCorner : piece.getCornersContacts(new Vector2d(0, 0))) {
                        //piece.printShape();
                        Move firstMove = new Move(this, piece, startingCorner);
                        if (firstMove.isAllowed(board))
                            return true;

                        for (Corner corner : cornersOnBOard) {
                            for (Vector2d emptyCorner : corner.getToCornerPositions()) { //for all the possible empty squares that would become corner contact
                                //System.out.println("in for: ");emptyCorner.printVector();
                                //move the piece so that it is contact with the corner with the part of it we want
                                Vector2d positionOfPiece= emptyCorner.subtract(pieceCorner.getPosition());


                                Move move = new Move(this, piece, positionOfPiece);
                                //move.print();
                                //board.print();
                                //System.out.println();
                                if (move.isAllowed(board))
                                    return true; //if at least one move is allowed
                            }

                        }
                    }
                }
            }
        }
//        System.out.println("Method possibleMove() piece can not be placed");
        return false; // if no piece can be placed in any of the corners

    }

    /**
     *
     * @return ArrayList of current possible moves a player can make.
     */
    public ArrayList<Move> possibleMoveSet(Board board){
        ArrayList<Corner> cornersOnBoard = board.getCorner(this.getStartingCorner());
        ArrayList<Move> moveSet=new ArrayList<>();

        for (Piece piecetoClone: this.getPiecesList()){
            if (!piecetoClone.isUsed()) {
                Piece piece = piecetoClone.clone(); // we clone it cause we rotate it and we do not want that to affect the real piece displayed
                for (int i = 0; i < piece.getTotalConfig(); i++) {
                    //get all the corners for that piece.
                    if(this.isFirstMove()) {
                        Vector2d adjust = new Vector2d(0,0);
                        if(startingCorner.get_x()!=0) adjust.set_x(piece.getShape()[0].length-1);
                        if(startingCorner.get_y()!=0) adjust.set_y(piece.getShape().length-1);
                        Move firstMove = new Move(this, piece.clone(), startingCorner.subtract(adjust));
                        if (firstMove.isAllowed(board)) moveSet.add(firstMove);
                    }else
                        for (Corner pieceCorner : piece.getCornersContacts(new Vector2d(0, 0))) {
                            for (Corner corner : cornersOnBoard) {
                                for (Vector2d emptyCorner : corner.getToCornerPositions()) { //for all the possible empty squares that would become corner contact
                                    //move the piece so that it is contact with the corner with the part of it we want
                                    Vector2d positionOfPiece= emptyCorner.subtract(pieceCorner.getPosition());
                                    Move move = new Move(this, piece.clone(), positionOfPiece);
                                    if (move.isAllowed(board))
                                        moveSet.add(move);
                                }

                            }
                        }
                    piece.rotateRight();
                    if(i==piece.getNbRotation()-1) piece.rotateUpsideDown();
                }
            }
        }
        return moveSet;

    }

    public Move randomPossibleMove(Board board){
        ArrayList<Corner> cornersOnBoard = board.getCorner(this.getStartingCorner());//list of corners on the board
        Random r = new Random();
        if(piecesList.size()>0) {
            int randomPiece = r.nextInt(piecesList.size()); //take a random piece
            for (int i = 0; i < piecesList.size(); i++) {
                if (!piecesList.get((i + randomPiece) % piecesList.size()).isUsed()) {
                    Piece piece = piecesList.get((i + randomPiece) % piecesList.size()); // we don't need to CLONE it because the players are already a clone
                    int randomRotation = r.nextInt(piece.getTotalConfig()); // starts with a random rotation
                    for (int j = 0; j < randomRotation; j++) piece.rotateRight();
                    if(randomRotation>=piece.getNbRotation()-1) piece.rotateUpsideDown();

                    for (int j = 0; j < piece.getTotalConfig(); j++) {
                        //get all the corners for that piece.
                        if (this.isFirstMove()) { //if we only need to check the starting corner:
                            Vector2d adjust = new Vector2d(0,0);
                            if(startingCorner.get_x()!=0) adjust.set_x(piece.getShape()[0].length-1);
                            if(startingCorner.get_y()!=0) adjust.set_y(piece.getShape().length-1);
                            Move firstMove = new Move(this, piece, startingCorner.subtract(adjust));
                            if (firstMove.isAllowed(board)) {
                                return firstMove;
                            }
                        }else{ // verify corners on board
                            List<Corner> pieceCorner = piece.getCornersContacts(new Vector2d(0, 0));
                            for (int k = 0; k < pieceCorner.size(); k++) {
                                //if(cornersOnBoard.size()==0) System.out.println(startingCorner.get_x()+" "+startingCorner.get_y());//sometimes it bugs here
                                int randomCornerBoard = r.nextInt(cornersOnBoard.size());
                                for (int l = 0; l < cornersOnBoard.size(); l++) {
                                    for (Vector2d emptyCorner : cornersOnBoard.get((l + randomCornerBoard) % cornersOnBoard.size()).getToCornerPositions()) { //for all the possible empty squares that would become corner contact
                                        //move the piece so that it is contact with the corner with the part of it we want
                                        Vector2d positionOfPiece = emptyCorner.subtract(pieceCorner.get(k).getPosition());

                                        Move move = new Move(this, piece, positionOfPiece);
                                        if (move.isAllowed(board)) {
                                            return move;
                                        }

                                    }

                                }
                            }
                        }
                        piece.rotateRight();
                        if (j  == piece.getNbRotation() - 1)
                            piece.rotateUpsideDown();
                    }
                }
            }
        }
        //System.out.println("Method possibleMove() piece can not be placed for p"+getPlayerNumber());
        return null; // if no piece can be placed in any of the corners
    }


    public List<Piece> getPiecesUsed(){
        return this.piecesUsed;
    }

    public static void main(String[] args){
        HumanPlayer p1 = new HumanPlayer(1,"jo");
        HumanPlayer p2 = new HumanPlayer(2,"dos");
        p1.setStartingCorner(new Vector2d(0,0));
        p2.setStartingCorner(new Vector2d(19,19));
        p1.setPiecesList(PieceFactory.get().getAllPieces());
        p2.setPiecesList(PieceFactory.get().getAllPieces());
        Board board = new Board(new Player[]{p1,p2});

        Move m;

        //ArrayList<Move> moveset = p1.possibleMoveSet(board);
        int i = 15;
        while(i>0){
            m = (Move)p1.randomPossibleMove(board);
            System.out.println(m.getPiece()+" "+m.getPosition().get_x()+" "+m.getPosition().get_y());
            m.writePieceIntoBoard(board);
            i--;
        }

        board.print();
    }
}