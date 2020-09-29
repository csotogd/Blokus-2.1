package GameBoard;

import DataBase.Piece;
import DataBase.PieceFactory;
import Move.Move;
import Player.HumanPlayer;
import Tools.Vector2d;

import java.util.ArrayList;
import java.util.List;


public class Corner {
    private Vector2d position; //coordinates on the board
    private List<Vector2d> toCornerPositions; // list of positions of connected corners

    public Corner(Vector2d position){
        this.position=position;
    }


    public Corner(Vector2d position, Vector2d other){
        this.position=position;
        this.toCornerPositions= new ArrayList<Vector2d>();
        toCornerPositions.add(other);
    }

    /**
     * add another expected position for an appropriate corner
     * @param adjacent position of expected corner
     */
    public void addAdjacent(Vector2d adjacent){
        toCornerPositions.add(adjacent);
    }

    /**
     * Check if two corners are touching each others
     * @param otherCorner the other corner to be evaluated
     * @return true if the two corner are touching diagonally
     */
    public boolean isCompatible(Corner otherCorner){
        for(Vector2d c : toCornerPositions){
            if(c.get_x()==otherCorner.getPosition().get_x()&& c.get_y()==otherCorner.getPosition().get_y()){
                for(Vector2d v:otherCorner.getToCornerPositions()){
                    if(v.get_x()==position.get_x() && v.get_y() == position.get_y()) return true;
                }
            }
        }
        return false;
    }

    public Vector2d getPosition() {
        return position;
    }

    public List<Vector2d> getToCornerPositions() {
        return toCornerPositions;
    }

    /**
     * STATIC METHOD, SHOULD BE CHANGED TO NON STATIC WHEN WE FIND IN WHICH CLASS TO PUT IT IN
     * Get all corners on the board of the player with starting position startingPosition
     * @param board game board
     * @param startingPosition starting position of the player
     * @return
     */
    public static ArrayList<Corner> getCorner(Board board, Vector2d startingPosition){
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
        boolean[][] checked = new boolean[board.getDIMENSION().get_y()][board.getDIMENSION().get_x()];
        findCorners(board, checked,startingPosition,corners,board.board[startingPosition.get_y()][startingPosition.get_x()]);

        return corners;
    }

    /**
     * helper method to find corners and add them to the arraylist
     * @param board the game board
     * @param checked position on the board that is already checked
     * @param position current coordinates
     * @param corners arraylist that contains the corners
     * @param player id of the player
     */
//TODO : not use inboard function as it takes twice as many evaluations as necessary
    private static void findCorners(Board board, boolean[][] checked, Vector2d position, ArrayList<Corner> corners, int player) {
        if(board.inBoard(position) && !checked[position.get_y()][position.get_x()]){
            checked[position.get_y()][position.get_x()]=true;
            if(board.board[position.get_y()][position.get_x()]==player){
                boolean top=true, down=true, left=true, right=true; //NOT occupied by own blocks ie"free"
                if(board.inBoard(position.add(new Vector2d(0,-1))) && board.board[position.get_y()-1][position.get_x()]==player){
                    top=false;
                    findCorners(board, checked, position.add(new Vector2d(0,-1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(0,1))) && board.board[position.get_y()+1][position.get_x()]==player){
                    down=false;
                    findCorners(board, checked, position.add(new Vector2d(0,1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(-1,0))) && board.board[position.get_y()][position.get_x()-1]==player){
                    left=false;
                    findCorners(board, checked, position.add(new Vector2d(-1,0)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(1,0))) && board.board[position.get_y()][position.get_x()+1]==player){
                    right=false;
                    findCorners(board, checked, position.add(new Vector2d(1,0)), corners, player);
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
                if(board.isOccupiedBy(position.add(new Vector2d(1,1)),player)) findCorners(board, checked,position.add(new Vector2d(1,1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(-1,1)),player)) findCorners(board, checked,position.add(new Vector2d(-1,1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(-1,-1)),player)) findCorners(board, checked,position.add(new Vector2d(-1,-1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(1,-1)),player)) findCorners(board, checked,position.add(new Vector2d(1,-1)),corners,player);

            }

        }
    }

    public static void main(String[] args){
        Board board = new Board(new int[][]{
                {1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}});



ArrayList <Corner> corners= Corner.getCorner(board, new Vector2d(0,0));
    for(Corner corner : corners){
    corner.getPosition().printVector();}
    }

}
