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
    /*
    * 1 0 0 0 0
    * 1 1 0 0 0
    * 1 0 0 0 0
    * 0 0 0 0 0
    *
    * here one of the 2 corners would be in coordinate (1,1)
    * and the list of toCorner positions would be: [(0,2),(2,2)], as they are the diagonal contacts with
    * the corner itself, also known as the places where the corner of another piece of the same colour should be
    * placed
    *
    * */

    public Corner(Vector2d position){
        this.position=position;
    }

    /**
     * CONSTRUCTOR
     * @param position position of the corner
     * @param other position of a corresponding corner
     */
    public Corner(Vector2d position, Vector2d other){
        this.position=position;
        this.toCornerPositions= new ArrayList<Vector2d>();
        toCornerPositions.add(other);
    }

    /**
     * Add another expected position for an appropriate corner
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

    /**
     * Getter
     * @return position of the corner
     */
    public Vector2d getPosition() {
        return position;
    }

    /**
     * Getter
     * @return list of position of valid corresponding corner positions
     */
    public List<Vector2d> getToCornerPositions() {
        return toCornerPositions;
    }



/*
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
    corner.getPosition().printVector();
    System.out.println("expected positions:");
    for(Vector2d otherCorner:corner.getToCornerPositions()) System.out.println(otherCorner.get_x()+" "+otherCorner.get_y());}
    }

*/

}
