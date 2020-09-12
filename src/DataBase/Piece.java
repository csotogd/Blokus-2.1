
package DataBase;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    private int[][] shape;
    private boolean used;
    private static List<int[][]> permutations=null;
    private boolean mirror;
    private int nbRotation;
    private int totalConfig;
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

    public List<int[][]> getPermutations() {
        if(permutations==null){
            permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<nbRotation){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return permutations;
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

    public abstract Piece getPiece();
}


