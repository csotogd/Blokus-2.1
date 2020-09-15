package DataBase.Pieces;

import DataBase.Piece;

/**
 *          ##
 *          ##
 *          #
 */
public class PPiece extends Piece {
    public PPiece() {
        super("P", new int[][]{{1,1},{1,1},{1,0}}, true, 4, 8);
    }

    @Override
    public Piece getPiece() {
        return new PPiece();
    }
}
