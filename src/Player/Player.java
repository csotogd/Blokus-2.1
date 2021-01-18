package Player;

import DataBase.Piece;
import DataBase.PieceFactory;
import GameBoard.Board;
import GameBoard.Corner;
import Move.Move;
import Tools.Vector2d;
import javafx.scene.paint.Color;

import java.util.*;

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
    protected List<Piece> unplayablePiece; //piece that couldn't be place last time we called "possibleMoveUpdate()" method

    public boolean isFirstMove() {
        return firstMove;
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

    public boolean removePiece(String label){
        Piece pieceToRemove = null;
        for(Piece p: piecesList) if(p.getLabel().equalsIgnoreCase(label)){
            pieceToRemove=p;
            return piecesList.remove(pieceToRemove);
        }
        return false;
    }



    public void reOrderPieceList(){
        //sort pieces depending on their nbr of blocks
        int[] nbrOfBlocksPerPieces = new int[piecesList.size()];
        int counter = 0;
        for (Piece piece:piecesList){
            nbrOfBlocksPerPieces[counter] = piece.getNumberOfBlocks();
            counter++;
        }

        List<List<Piece>> allPiecesPerNbrOfBlocks= new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            allPiecesPerNbrOfBlocks.add(i,new ArrayList<Piece>());
        }
        for (int i = 0; i < nbrOfBlocksPerPieces.length; i++) {
            if(nbrOfBlocksPerPieces[i]==5){
                allPiecesPerNbrOfBlocks.get(0).add(piecesList.get(i));
            }else if(nbrOfBlocksPerPieces[i]==4){
                allPiecesPerNbrOfBlocks.get(1).add(piecesList.get(i));
            }else if(nbrOfBlocksPerPieces[i]==3){
                allPiecesPerNbrOfBlocks.get(2).add(piecesList.get(i));
            }else if(nbrOfBlocksPerPieces[i]==2){
                allPiecesPerNbrOfBlocks.get(3).add(piecesList.get(i));
            }else if(nbrOfBlocksPerPieces[i]==1){
                allPiecesPerNbrOfBlocks.get(4).add(piecesList.get(i));
            }
        }

        List<Piece> newPiecesList = new ArrayList<Piece>();
        for (int i = 0; i < allPiecesPerNbrOfBlocks.size(); i++) {
            if(!allPiecesPerNbrOfBlocks.get(i).isEmpty()){
                newPiecesList.addAll(allPiecesPerNbrOfBlocks.get(i));
            }
        }
        piecesList = newPiecesList;
        System.out.println(piecesList.toString());
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
                for (int i = 0; i < piece.getTotalConfig(); i++) {
                    for (Corner pieceCorner : piece.getCorners().get(piece.getCurrentState())) {
                        //piece.printShape();
                        Move firstMove = new Move(this, piece, startingCorner);
                        if (firstMove.isAllowed(board))
                            return true;

                        for (Corner corner : cornersOnBOard) {
                            for (Vector2d emptyCorner : corner.getToCornerPositions()) { //for all the possible empty squares that would become corner contact
//                                System.out.println("in for: ");emptyCorner.printVector();
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
                    piece.rotateRight(); //try all possible rotations
                    //get all the corners for that piece.
                    if(i==piece.getNbRotation()-1) piece.rotateUpsideDown();
                }
            }
        }
//        System.out.println("Method possibleMove() piece can not be placed");
        return false; // if no piece can be placed in any of the corners

    }

    /**
     * @return ArrayList of current possible moves a player can make.
     */
    public ArrayList<Move> possibleMoveSet(Board board){
        ArrayList<Move> moveSet=new ArrayList<>();

        ArrayList<Corner> cornersOnBoard = board.getCorner(this.getStartingCorner());
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
                    }else {
                        for (Corner pieceCorner : piece.getCorners().get(piece.getCurrentState())) {
                            for (Corner corner : cornersOnBoard) {
                                for (Vector2d emptyCorner : corner.getToCornerPositions()) { //for all the possible empty squares that would become corner contact
                                    //move the piece so that it is contact with the corner with the part of it we want
                                    Vector2d positionOfPiece = emptyCorner.subtract(pieceCorner.getPosition());
                                    Move move = new Move(this, piece.clone(), positionOfPiece);
                                    if (move.isAllowed(board))
                                        moveSet.add(move);
                                }

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

    /**
     * Method used for Monte Carlo simulation
     * @param board board on which to make simulation
     * @return one random move
     */
    public Move randomPossibleMove(Board board){
        ArrayList<Corner> cornersOnBoard = board.getCorner(this.getStartingCorner());//list of corners on the board
        Random r = new Random();
        if(piecesList.size()>0) {
            int randomPiece = r.nextInt(piecesList.size()); //take a random piece
            for (int i = 0; i < piecesList.size(); i++) {
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
                        List<Corner> pieceCorner = piece.getCorners().get(piece.getCurrentState());
                        if(cornersOnBoard.size()==0) return null;
                        for (int k = 0; k < pieceCorner.size(); k++) {
                            int randomCornerBoard = r.nextInt(cornersOnBoard.size());
                            for (int l = 0; l < cornersOnBoard.size(); l++) { //TODO: ?emptycorner is the only thing that is not random
                                List<Vector2d> cornersEmpty = cornersOnBoard.get((l + randomCornerBoard) % cornersOnBoard.size()).getToCornerPositions();
                                int emptycornerrand = r.nextInt(cornersEmpty.size());
                                for (int m = 0; m < cornersEmpty.size(); m++) {//for all the possible empty squares that would become corner contact
                                    //move the piece so that it is contact with the corner with the part of it we want
                                    Vector2d positionOfPiece = cornersEmpty.get((m+emptycornerrand)%cornersEmpty.size()).subtract(pieceCorner.get(k).getPosition());
                                    Move move = new Move(this, piece, positionOfPiece);

                                    if (move.isAllowed(board, piece.getCornersContacts(positionOfPiece).get(k), cornersOnBoard.get((l+randomCornerBoard)%cornersOnBoard.size()))) {
                                        return move;
                                    }
                                }
                            }
                        }
                    }
                    piece.rotateRight();
                    if (j  == piece.getNbRotation() - 1) piece.rotateUpsideDown();
                }
            }
        }
        //System.out.println("Method possibleMove() piece can not be placed for p"+getPlayerNumber());
        return null; // if no piece can be placed in any of the corners
    }

    /**
     * same as above method but clone the piece
     * @param board board on which the move can be made
     * @param piecesList list of piece to try to fit
     * @return a random move
     */

    public Move randomPossibleMoveClone(Board board, List<Piece> piecesList){
        ArrayList<Corner> cornersOnBoard = board.getCorner(this.getStartingCorner());//list of corners on the board
        Random r = new Random();
        if(piecesList.size()>0) {
            int randomPiece = r.nextInt(piecesList.size()); //take a random piece
            for (int i = 0; i < piecesList.size(); i++) {
                Piece piece = piecesList.get((i + randomPiece) % piecesList.size()).clone(); // we don't need to CLONE it because the players are already a clone
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
                        List<Corner> pieceCorner = piece.getCorners().get(piece.getCurrentState());
                        for (int k = 0; k < pieceCorner.size(); k++) {
                            if(cornersOnBoard.size()==0) return null;
                            int randomCornerBoard = r.nextInt(cornersOnBoard.size());
                            if(this.getPiecesList().size()>12&&cornersOnBoard.size()>1){
                                int randomCornerBoard2 = r.nextInt(cornersOnBoard.size());
                                randomCornerBoard = getFarestPiece(randomCornerBoard,randomCornerBoard2,cornersOnBoard);
                            }
                            for (int l = 0; l < cornersOnBoard.size(); l++) { //emptycorner is the only thing that is not random
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
                    if (j  == piece.getNbRotation() - 1) piece.rotateUpsideDown();
                }
            }
        }
        //System.out.println("Method possibleMove() piece can not be placed for p"+getPlayerNumber());
        return null; // if no piece can be placed in any of the corners
    }

    private int getFarestPiece(int random1, int random2, List<Corner> cornersOnBoard){
        Corner corner1 = cornersOnBoard.get(random1);
        Corner corner2 = cornersOnBoard.get(random2);

        if((corner1.getPosition().get_x()+corner1.getPosition().get_y())<(corner2.getPosition().get_x()+corner2.getPosition().get_y())){
            return  random2;
        }
        return random1;
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

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean equals(Object other){
        if(other == null) return false;
        if(!(other instanceof Player)) return false;
        Player o = (Player) other;
        if(number!=o.getNumber()) return false;
        if(piecesList.size()!=o.getPiecesList().size()) return false;
        return true;
    }

    /**
     * method used to gain computational time:
     * return a list of possible moves taking care of not looking
     * at smaller piece if the number of moves exceed the numMoves
     * also set the unplayable biggest piece encountered along the way
     * @param board board on which to find the moves
     * @param numMoves limit of moves that is interesting to see
     * @return list of moves(with biggest piece in general)
     */
    public List<Move> possibleMoveSetUpdate(Board board, int numMoves) {
        unplayablePiece = new ArrayList<>();
        ArrayList<Move> moveSet=new ArrayList<>();

        ArrayList<Corner> cornersOnBoard = board.getCorner(this.getStartingCorner());
        for (Piece piecetoClone: this.getPiecesList()){
            if(moveSet.size()>numMoves && piecetoClone.getNumberOfBlocks()<=3) return moveSet;
            boolean notPlaced = true;
            if (!piecetoClone.isUsed()) {
                Piece piece = piecetoClone.clone(); // we clone it cause we rotate it and we do not want that to affect the real piece displayed
                for (int i = 0; i < piece.getTotalConfig(); i++) {
                    //get all the corners for that piece.
                    if(this.isFirstMove()) {
                        Vector2d adjust = new Vector2d(0,0);
                        if(startingCorner.get_x()!=0) adjust.set_x(piece.getShape()[0].length-1);
                        if(startingCorner.get_y()!=0) adjust.set_y(piece.getShape().length-1);
                        Move firstMove = new Move(this, piece.clone(), startingCorner.subtract(adjust));
                        if (firstMove.isAllowed(board)) {
                            moveSet.add(firstMove);
                            notPlaced=false;
                        }
                    }else
                        for (Corner pieceCorner : piece.getCorners().get(piece.getCurrentState())) {
                            for (Corner corner : cornersOnBoard) {
                                for (Vector2d emptyCorner : corner.getToCornerPositions()) { //for all the possible empty squares that would become corner contact
                                    //move the piece so that it is contact with the corner with the part of it we want
                                    Vector2d positionOfPiece= emptyCorner.subtract(pieceCorner.getPosition());
                                    Move move = new Move(this, piece.clone(), positionOfPiece);
                                    if (move.isAllowed(board)) {
                                        moveSet.add(move);
                                        notPlaced=false;
                                    }
                                }

                            }
                        }
                    piece.rotateRight();
                    if(i==piece.getNbRotation()-1) piece.rotateUpsideDown();
                }
            }
            if(notPlaced) unplayablePiece.add(piecetoClone);
        }
        return moveSet;
    }

    public List<Piece> getUnplayablePiece() {
        return unplayablePiece;
    }

    @Override
    public String toString() {
        return "Player{" +
                "number=" + number +
                ", name='" + name + '\'' +
                ", humanPlayer=" + humanPlayer +
                ", color=" + color +
                ", startingCorner=" + startingCorner +
                ", points=" + points +
                '}';
    }
}