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
    public Color playerColor;
    public int [][] pieceTable = new int[][]{{0,1,1},{1,1,0},{0,1,0}};
    public Parent pieceUI;
    public FPiece() {
        super("F", new int[][]{{0,1,1},{1,1,0},{0,1,0}}, true, 4,8);
        pieceUI = createPieceUI();
    }

    public Parent createPieceUI(){
        GridPane piece = new GridPane();

        for (int i = 0; i < pieceTable.length; i++) {
            for (int j = 0; j < pieceTable.length; j++) {

                Rectangle tile = new Rectangle(30, 30);
                if(pieceTable[i][j]==1){
                    tile.setFill(playerColor);
                }else{
                    tile.setFill(Color.WHITE);
                }

                tile.setStrokeWidth(2.0);
                tile.setStroke(Color.BLACK);

                piece.add(new StackPane(tile), j, i);
            }
        }
        return piece;
    }

    @Override
    public Piece getPiece() {
        return new FPiece();
    }
}
