package GameBoard;

import DataBase.Piece;
import DataBase.PieceFactory;
import DataBase.Pieces.PPiece;
import Move.Move;
import Player.HumanPlayer;
import Player.Player;
import Tools.Vector2d;

import java.util.ArrayList;
import java.util.List;


public class Corner {
    private Vector2d position; //coordinates on the board
    private List<Vector2d> toCornerPositions; // list of positions of connected corners
    private Vector2d[] relativeToCornerPositions; // length of 4, topleft, topright, downright and downleft. If doesn't exist, it's null
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
    *RelativeToCornerPositions would be {null,(0,2),(2,2), null}
    * */

    public Corner(Vector2d position){
        this.position=position;
        this.toCornerPositions= new ArrayList<Vector2d>();
    }

    /**
     * constructor for Piece class
     * @param position position of the corner
     * @param piece
     */
    public Corner(Vector2d position, Vector2d other, boolean piece){
        relativeToCornerPositions=new Vector2d[4];
        this.toCornerPositions= new ArrayList<Vector2d>();
        toCornerPositions.add(other);
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

    /**
     * change the position
     * @param newPos the new coordinates for the vector
     */
    public void setPosition(Vector2d newPos) {
        this.position = newPos;
    }

    /**
     * change the position of the corner according to shift
     * @param shift vector2d position of the piece on the board
     * @return corner positions on the board
     */
    public Corner cornerAdd(Vector2d shift) {
        Corner newCorner = new Corner(position.add(shift));
        for(Vector2d v: toCornerPositions) newCorner.addAdjacent(v.add(shift));
        return newCorner;
    }

    /**
     * get the expected position of the corresponding corner
     * @return expected position of the corresponding corner
     */
    public Vector2d[] getRelativeToCornerPositions() {
        return relativeToCornerPositions;
    }

    /**
     * used to rotate corner with relative corner position according to 1st parameter
     * @param relativeToCornerPositions topleft topright bottomright bottomleft (null if doesn't exist)
     * @param newPosition new postion of the corner (rotated)
     */
    public void setRelativeToCornerPositions(Vector2d[] relativeToCornerPositions, Vector2d newPosition) {
        this.relativeToCornerPositions = relativeToCornerPositions;
        this.position = newPosition;
        this.toCornerPositions=new ArrayList<>();
        if(this.relativeToCornerPositions[0]!=null){
            this.relativeToCornerPositions[0]=(position.add(new Vector2d(-1,-1)));
            this.toCornerPositions.add(this.relativeToCornerPositions[0]);
        }
        if(this.relativeToCornerPositions[1]!=null){
            this.relativeToCornerPositions[1]=(position.add(new Vector2d(1,-1)));
            this.toCornerPositions.add(this.relativeToCornerPositions[1]);
        }
        if(this.relativeToCornerPositions[2]!=null){
            this.relativeToCornerPositions[2]=(position.add(new Vector2d(1,1)));
            this.toCornerPositions.add(this.relativeToCornerPositions[2]);
        }
        if(this.relativeToCornerPositions[3]!=null){
            this.relativeToCornerPositions[3]=(position.add(new Vector2d(-1,1)));
            this.toCornerPositions.add(this.relativeToCornerPositions[3]);
        }
    }

    @Override
    public Corner clone(){
        Corner clone = new Corner(position.clone());
        for(Vector2d v:toCornerPositions){
            clone.addAdjacent(v.clone());
        }
        if(relativeToCornerPositions!=null){
            int count = 0;
            clone.relativeToCornerPositions = new Vector2d[4];
            for(Vector2d v: relativeToCornerPositions){
                if(v!=null) clone.relativeToCornerPositions[count]=clone.getToCornerPositions().get(toCornerPositions.indexOf(relativeToCornerPositions[count]));
                count++;
            }
        }
        return clone;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder("Position: ");
        sb.append(this.position.toString());
        sb.append("\n");
        for(Vector2d v: toCornerPositions) sb.append(v.toString()+" ");
        return sb.toString();
    }

    /**
     * copy a list of corners
     * @param original corner list to be copied
     * @return the copy
     */
    public static List<Corner> copyOf(List<Corner> original){
        ArrayList<Corner> copy = new ArrayList<>();
        for(Corner c:original) copy.add(c.clone());
        return copy;
    }


}
