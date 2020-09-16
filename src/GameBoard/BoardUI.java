package GameBoard;

import DataBase.Piece;
import DataBase.Pieces.FPiece;
import Player.Player;
import Tools.Vector2d;
import javafx.geometry.Pos;
import javafx.scene.Parent;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Vector;

//TEST
public class BoardUI{
    private final int BOARD_SIZE = 20;
    public Board board;
    public Parent gameBoardRep;
    public Player[] players;
    private Background background;
    private final Vector2d RECTANGLE_SIZE = new Vector2d(100,280);
    private final int CELL_SIZE = 30;

    public BoardUI(int nbrPlayer,Player[] players){
        this.board = new Board(nbrPlayer);
        this.players = players;

        this.background = createBackGround();
        this.gameBoardRep=createBoard();

    }

    public Parent createBoard() {
        BorderPane principal = new BorderPane();
        StackPane left = new StackPane();
        left.setTranslateY(350);
        StackPane right = new StackPane();
        right.setTranslateY(350);
        StackPane top = new StackPane();
        top.setTranslateX(400);
        StackPane bottom = new StackPane();
        bottom.setTranslateX(400);

        principal.setBackground(background);

        GridPane gameBoard = new GridPane();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {

                Rectangle tile = new Rectangle(CELL_SIZE, CELL_SIZE);
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
        top.getChildren().add(pieceOfPlayer(0));
        principal.setTop(top);
        right.getChildren().add(pieceOfPlayer(1));
        principal.setRight(right);
        left.getChildren().add(pieceOfPlayer(3));
        principal.setLeft(left);
        bottom.getChildren().add(pieceOfPlayer(2));
        principal.setBottom(bottom);

        principal.getChildren().addAll(playersTurn());
        //principal.getChildren().addAll(playersTurn(),rotationButtons());
        return principal;
    }

    public Pane playersTurn(){
        Rectangle principal = new Rectangle();
        principal.setFill(Color.TRANSPARENT);
        principal.setStroke(Color.WHITE);
        principal.setHeight(RECTANGLE_SIZE.get_x());
        principal.setWidth(RECTANGLE_SIZE.get_y());
        Text text = new Text("Turn of player : ");
        text.setFont(Font.font("Verdana", 20));
        text.setFill(Color.WHITE);
        StackPane layout = new StackPane();
        layout.getChildren().addAll(principal,text);
        layout.setAlignment(principal, Pos.TOP_LEFT);
        layout.setAlignment(text, Pos.TOP_LEFT);


        return layout;
    }

    public Pane rotationButtons(){
        Rectangle principal = new Rectangle();
        principal.setFill(Color.TRANSPARENT);
        principal.setStroke(Color.WHITE);
        principal.setHeight(RECTANGLE_SIZE.get_x());
        principal.setWidth(RECTANGLE_SIZE.get_y());

        //TODO buttons error not understood
        Button rightRotate = new Button("Right rotation");
        Button leftRotate = new Button("Left rotation");
        StackPane layout = new StackPane();
        layout.getChildren().addAll(principal,rightRotate,leftRotate);
        layout.setAlignment(principal, Pos.BOTTOM_RIGHT);
        layout.setAlignment(rightRotate, Pos.BOTTOM_RIGHT);
        layout.setAlignment(leftRotate, Pos.BOTTOM_RIGHT);


        return layout;
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
        Text text = new Text("Player " + players[playerNbr].getNumber() + " pieces left:  ");
        text.setFont(Font.font("Verdana", 20));
        text.setFill(Color.WHITE);
        allPieces.getChildren().add(text);
        int pieceCounter = 0;
        for (Piece pieceLeft:players[playerNbr].getPiecesList()) {
            allPieces.getChildren().add(new Text(++pieceCounter + "  "));
            allPieces.getChildren().add(drawPiece(pieceLeft.getShape(),players[playerNbr].getColor()));
            allPieces.getChildren().add(new Text("\t"));
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
