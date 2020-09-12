package DataBase.Pieces;

import DataBase.Piece;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *           ##
 *          ##
 *           #
 */
public class FPiece extends Piece {
    public int [][] piece = new int[][]{{0,1,1},{1,1,0},{0,1,0}};
    public FPiece() {
        super("F", new int[][]{{0,1,1},{1,1,0},{0,1,0}}, true, 4,8);
    }


    @Override
    public Piece getPiece() {
        return new FPiece();
    }
}
