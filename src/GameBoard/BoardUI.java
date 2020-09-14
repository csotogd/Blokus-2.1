package GameBoard;

import DataBase.Piece;
import DataBase.Pieces.FPiece;
import Player.Player;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

//TEST
public class BoardUI{
    private final int BOARD_SIZE = 20;
    public Board board;
    public Parent gameBoardRep;
    public Player[] players;

    public BoardUI(int nbrPlayer,Player[] players){
        this.board = new Board(nbrPlayer);
        this.players = players;
        this.gameBoardRep=createBoard();
    }

    public Parent createBoard() {
        GridPane principal = new GridPane();

        GridPane gameBoard = new GridPane();

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

        principal.add(gameBoard,0,0);
        principal.add(pieceOfPlayer(),1,0);

        return principal;
    }

    public GridPane pieceOfPlayer(){
        GridPane allPieces = new GridPane();
        for (int i = 0;i<players.length;i++) {
            for (Piece pieceLeft:players[i].getPiecesList()) {
                allPieces.add(drawPiece(pieceLeft.getShape(),Color.RED),0,0);
            }
        }
        return allPieces;
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
        }else if(board.board[col][row]==2){
            return Color.RED;
        }else if(board.board[col][row]==3){
            return Color.BLUE;
        }else if(board.board[col][row]==4){
            return Color.GREEN;
        }
        return null;
    }
}
