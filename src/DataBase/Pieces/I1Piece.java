package DataBase.Pieces;

import DataBase.Piece;

/**
 *          #
 */
public class I1Piece extends Piece {
    public int [][] piece = new int[][]{{1}};

    public I1Piece() {
        super("I1", new int[][]{{1}}, false, 1, 1);
    }

    @Override
    public Piece getPiece() {
        return new I1Piece();
    }
}
