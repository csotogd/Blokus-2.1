package DataBase.Pieces;

import DataBase.Piece;

/**
 *          #
 *          #
 *          ##
 */
public class L4Piece extends Piece {
    public int[][] piece = new int[][]{{1,0},{1,0},{1,1}};
    public L4Piece() {
        super("L4", new int[][]{{1,0},{1,0},{1,1}}, true, 4, 8);
    }

    @Override
    public Piece getPiece() {
        return new L4Piece();
    }
}
