package GameBoard;

import DataBase.Piece;
import Tools.Vector2d;

public class Cell {
    private Vector2d positionInPiece;
    private Vector2d positionInBoard;
    private Piece piece;

    public Cell(Vector2d positionInPiece,Vector2d positionInBoard,Piece piece){
        this.positionInPiece = positionInPiece;
        this.positionInBoard = positionInBoard;
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }
}
