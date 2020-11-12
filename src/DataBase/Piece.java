
package DataBase;

import DataBase.Pieces.*;
import GameBoard.Corner;
import Move.Move;
import Player.HumanPlayer;
import Tools.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private int[][] shape; //state of the piece
    private boolean used;
    protected boolean mirror; //possible to mirror
    protected int nbRotation; //1 to 4
    private int numberOfBlocks;
    protected int totalConfig; //total number of configuration max 8 min 1
    private String label; //name of the piece
    private int number;//can be either 1,2,3,4 depending on the player
    private double posInBoardX; //UI related
    private double posInBoardY;
    private boolean cornerComputed;
    private ArrayList<Corner> corners;
    private Vector2d position; //last time the corners was computed?
    private Piece origin;

    /**
     * CONSTRUCTOR
     * @param label name of the piece
     * @param array shape-> state of the piece
     * @param mirror possible to flip the piece
     * @param rotation number of rotation without flipping
     * @param totalConfig total number of configuration
     */
    public Piece (String label, int[][]  array, boolean mirror, int rotation, int totalConfig) {
        this.shape = array;
        this.mirror = mirror;
        this.nbRotation = rotation;
        this.totalConfig=totalConfig;
        this.label=label;
        this.used = false;
        this.numberOfBlocks=0;
        origin=this;
        for(int[] line : shape)
            for(int i: line)
                if(i!=0) this.numberOfBlocks++;
        this.position=new Vector2d(0,0);
    }

    /**
     * CONSTRUCTOR 2 used by clone method
     * @param label name of the piece
     * @param array shape-> state of the piece
     * @param mirror possible to flip the piece
     * @param rotation number of rotation without flipping
     * @param totalConfig total number of configuration
     * @param nblocks the number of block
     */
    private Piece (String label, int[][]  array, boolean mirror, int rotation, int totalConfig, int nblocks) {
        this.shape = array;
        this.mirror = mirror;
        this.nbRotation = rotation;
        this.totalConfig=totalConfig;
        this.label=label;
        this.used = false;
        this.numberOfBlocks=nblocks;
        this.cornerComputed=false;
        this.position=new Vector2d(0,0);
    }

    /**
     * Cloning method
     * @return a new piece with the same state
     */
    public Piece clone(){
        int [][]shape= new int[getShape().length][getShape()[0].length];
        for(int i=0; i<this.shape.length; i++)
            System.arraycopy(this.shape[i],0,shape[i], 0, this.shape[i].length);
        String label= this.label;
        boolean mirror= this.mirror; //this is probably not necessary
        int rotation = this.nbRotation; //this is probably not necessary

        Piece clone = new Piece(label, shape, mirror, rotation, totalConfig, this.numberOfBlocks);
        if(cornerComputed) clone.setCorners(corners);
        clone.position=this.position.clone();
        return clone;
}

    private void setCorners(List<Corner> corners) {
        if(corners.size()>0) {
            this.corners = new ArrayList<>();
            for (Corner c : corners) this.corners.add(c.clone());

            cornerComputed = true;
        }
    }


    /**
     * MAkes sure every piece has its own number
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
        if (shape != null) {
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    if (shape[i][j] != 0)
                        shape[i][j] = this.number;
                }
            }
        }
    }

    /**
     * Getter
     * @return current state of the piece
     */
    public int[][] getShape(){
        int[][] newshape= new int[shape.length][shape[0].length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                newshape[i][j]=shape[i][j];
            }
        }
        return newshape;
    }

    /**
     * Mutator, rotate to the left the current state of the piece
     */
    public void rotateLeft(){
        if(nbRotation>1){
            int[][] newState = new int[shape[0].length][shape.length];
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    newState[newState.length-1-j][i]= shape[i][j];
                }
            }
            shape=newState;
            if(cornerComputed) {
                getCornersContacts(this.position.scale(0)); // set the corner to the 0 0 coordinates
                for (Corner corner : this.corners) {
                    Vector2d newPosition = new Vector2d(corner.getPosition().get_y(), shape.length - 1 - corner.getPosition().get_x());
                    Vector2d[] newPos = new Vector2d[4];
                    for (int i = 0; i < 4; i++) newPos[(i + 3) % 4] = corner.getRelativeToCornerPositions()[i];
                    corner.setRelativeToCornerPositions(newPos, newPosition);
                }
            }
        }
    }

    /**
     * Mutator, rotate to the right the current state of the piece
     */
    public void rotateRight(){
        if(nbRotation>1){ // do this only if the piece has rotations
            int[][] newState = new int[shape[0].length][shape.length];
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    newState[j][newState[0].length-1-i]= shape[i][j];
                }
            }
            shape= newState;
            if(cornerComputed) {
                getCornersContacts(this.position.scale(0)); // set the corner to the 0 0 coordinates
                for (Corner corner : this.corners) { // with right rotation, expected coordinates should be:
                    Vector2d newPosition = new Vector2d(shape[0].length - 1 - corner.getPosition().get_y(), corner.getPosition().get_x());
                    Vector2d[] newPos = new Vector2d[4];
                    for (int i = 0; i < 4; i++) newPos[(i + 1) % 4] = corner.getRelativeToCornerPositions()[i];
                    corner.setRelativeToCornerPositions(newPos, newPosition);
                }
            }
        }
    }

    /**
     * Mutator, flip the current state of the piece (mirror)
     */
    public void rotateUpsideDown(){
        if(mirror){
            int[][] newState = new int[shape.length][shape[0].length];
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    newState[newState.length-1-i][j]= shape[i][j];
                }
            }
            shape=newState;
         //   Vector2d temp = null;
        //    if(position.get_x()!=0||position.get_y()!=0) temp= this.position.clone();
            if(cornerComputed) {
                getCornersContacts(this.position.scale(0)); // set the corner to the 0 0 coordinates
                for (Corner corner : this.corners) { //rotation
                    Vector2d newPosition = new Vector2d(corner.getPosition().get_x(), shape.length - 1 - corner.getPosition().get_y());
                    Vector2d[] newPos = new Vector2d[4];
                    for (int i = 0; i < 4; i++) newPos[3 - i] = corner.getRelativeToCornerPositions()[i];
                    corner.setRelativeToCornerPositions(newPos, newPosition);
                }
        //        if (temp != null) getCornersContacts(temp);
            }
        }
    }

    public boolean isUsed() {
        return used;
    }

    public void printShape(){
        System.out.println();
        for (int i=0; i<shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                System.out.print(shape[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public  List<int[][]> getPermutations(){
        System.out.println("using get permutations from piece class, not allowed, ask carlos");
        return null;
    }

    public int getNumberOfBlocks(){
        return this.numberOfBlocks;
    }

    public boolean isMirror() {
        return mirror;
    }

    public int getNbRotation() {
        return nbRotation;
    }

    public int getTotalConfig() {
        return totalConfig;
    }

    public String getLabel() {
        return label;
    }

    public int getNumber(){
        return  this.number;
    }

    public void setPosInBoardX(double posInBoardX) {
        this.posInBoardX = posInBoardX;
    }

    public void setPosInBoardY(double posInBoardY) {
        this.posInBoardY = posInBoardY;
    }

    public void setUsed(boolean used){
        this.used=used;
    }

    public double getPosInBoardX() {
        return posInBoardX;
    }

    public double getPosInBoardY() {
        return posInBoardY;
    }

    protected void calculateNumberOfBlocks() {
        int blocks=0;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                if(shape[i][j]==1)
                    blocks++;
            }
        }
        numberOfBlocks=blocks;
    }

    public  Piece getPiece(){
        System.out.println("calling get piece in piece class, only makes sense in subclasses");
        return null;
    }

    public Piece getOrigin() {
        return origin;
    }

    /**
     * Methods that finds coordinates of the corners of the piece and the coordinates of the corner of the piece on the board
     * @return the positions of the hypothetical corners w.r.t. coordinates of the board !!
     */
    public ArrayList<Corner> getCornersContacts(Vector2d position){
        if(!cornerComputed) { //if the corners have not been computed since last rotation, search for them
            corners = new ArrayList<>(); //contains the corners of the piece
            for (int y = 0; y < shape.length; y++) {
                for (int x = 0; x < shape[0].length; x++) {
                    if (shape[y][x] != 0) {
                        boolean top = true, right = true, down = true, left = true; //is not occupied by a block
                        Vector2d current_position = position.add(new Vector2d(x, y));
                        if (y > 0 && shape[y - 1][x] != 0) top = false; //if top outside OR top block occupied
                        if (y < shape.length - 1 && shape[y + 1][x] != 0)
                            down = false; //if down outside OR down block occupied
                        if (x > 0 && shape[y][x - 1] != 0) left = false; //if left outside OR left block occupied
                        if (x < shape[0].length - 1 && shape[y][x + 1] != 0)
                            right = false; //if right outside OR right block occupied
                        Corner current = null;
                        if (top && left) { //now adding corresponding to corner positions
                            current = new Corner(current_position, current_position.add(new Vector2d(-1, -1)),true);
                            corners.add(current);
                            current.getRelativeToCornerPositions()[0]=current.getToCornerPositions().get(0);
                        }
                        if (top && right) {
                            if (current == null) {
                                current = new Corner(current_position, current_position.add(new Vector2d(1, -1)),true);
                                corners.add(current);
                                current.getRelativeToCornerPositions()[1]=current.getToCornerPositions().get(0);
                            } else {
                                current.addAdjacent(current_position.add(new Vector2d(1, -1)));
                                current.getRelativeToCornerPositions()[1]=current.getToCornerPositions().get(current.getToCornerPositions().size()-1);
                            }
                        }
                        if (down && left) {
                            if (current == null) {
                                current = new Corner(current_position, current_position.add(new Vector2d(-1, 1)),true);
                                current.getRelativeToCornerPositions()[3]=current.getToCornerPositions().get(0);
                                corners.add(current);
                            } else {
                                current.addAdjacent(current_position.add(new Vector2d(-1, 1)));
                                current.getRelativeToCornerPositions()[3]=current.getToCornerPositions().get(current.getToCornerPositions().size()-1);
                            }
                        }
                        if (down && right) {
                            if (current == null) {
                                current = new Corner(current_position, current_position.add(new Vector2d(1, 1)),true);
                                current.getRelativeToCornerPositions()[2]=current.getToCornerPositions().get(0);
                                corners.add(current);
                            } else {
                                current.addAdjacent(current_position.add(new Vector2d(1, 1)));
                                current.getRelativeToCornerPositions()[2]=current.getToCornerPositions().get(current.getToCornerPositions().size()-1);
                            }
                        }

                    }
                }
            }
            cornerComputed=true;
            this.position=position.clone();
        }else{ //else we just modify the coordinates of the old corners
            if(!position.equals(this.position)) {
                Vector2d shift = position.subtract(this.position); //amount of coorndinates to add to get to current position
                for (Corner corner : corners) {
                    corner.setPosition(corner.getPosition().add(shift));
                    corner.toCornerAdd(shift);
                }
                this.position=position.clone();
            }
        }
        ArrayList<Corner> ret = new ArrayList<>();
        for(Corner c: corners) ret.add(c.clone());
        return ret;
    }

    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append(label+": \n\n");
        for(int[] line : shape) {
            for (int i : line) {
                s.append(i);
            }
            s.append("\n");
        }
        return s.toString();
    }

    public boolean isCornerComputed() {
        if(cornerComputed) return true;
        return false;
    }

    public void setCornerComputed(boolean cornerComputed) {
        this.cornerComputed = cornerComputed;
    }

    public static void main(String[]args) {
        int count = 1;
        //for(Piece p: PieceFactory.get().getAllPieces()) System.out.println((count++)+" "+p);

        Piece p = new L4Piece();
        System.out.println(p);


        for(Corner c: p.getCornersContacts(new Vector2d(5,5))){
            System.out.println(c.getPosition()+":");
            for(Vector2d v:c.getToCornerPositions()) System.out.print(" "+v+" ");
            System.out.println();
        }
        p.rotateRight();

        System.out.println(p);
        for(Corner c: p.getCornersContacts(new Vector2d(2,2))){
            System.out.println(c.getPosition()+":");
            for(Vector2d v:c.getToCornerPositions()) System.out.print(" "+v+" ");
            System.out.println();
        }
    }

    public void printLastC() {
        if(this.corners!=null)
        for(Corner c:this.corners){
            System.out.println(c);
        }
    }
}


