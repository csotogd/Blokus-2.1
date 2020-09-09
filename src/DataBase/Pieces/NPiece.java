package DataBase.Pieces;

import DataBase.Piece;

/**
 *          #
 *          #
 *          ##
 *           #
 */
public class NPiece extends Piece {
    public NPiece() {
        super("N", new int[][]{{1,0},{1,0},{1,1},{0,1}}, true, 4, 8);
    }

    @Override
    public Piece getPiece() {
        return new NPiece();
    }
}
