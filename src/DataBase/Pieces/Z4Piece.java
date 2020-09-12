package DataBase.Pieces;

import DataBase.Piece;

/**
 *          ##
 *           ##
 */
public class Z4Piece extends Piece {
    public int[][] piece =  new int[][]{{1,1,0},{0,1,1}};

    public Z4Piece() {
        super("Z4", new int[][]{{1,1,0},{0,1,1}}, true, 2, 4);
    }

    @Override
    public Piece getPiece() {
        return new Z4Piece();
    }
}
