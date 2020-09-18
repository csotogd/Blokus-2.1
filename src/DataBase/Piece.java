
package DataBase;

import DataBase.Pieces.*;
import GameBoard.Corner;
import Tools.Vector2d;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    private int[][] shape;
    private boolean used;
    protected boolean mirror;
    protected int nbRotation;
    private int numberOfBlocks;
    protected int totalConfig;
    private String label;
    private int number;//can be either 1,2,3,4 depending on the player

    public Piece (String label, int[][]  array, boolean mirror, int rotation, int totalConfig) {
        this.shape = array;
        this.mirror = mirror;
        this.nbRotation = rotation;
        this.totalConfig=totalConfig;
        this.label=label;
    }

    /**
     * MAkes sure every piece has its own number
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
        if (shape != null) {
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j--) {
                    if (shape[i][j] != 0)
                        shape[i][j] = this.number;
                }
            }
        }
    }

    public int[][] getShape(){
        int[][] newshape= new int[shape.length][shape[0].length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                newshape[i][j]=shape[i][j];
            }
        }
        return newshape;
    }


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

    public abstract List<int[][]> getPermutations();
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
    public void setUsed(boolean used){
        this.used=used;
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
    public abstract Piece getPiece();

    /**
     * Methods that finds coordinates of the corners of the piece and the coordinates of the corner of the piece on the board
     * @return the positions of the hypothetical corners w.r.t. coordinates of the board !!
     */
    public ArrayList<Corner> getCornersContacts(Vector2d position){
        ArrayList<Corner> result = new ArrayList<>();
        //Vector2d[] corners = new Vector2d[4]; //find the corners
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[0].length; x++) {
                if(shape[y][x]!=0){
                    boolean top=true, right=true, down=true, left=true; //is not occupied by a block
                    Vector2d current_position = position.add(new Vector2d(x,y));
                    if(y>0 && shape[y-1][x]!=0) top = false;
                    if(y<shape.length-1 && shape[y+1][x]!=0) down =false;
                    if(x>0 && shape[y][x-1]!=0) left=false;
                    if(x<shape[0].length-1 && shape[y][x+1]!=0) right = false;
                    Corner current = null;
                    if(top&&left){
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

     /*   for (int y = 0; y < shape.length; y+=shape.length-1) {
            for (int x = 0; x < shape[0].length; x++){
                if(corners[0]==null){
                    if(shape[y][x]!=0) corners[0]=new Vector2d(x,y);
                }else if(corners[1]==null){
                    if (shape[y][x]!=0 && x+1<shape[0].length && shape[y][x+1]==0) corners[1]=new Vector2d(x,y);
                }else if(corners[2]==null){
                    if(y==shape.length-1 && shape[y][x]!=0) corners[3] = new Vector2d(x,y);
                }else if(corners[3]==null){
                    if (shape[y][x]!=0 && x+1<shape[0].length && shape[y][x+1]==0) corners[3]=new Vector2d(x,y);
                }
            }
        }

        result.add(new Vector2d(corners[0].get_x()-1,corners[0].get_y()-1));
        result.add(corners[0]);
        result.add(new Vector2d(corners[1].get_x()+1,corners[1].get_y()-1));
        result.add(corners[1]);
        result.add(new Vector2d(corners[2].get_x()-1,corners[2].get_y()+1));
        result.add(corners[2]);
        result.add(new Vector2d(corners[3].get_x()+1,corners[3].get_y()+1));
        result.add(corners[3]);
      */  return result;
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


