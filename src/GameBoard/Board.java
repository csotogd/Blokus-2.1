package GameBoard;

import DataBase.Data;
import DataBase.Piece;
import Player.Player;
import Tools.Vector2d;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

//TEST
public class Board{

    public int[][] board;
    private final int DIMENSION = Data.getDIMENSION();
    Player[] players;

    public Board(Player[] players) {
        this.board = new int[DIMENSION][DIMENSION];
        this.players = players;
        createBoard();
    }

    public void createBoard() {
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                board[i][j] = 0;
            }
        }
    }


    public int getDIMENSION() {
        return DIMENSION;
    }

    public boolean inBoard(Vector2d position){
        if(position.get_y()<0||position.get_x()<0||
        position.get_x()>=DIMENSION||
        position.get_y()>=DIMENSION) return false;
        return true;
    }

    public boolean isOccupiedBy(Vector2d position, int player){
        if(position.get_y()<0||position.get_x()<0||
                position.get_x()>=DIMENSION||
                position.get_y()>=DIMENSION) return false;
        if(board[position.get_y()][position.get_x()]==player) return true;
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
        if(board.board[startingPosition.get_y()][startingPosition.get_x()]==0){
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
        findCorners( checked,startingPosition,corners,board.board[startingPosition.get_y()][startingPosition.get_x()]);

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
            if(board.board[position.get_y()][position.get_x()]==player){
                boolean top=true, down=true, left=true, right=true; //NOT occupied by own blocks ie"free"
                if(board.inBoard(position.add(new Vector2d(0,-1))) && board.board[position.get_y()-1][position.get_x()]==player){
                    top=false;
                    findCorners( checked, position.add(new Vector2d(0,-1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(0,1))) && board.board[position.get_y()+1][position.get_x()]==player){
                    down=false;
                    findCorners( checked, position.add(new Vector2d(0,1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(-1,0))) && board.board[position.get_y()][position.get_x()-1]==player){
                    left=false;
                    findCorners( checked, position.add(new Vector2d(-1,0)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(1,0))) && board.board[position.get_y()][position.get_x()+1]==player){
                    right=false;
                    findCorners( checked, position.add(new Vector2d(1,0)), corners, player);
                }

                Corner current=null;//current corner being evaluated

                if(top && left
                        && board.inBoard(new Vector2d(position.get_x()-1, position.get_y()-1))
                        && board.board[position.get_y()-1][position.get_x()-1]==0){
                    current = new  Corner(position,position.add(new Vector2d(-1,-1)));
                    corners.add(current);
                }
                if(top && right
                        && board.inBoard(new Vector2d(position.get_x()+1, position.get_y()-1))
                        && board.board[position.get_y()-1][position.get_x()+1]==0){
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
                        && board.board[position.get_y()+1][position.get_x()-1]==0){
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
                        && board.board[position.get_y()+1][position.get_x()+1]==0){
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
    public int[][] getBoard() {
        return board;
    }

    /**
     * setter for board
     * @param board
     */
    public void setBoard(int[][] board) {
        this.board = board;
    }

    /**
     * prints the current situation of the board, used for debugging purposes
     */
    public void print(){
        System.out.println();
        for (int i=0; i<board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
    public int getBoardDimension(){
        return this.DIMENSION;
    }
}
