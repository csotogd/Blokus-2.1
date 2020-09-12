package GameBoard;

import DataBase.Pieces.FPiece;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

//TEST
public class BoardUI{
    private final int BOARD_SIZE = 20;
    public Board board;
    public Parent gameBoardRep;

    public BoardUI(int nbrPlayer){
        this.board = new Board(nbrPlayer);
        this.gameBoardRep=createBoard();
    }

    public Parent createBoard() {

        GridPane gameBoard = new GridPane();
        FPiece fPiece = new FPiece();
        Parent pieceTest = drawPiece(fPiece.piece,Color.RED);

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {

                Rectangle tile = new Rectangle(30, 30);
                tile.setFill(paintColor(i,j));
                tile.setStrokeWidth(2.0);
                tile.setStroke(Color.BLACK);

                gameBoard.add(new StackPane(tile), j, i);

                int finalI = i;
                int finalJ = j;
                tile.setOnMouseClicked(event -> drawCase(tile,finalI, finalJ));

            }
        }
        gameBoard.add(pieceTest,50,50);

        return gameBoard;
    }

    public void drawCase(Rectangle tile,int col, int row) {
        if(board.caseUsed(col,row)){
            tile.setStroke(Color.RED);
        }else{
            tile.setStroke(Color.GREEN);
        }

    }

    public Parent drawPiece(int [][] pieceTable, Color playerColor){
        GridPane piece = new GridPane();

        for (int i = 0; i < pieceTable.length; i++) {
            for (int j = 0; j < pieceTable.length; j++) {

                Rectangle tile = new Rectangle(30, 30);
                if(pieceTable[i][j]==1){
                    tile.setFill(playerColor);
                    tile.setStrokeWidth(2.0);
                    tile.setStroke(Color.BLACK);
                    piece.add(new StackPane(tile), j, i);
                }

            }
        }
        return piece;
    }

    public  Color paintColor(int col, int row){
        if(board.board[col][row]==0){
            return Color.WHITE;
        }else if(board.board[col][row]==1){
            return Color.YELLOW;
        }else if(board.board[col][row]==1){
            return Color.RED;
        }else if(board.board[col][row]==1){
            return Color.BLUE;
        }else if(board.board[col][row]==1){
            return Color.GREEN;
        }
        return null;
    }
}
