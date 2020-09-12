package DataBase.Pieces;

import DataBase.Piece;

/**
 *          #
 *          ##
 *          #
 *          #
 */
public class YPiece extends Piece {
    public int[][] piece =  new int[][]{{1,0},{1,1},{1,0},{1,0}};

    public YPiece(){
        super("Y", new int[][]{{1,0},{1,1},{1,0},{1,0}}, true, 4, 8);
    }

    @Override
    public Piece getPiece() {
        return new YPiece();
    }
}
