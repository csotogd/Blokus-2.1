package DataBase.Pieces;

import DataBase.Piece;

public class XPiece extends Piece {
    public XPiece() {
        super("X", new int[][]{{0,1,0},{1,1,1},{0,1,0}}, false, 1, 1);
    }

    @Override
    public Piece getPiece() {
        return new XPiece();
    }
}
