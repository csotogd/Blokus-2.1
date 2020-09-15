package GameBoard;

import DataBase.Piece;
import DataBase.Pieces.FPiece;
import Player.Player;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
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
        BorderPane principal = new BorderPane();
        Background background = createBackGround();
        principal.setBackground(background);

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
        principal.setCenter(gameBoard);

        principal.setTop(pieceOfPlayer(0));
        principal.setRight(pieceOfPlayer(1));
        principal.setLeft(pieceOfPlayer(3));
        principal.setBottom(pieceOfPlayer(2));

        return principal;
    }

    public Background createBackGround(){
        Image image = new Image("https://images.hdqwalls.com/wallpapers/simple-gray-background-4k-br.jpg",800,800,false,true);

        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);

        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);

        Background background = new Background(backgroundImage);
        return background;
    }

    public FlowPane pieceOfPlayer(int playerNbr){
        FlowPane allPieces = new FlowPane();
        allPieces.getChildren().add(new Text("Player " + players[playerNbr].getNumber() + " pieces left:  "));
        int pieceCounter = 0;
        for (Piece pieceLeft:players[playerNbr].getPiecesList()) {
            //TODO fix the issue that the pieces are over others
            allPieces.getChildren().add(new Text(++pieceCounter + "  "));
            allPieces.getChildren().add(drawPiece(pieceLeft.getShape(),players[playerNbr].getColor()));
        }
        return allPieces;
    }

    public Parent drawPiece(int [][] pieceTable, Color playerColor){
        GridPane piece = new GridPane();
        for (int i = 0; i < pieceTable.length; i++) {
            for (int j = 0; j < pieceTable[i].length; j++) {

                Rectangle tile = new Rectangle(20, 20);
                if(pieceTable[i][j]==1){
                    tile.setFill(playerColor);
                    tile.setStrokeWidth(2.0);
                    tile.setStroke(Color.BLACK);
                    piece.add(new StackPane(tile),i,j);
                }else{
                    tile.setFill(Color.TRANSPARENT);
                    piece.add(new StackPane(tile),i,j);
                }

            }
        }
        return piece;
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
