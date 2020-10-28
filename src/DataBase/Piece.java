
package DataBase;

import DataBase.Pieces.*;
import GameBoard.Corner;
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
    }

    /**
     * Cloning method
     * @return a new piece with the same state
     */
    public Piece clone(){
        int [][]shape= new int[getShape().length][getShape()[0].length];
        for(int i=0; i<this.shape.length; i++)
            for(int j=0; j<this.shape[i].length; j++)
                shape[i][j]=this.shape[i][j];
        String label= this.label;
        boolean mirror= this.mirror; //this is probably not necessary
        int rotation = this.nbRotation; //this is probably not necessary

        return new Piece(label, shape, mirror, rotation, totalConfig);
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
        }
    }

    /**
     * Mutator, rotate to the right the current state of the piece
     */
    public void rotateRight(){
        if(nbRotation>1){
            int[][] newState = new int[shape[0].length][shape.length];
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    newState[j][newState[0].length-1-i]= shape[i][j];
                }
            }
            shape=newState;
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

    /** //TODO: should we move this method to move class?
     * Methods that finds coordinates of the corners of the piece and the coordinates of the corner of the piece on the board
     * @return the positions of the hypothetical corners w.r.t. coordinates of the board !!
     */
    public ArrayList<Corner> getCornersContacts(Vector2d position){
        ArrayList<Corner> result = new ArrayList<>(); //contains the corners of the piece
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[0].length; x++) {
                if(shape[y][x]!=0){
                    boolean top=true, right=true, down=true, left=true; //is not occupied by a block
                    Vector2d current_position = position.add(new Vector2d(x,y));
                    if(y>0 && shape[y-1][x]!=0) top = false; //if top outside OR top block occupied
                    if(y<shape.length-1 && shape[y+1][x]!=0) down =false; //if down outside OR down block occupied
                    if(x>0 && shape[y][x-1]!=0) left=false; //if left outside OR left block occupied
                    if(x<shape[0].length-1 && shape[y][x+1]!=0) right = false; //if right outside OR right block occupied
                    Corner current = null;
                    if(top&&left){ //now adding corresponding corner positions
                        current = new Corner(current_position,current_position.add(new Vector2d(-1,-1)));
                        result.add(current);
                    }
                    if(top&&right){
                        if(current == null){
                            current = new Corner(current_position,current_position.add(new Vector2d(1,-1)));
                            result.add(current);
                        }else{
                            current.addAdjacent(current_position.add(new Vector2d(1,-1)));
                        }
                    }
                    if(down&&left){
                        if(current==null){
                            current = new Corner(current_position, current_position.add(new Vector2d(-1,1)));
                            result.add(current);
                        }else{
                            current.addAdjacent(current_position.add(new Vector2d(-1,1)));
                        }
                    }
                    if(down&&right){
                        if(current==null){
                            current = new Corner(current_position, current_position.add(new Vector2d(1,1)));
                            result.add(current);
                        }else{
                            current.addAdjacent(current_position.add(new Vector2d(1,1)));
                        }
                    }

                }
            }
        }
        return result;
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

    public static void main(String[]args){
        int count = 1;
        //for(Piece p: PieceFactory.get().getAllPieces()) System.out.println((count++)+" "+p);

        Piece p = new WPiece();
        for(int[][] perm : p.getPermutations()){
            for (int i = 0; i < perm.length; i++) {
                for (int j = 0; j < perm[0].length; j++) {
                    System.out.print(perm[i][j]);
                }
                System.out.println();
            }
            System.out.println();
        }

        Piece p2 = new FPiece();
        for(int[][] perm : p2.getPermutations()){
            for (int i = 0; i < perm.length; i++) {
                for (int j = 0; j < perm[0].length; j++) {
                    System.out.print(perm[i][j]);
                }
                System.out.println();
            }
            System.out.println();
        }

        Piece p3 = new I1Piece();
        for(int[][] perm : p3.getPermutations()){
            for (int i = 0; i < perm.length; i++) {
                for (int j = 0; j < perm[0].length; j++) {
                    System.out.print(perm[i][j]);
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}


