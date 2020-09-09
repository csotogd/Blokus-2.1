package DataBase.Pieces;

import DataBase.Piece;

/**
 *           ##
 *          ##
 *           #
 */
public class FPiece extends Piece {
    public FPiece() {
        super("F", new int[][]{{0,1,1},{1,1,0},{0,1,0}}, true, 4,8);
    }

    @Override
    public Piece getPiece() {
        return new FPiece();
    }
}
