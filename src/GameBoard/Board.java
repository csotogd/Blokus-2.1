package GameBoard;

import DataBase.Data;
import DataBase.Piece;
import Move.Move;
import Player.Player;
import Tools.Vector2d;

import java.util.ArrayList;

//TEST
public class Board{

    public int[][] boardArray;
    public  int DIMENSION = Data.getDIMENSION();
    Player[] players;

    public Board(Player[] players) {
        this.boardArray = new int[DIMENSION][DIMENSION];
        this.players = players;
        createBoard();
    }

    public Board(Player[] players, int dim) {
        DIMENSION=dim;
        this.boardArray = new int[DIMENSION][DIMENSION];
        this.players = players;
        createBoard();
    }
    public void createBoard() {
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                boardArray[i][j] = 0;
            }
        }
    }

    public boolean fitOnBoard(Piece piece,Player player){
        //TODO for phase 2 we will probably make this move an arraylist with the moves or something similar instead of a boolean
        ArrayList<Corner> cornersOnBOard = getCorner(player.getStartingCorner());

        Piece pieceCloned = piece.clone(); // we clone it cause we rotate it and we do not want that to affect the real piece displayed
        for (int i = 0; i < pieceCloned.getTotalConfig(); i++) { //TODO calculate only for permutations of a piece, for instance 1 instead of 4
            pieceCloned.rotateLeft(); //try all possible rotations
            //get all the corners for that piece.
            if (i == pieceCloned.getNbRotation() - 1) pieceCloned.rotateUpsideDown(); //TODO is done, but have to be tested
            if (player.isFirstMove()) { //if we only need to check the starting corner:
                Vector2d adjust = new Vector2d(0,0);
                if(player.getStartingCorner().get_x()!=0) adjust.set_x(piece.getShape()[0].length-1);
                if(player.getStartingCorner().get_y()!=0) adjust.set_y(piece.getShape().length-1);
                Move firstMove = new Move(player, piece, player.getStartingCorner().subtract(adjust));
                if (firstMove.isAllowed(this)) {
                    return true;
                }
            }else {
                for (Corner pieceCorner : pieceCloned.getCornersContacts(new Vector2d(0, 0))) {
                    //piece.printShape();
                    Move firstMove = new Move(player, pieceCloned, player.getStartingCorner());
                    if (firstMove.isAllowed(this))
                        return true;

                    for (Corner corner : cornersOnBOard) {
                        for (Vector2d emptyCorner : corner.getToCornerPositions()) { //for all the possible empty squares that would become corner contact
                            //System.out.println("in for: ");emptyCorner.printVector();
                            //move the piece so that it is contact with the corner with the part of it we want
                            Vector2d positionOfPiece = emptyCorner.subtract(pieceCorner.getPosition());


                            Move move = new Move(player, pieceCloned, positionOfPiece);
                            //move.print();
                            //board.print();
                            //System.out.println();
                            if (move.isAllowed(this))
                                return true; //if at least one move is allowed
                        }

                    }
                }
            }
        }
        return false;
    }

    /**
     * clone method
     * @return an exact copy of the board (the players are the SAME)
     */
    public Board clone(){
        Board result = new Board(players);
        for (int i = 0; i < boardArray.length; i++) {
            System.arraycopy(boardArray[i], 0,result.boardArray[i],0, boardArray[0].length);
        }
        return result;
    }

    public int getDIMENSION() {
        return DIMENSION;
    }

    /**
     * check if vector2d is in the boundary of the board
     * @param position
     * @return true if in, false otherwise
     */
    public boolean inBoard(Vector2d position){
        if(position.get_y()<0||position.get_x()<0||
        position.get_x()>=DIMENSION||
        position.get_y()>=DIMENSION) return false;
        return true;
    }

    /**
     * checks if a certain position in the board is occupied by a certain player
     * @param position coordinates to be checked
     * @param player occupying the position or not
     * @return true if a piece of the player occupies the position
     */
    public boolean isOccupiedBy(Vector2d position, int player){
        if(position.get_y()<0||position.get_x()<0||
                position.get_x()>=DIMENSION||
                position.get_y()>=DIMENSION) return false;
        if(boardArray[position.get_y()][position.get_x()]==player) return true;
        return false;
    }


    /**
     * (used to be STATIC METHOD defined in Corner, where the board was passed as a parameter)
     * Get all corners on the board of the player with starting position startingPosition
     * So it basically helps us know what are the possible moves for a given player in a certain state of the board
     * @param startingPosition starting position of the player
     * @return list of all possible corners for a player
     */
    public  ArrayList<Corner> getCorner( Vector2d startingPosition){
    Board board=this;
        ArrayList<Corner> corners = new ArrayList<>();
        if(board.boardArray[startingPosition.get_y()][startingPosition.get_x()]==0){//could be replace by checking the player 1st move
            Vector2d adjacent=startingPosition.add(new Vector2d(1,1));
            if(board.inBoard(adjacent)) {
                corners.add(new Corner(startingPosition,adjacent));
                return corners;
            }
            adjacent=startingPosition.add(new Vector2d(-1,-1));
            if(board.inBoard(adjacent)) {
                corners.add(new Corner(startingPosition,adjacent));
                return corners;
            }
            adjacent=startingPosition.add(new Vector2d(1,-1));
            if(board.inBoard(adjacent)) {
                corners.add(new Corner(startingPosition,adjacent));
                return corners;
            }
            adjacent=startingPosition.add(new Vector2d(-1,1));
            if(board.inBoard(adjacent)) {
                corners.add(new Corner(startingPosition,adjacent));
                return corners;
            }
        }
        boolean[][] checked = new boolean[board.getDIMENSION()][board.getDIMENSION()];
        findCorners( checked,startingPosition,corners,board.boardArray[startingPosition.get_y()][startingPosition.get_x()]);

        return corners;
    }



