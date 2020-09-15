package DataBase.Pieces;

import DataBase.Piece;

/**
 *          ###
 */
public class I4Piece extends Piece {
    public I4Piece() {
        super("I4", new int[][]{{1,1,1,1}}, false, 2, 2);
    }

    @Override
    public Piece getPiece() {
        return new I4Piece();
    }
}
