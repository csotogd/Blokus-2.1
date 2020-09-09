package DataBase.Pieces;

import DataBase.Piece;

/**
 *         ##
 */

public class I2Piece extends Piece {
    public I2Piece() {
        super("I2", new int[][]{{1,1}}, false, 2, 2);
    }

    @Override
    public Piece getPiece() {
        return new I2Piece();
    }
}
