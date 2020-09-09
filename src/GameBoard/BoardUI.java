package GameBoard;

import GameBoard.Board;
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

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {

                Rectangle tile = new Rectangle(30, 30);
                tile.setFill(paintColor(i,j));
                tile.setStroke(Color.BLACK);

                gameBoard.add(new StackPane(tile), j, i);

                int finalI = i;
                int finalJ = j;
                tile.setOnMouseClicked(event -> drawCase(tile, finalI, finalJ));
            }
        }
        return gameBoard;
    }

    public void drawCase(Rectangle tile,int col, int row) {
        if(board.caseUsed(col,row)){
            tile.setFill(Color.RED);
        }else{
            tile.setFill(Color.GREEN);
        }

    }

    public  Color paintColor(int col, int row){
        if(board.board[col][row]==0){
            return Color.WHITE;
        }else if(board.board[col][row]==1){
            return Color.BLACK;
        }
        return null;
    }
}
