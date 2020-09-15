package DataBase.Pieces;

import DataBase.Piece;

/**
 *          ###
 *           #
 *          NB: mirror can be possible but not at every rotation
 */
public class T4Piece extends Piece {
    public T4Piece() {
        super("T4", new int[][]{{1,1,1},{0,1,0}}, true, 4, 4);
    }

    @Override
    public Piece getPiece() {
        return new T4Piece();
    }
}