//Dont spend too much time trying to understand it, just know what it is used for
    /**
     * helper method to find corners and add them to the arraylist
     * @param checked position on the board that is already checked
     * @param position current coordinates
     * @param corners arraylist that contains the corners
     * @param player id of the player
     */
//TODO : not use inboard function as it takes twice as many evaluations as necessary
    private void findCorners(boolean[][] checked, Vector2d position, ArrayList<Corner> corners, int player) {
        Board board =this;
        if(board.inBoard(position) && !checked[position.get_y()][position.get_x()]){
            checked[position.get_y()][position.get_x()]=true;
            if(board.boardArray[position.get_y()][position.get_x()]==player){
                boolean top=true, down=true, left=true, right=true; //NOT occupied by own blocks ie"free"
                //if top block is inside the board limit we can check whether it is the player's block
                if(board.inBoard(position.add(new Vector2d(0,-1))) && board.boardArray[position.get_y()-1][position.get_x()]==player){
                    top=false;//top is occupied by own block, continue the recursion at that block
                    findCorners( checked, position.add(new Vector2d(0,-1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(0,1))) && board.boardArray[position.get_y()+1][position.get_x()]==player){
                    down=false;
                    findCorners( checked, position.add(new Vector2d(0,1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(-1,0))) && board.boardArray[position.get_y()][position.get_x()-1]==player){
                    left=false;
                    findCorners( checked, position.add(new Vector2d(-1,0)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(1,0))) && board.boardArray[position.get_y()][position.get_x()+1]==player){
                    right=false;
                    findCorners( checked, position.add(new Vector2d(1,0)), corners, player);
                }

                Corner current=null;//current corner being evaluated

                if(top && left
                        && board.inBoard(new Vector2d(position.get_x()-1, position.get_y()-1))
                        && board.boardArray[position.get_y()-1][position.get_x()-1]==0){
                    current = new  Corner(position,position.add(new Vector2d(-1,-1)));
                    corners.add(current);
                }
                if(top && right
                        && board.inBoard(new Vector2d(position.get_x()+1, position.get_y()-1))
                        && board.boardArray[position.get_y()-1][position.get_x()+1]==0){
                    if(current==null) {
                        current = new Corner(position,position.add(new Vector2d(1,-1)));
                        corners.add(current);
                    }
                    else {
                        current.addAdjacent(position.add(new Vector2d(1,-1)));
                    }
                }
                if(down && left
                        && board.inBoard(new Vector2d(position.get_x()-1, position.get_y()+1))
                        && board.boardArray[position.get_y()+1][position.get_x()-1]==0){
                    if(current==null) {
                        current = new Corner(position,position.add(new Vector2d(-1,1)));
                        corners.add(current);
                    }
                    else {
                        current.addAdjacent(position.add(new Vector2d(-1,1)));
                    }
                }
                if(down && right
                        && board.inBoard(new Vector2d(position.get_x()+1, position.get_y()+1))
                        && board.boardArray[position.get_y()+1][position.get_x()+1]==0){
                    if(current==null) {
                        current = new Corner(position,position.add(new Vector2d(1,1)));
                        corners.add(current);
                    }
                    else {
                        current.addAdjacent(position.add(new Vector2d(1,1)));
                    }
                }

                //now check for corners
                if(board.isOccupiedBy(position.add(new Vector2d(1,1)),player)) findCorners( checked,position.add(new Vector2d(1,1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(-1,1)),player)) findCorners(checked,position.add(new Vector2d(-1,1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(-1,-1)),player)) findCorners(checked,position.add(new Vector2d(-1,-1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(1,-1)),player)) findCorners(checked,position.add(new Vector2d(1,-1)),corners,player);

            }

        }
    }

    /**
     *
     * @return the board object, NOT a copy
     */
    public int[][] getBoardArray() {
        return boardArray;
    }

    /**
     * setter for board
     * @param boardArray
     */
    public void setBoardArray(int[][] boardArray) {
        this.boardArray = boardArray;
    }

    public int getNumberOfPlayers(){
        return players.length;
    }

    /**
     * prints the current situation of the board, used for debugging purposes
     */
    public void print(){
        System.out.println();
        for (int i = 0; i< boardArray.length; i++) {
            for (int j = 0; j < boardArray[0].length; j++) {
                System.out.print(boardArray[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
    public int getBoardDimension(){
        return this.DIMENSION;
    }
}
