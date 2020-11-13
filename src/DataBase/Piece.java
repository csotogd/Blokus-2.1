
package DataBase;

import DataBase.Pieces.*;
import GameBoard.Corner;
import Move.Move;
import Player.HumanPlayer;
import Tools.Vector2d;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
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
    protected int current_state;

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
        for(int[] line : shape)
            for(int i: line)
                if(i!=0) this.numberOfBlocks++;
        this.position=new Vector2d(0,0);
        current_state=0;
    }

    /**
     * CONSTRUCTOR 2 used by clone method
     * @param label name of the piece
     * @param mirror possible to flip the piece
     * @param rotation number of rotation without flipping
     * @param totalConfig total number of configuration
     * @param currentS state of the piece
     */
    public Piece (String label, boolean mirror, int rotation, int totalConfig, int currentS) {
        this.shape = getPermutations().get(currentS);
        this.mirror = mirror;
        this.nbRotation = rotation;
        this.totalConfig=totalConfig;
        this.label=label;
        this.used = false;
        this.cornerComputed=false;
        this.position=new Vector2d(0,0);
        this.current_state=currentS;
        calculateNumberOfBlocks();
    }

    /**
     * Cloning method
     * @return a new piece with the same state
     */
    public abstract Piece clone();


    public abstract List<List<Corner>> getCorners();


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
            current_state--;
            if(current_state<0||current_state==nbRotation-1) current_state+=nbRotation;
            shape = getPermutations().get(current_state);
        }
    }

    /**
     * Mutator, rotate to the right the current state of the piece
     */
    public void rotateRight(){
        if(nbRotation>1){ // do this only if the piece has rotations
            current_state++;
            if(current_state>=totalConfig||current_state==nbRotation) current_state-=nbRotation;
            shape = getPermutations().get(current_state);
        }
    }

    /**
     * Mutator, flip the current state of the piece (mirror)
     */
    public void rotateUpsideDown(){
        if(mirror){
            if(nbRotation!=totalConfig) {
                if (current_state >= nbRotation) current_state -= nbRotation;
                else current_state += nbRotation;
            }else{
                if(current_state<totalConfig/2) current_state += nbRotation/2;
                else current_state -= nbRotation/2;
            }
            shape = getPermutations().get(current_state);
        }
    }

    /**
     * Mutator, rotate to the right the current state of the piece
     */
    public void rotateRi(){
        if(nbRotation>1){ // do this only if the piece has rotations
            int[][] newState = new int[shape[0].length][shape.length];
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    newState[j][newState[0].length-1-i]= shape[i][j];
                }
            }
            shape= newState;
        }
    }

    /**
     * Mutator, flip the current state of the piece (mirror)
     */
    public void rotateUp(){
        if(mirror){
            int[][] newState = new int[shape.length][shape[0].length];
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    newState[newState.length-1-i][j]= shape[i][j];
                }
            }
            shape=newState;

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

    /**
     * Methods that finds coordinates of the corners of the piece and the coordinates of the corner of the piece on the board
     * @return the positions of the hypothetical corners w.r.t. coordinates of the board !!
     */
    public ArrayList<Corner> getCornersContacts(Vector2d position){
        List<Corner> corners0 = this.getCorners().get(current_state);
        ArrayList<Corner> ret = new ArrayList<>();
        for(Corner c: corners0) {
            ret.add(c.cornerAdd(position));
        }
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


}


