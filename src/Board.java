import Tools.Vector2d;

import javax.lang.model.element.ElementVisitor;


public class Board {


    private Vector2d dimesions;
    private int[][] boxes;

    public Board(Vector2d dimesions){
        int[][]boxes = new int[dimesions.get_x()][ dimesions.get_y()];
        //Default value for the board is 0

    }

    /**
     *
     * @param piece
     * @param coordinates of the top-left corner of the piece shape
     * @return
     */
    public boolean moveISAllowed(Piece piece, Vector2d coordinates){
        /*
        *  For a move to be allowed, the following 3 conditions have to be TRUE
        * 1. every "block" of the piece should be occupying an empty board space
        * 2. There has to be a piece touching the corner of at least ONE of the blocks
        * 3. NONE of the blocks is in contact with a piece of the same player in a none- CORNER context
         *
         */
        boolean emptyspace=true;
        boolean cornerContact=true;
        boolean noDirectContact=true;





            return emptyspace && cornerContact && noDirectContact;
    }



    public Vector2d getDimesions() {
        return dimesions;
    }

    public int[][] getBoxes() {
        return boxes;
    }
}
