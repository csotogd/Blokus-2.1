package DataBase.Pieces;

import DataBase.Piece;

/**
 *          ###
 *           #
 *           #
 */
public class T5Piece extends Piece {
    public int[][] piece = new int[][]{{1,1,1},{0,1,0},{0,1,0}};

    public T5Piece() {
        super("T5", new int[][]{{1,1,1},{0,1,0},{0,1,0}}, true, 4, 4);
    }

    @Override
    public Piece getPiece() {
        return new T5Piece();
    }
}
