package DataBase.Pieces;

import DataBase.Piece;

/**
 *          #####
 */
public class I5Piece extends Piece {
    public int[][] piece = new int[][]{{1,1,1,1,1}};
    public I5Piece() {
        super("I5", new int[][]{{1,1,1,1,1}}, false, 2, 2);
    }

    @Override
    public Piece getPiece() {
        return new I5Piece();
    }
}
