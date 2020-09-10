package DataBase.Pieces;

import DataBase.Piece;

/**
 *          ###
 */
public class I3Piece extends Piece {
    public I3Piece() {
        super("I3", new int[][]{{1,1,1}}, false, 2, 2);
    }

    @Override
    public Piece getPiece() {
        return new I3Piece();
    }
}
