package DataBase.Pieces;

import DataBase.Piece;

/**
 *          ##
 *          ##
 */
public class O4Piece extends Piece {
    public int[][] piece = new int[][]{{1,1},{1,1}};
    public O4Piece() {
        super("O4", new int[][]{{1,1},{1,1}}, false, 1, 1);
    }

    @Override
    public Piece getPiece() {
        return new O4Piece();
    }
}
