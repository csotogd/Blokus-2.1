package DataBase.Pieces;

import DataBase.Piece;

public class UPiece extends Piece {
    public UPiece() {
        super("U", new int[][]{{1,0,1},{1,1,1}}, true, 4, 4);
    }

    @Override
    public Piece getPiece() {
        return new UPiece();
    }
}
