package DataBase.Pieces;

import DataBase.Piece;

/**
 *         ##
 *         #
 */

public class V3Piece extends Piece {
    public V3Piece() {
        super("V3", new int[][]{{1,1}, {1,0}}, true, 4, 4);
    }

    @Override
    public Piece getPiece() {
        return new V3Piece();
    }
}
