import Tools.Vector2d;

import javax.lang.model.element.ElementVisitor;


public class Board {


    private Vector2d dimesions;
    private int[][] boxes;

    public Board(Vector2d dimesions){
        int[][]boxes = new int[dimesions.get_x()][ dimesions.get_y()];
        //Default value for the board is 0

    }

    public Vector2d getDimesions() {
        return dimesions;
    }

    public int[][] getBoxes() {
        return boxes;
    }
}
