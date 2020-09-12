package DataBase.Pieces;

import DataBase.Piece;

/**
 *          ##
 *           #
 *           ##
 */
public class Z5Piece extends Piece {
    public int[][] piece =  new int[][]{{1,1,0},{0,1,0},{0,1,1}};

    public Z5Piece() {
        super("Z5", new int[][]{{1,1,0},{0,1,0},{0,1,1}}, true, 2, 4);
    }

    @Override
    public Piece getPiece() {
        return new Z5Piece();
    }
}
