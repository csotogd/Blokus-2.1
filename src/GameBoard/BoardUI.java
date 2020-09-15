package GameBoard;

import DataBase.Piece;
import DataBase.Pieces.FPiece;
import Player.Player;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
        Color[] colors = {Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE};
        int colorCounter = 0;
        for (int i = 0;i<players.length;i++) {
            int pieceCounter = 0;
            for (Piece pieceLeft:players[i].getPiecesList()) {
                //TODO fix the issue that the pieces are over others
                allPieces.add(drawPiece(pieceLeft.getShape(),colors[colorCounter]),i,pieceCounter);
                pieceCounter++;
            }
            colorCounter++;
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
