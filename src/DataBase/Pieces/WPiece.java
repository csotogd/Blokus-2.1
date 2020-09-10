package DataBase.Pieces;

import DataBase.Piece;

/**
 *          #
 *          ##
 *           ##
 */
public class WPiece extends Piece {
    public WPiece() {
        super("W", new int[][]{{1,0,0},{1,1,0},{0,1,1}}, true, 4, 4);
    }

    @Override
    public Piece getPiece() {
        return new WPiece();
    }
}
