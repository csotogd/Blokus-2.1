package Player;

import DataBase.Piece;
import GameBoard.Board;
import GameBoard.Corner;
import Move.Move;
import Tools.Vector2d;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class Player {
    protected int number;
    protected List<Piece> piecesList = new ArrayList<Piece>();
    protected List<Piece> piecesUsed = new ArrayList<Piece>();
    protected String name;
    protected boolean humanPlayer;
    protected Color color;
    protected Vector2d startingCorner;
    protected boolean skippedLastMove=false; //If the LAST move was not made due to no possible moves
    protected int points=0;

    protected Stack <Move> moveLog = new Stack<>();

    //TODO: implement logic of finding the appropriate corner, and find the location in the code to do it



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
                    if(i>=piece.getNbRotation()) piece.rotateUpsideDown(); //TODO is done, but have to be tested
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
        System.out.println("Method possibleMove() piece can not be placed");
    return false; // if no piece can be placed in any of the corners

    }
public List<Piece> getPiecesUsed(){
        return this.piecesUsed;
}

}