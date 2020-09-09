package DataBase.Pieces;

import DataBase.Piece;

/**
 *          #
 *          #
 *          ###
 */
public class V5Piece extends Piece {
    public V5Piece() {
        super("V5", new int[][]{{1,0,0},{1,0,0},{1,1,1}}, true,4, 4);
    }

    @Override
    public Piece getPiece() {
        return new V5Piece();
    }
}
