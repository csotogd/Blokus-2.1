package GameBoard;

import DataBase.Piece;
import Player.Player;
import Tools.Vector2d;
import javafx.geometry.Pos;
import javafx.scene.Parent;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

//TEST
public class BoardUI{
    private final int BOARD_SIZE = 20;
    public Board board;
    public Parent gameBoardRep;
    public Player[] players;
    private Background background;
    private final Vector2d RECTANGLE_SIZE = new Vector2d(100,280);
    private final int CELL_SIZE = 25;
    private Player actualPlayer;

    public BoardUI(int nbrPlayer,Player[] players){
        this.actualPlayer = players[0];
        this.board = new Board(nbrPlayer);
        this.players = players;

        this.background = createBackGround();
        this.gameBoardRep=createBoard();

    }

    public Parent createBoard() {
        BorderPane principal = new BorderPane();
        StackPane left = new StackPane();
        left.setTranslateY(100);
        StackPane right = new StackPane();
        right.setTranslateY(100);
        StackPane top = new StackPane();
        top.setTranslateX(280);
        StackPane bottom = new StackPane();
        bottom.setTranslateX(280);
        bottom.setTranslateY(-30);
        StackPane center = new StackPane();
        center.setTranslateX(100);
        center.setTranslateY(80);

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
        center.getChildren().add(gameBoard);
        principal.setCenter(center);
        top.getChildren().add(pieceOfPlayer(0));
        principal.setTop(top);
        if(players.length==2){
            right.getChildren().addAll(rotationButtons());
            principal.setRight(right);
            bottom.getChildren().addAll(pieceOfPlayer(1));
            principal.setBottom(bottom);
            left.getChildren().addAll(playersTurn());
            principal.setLeft(left);
        }else{
            right.getChildren().addAll(pieceOfPlayer(1),rotationButtons());
            principal.setRight(right);
            left.getChildren().addAll(pieceOfPlayer(3),playersTurn());
            principal.setLeft(left);
            bottom.getChildren().add(pieceOfPlayer(2));
            principal.setBottom(bottom);
        }



        return principal;
    }

    public Pane playersTurn(){
        Rectangle principal = new Rectangle();
        principal.setFill(Color.TRANSPARENT);
        principal.setStroke(Color.WHITE);
        principal.setHeight(RECTANGLE_SIZE.get_x());
        principal.setWidth(RECTANGLE_SIZE.get_y());
        principal.setTranslateX(-50);
        Text text = new Text("Turn of player:");
        text.setTranslateY(-30); text.setTranslateX(-50);
        text.setFont(Font.font("Verdana", 20));
        text.setFill(Color.WHITE);
        Text text1 = new Text(actualPlayer.getName());
        text1.setTranslateY(15);text1.setTranslateX(-50);
        text1.setFont(Font.font("Verdana", 30));
        text1.setFill(actualPlayer.getColor());
        StackPane layout = new StackPane();
        layout.getChildren().addAll(principal,text,text1);


        return layout;
    }

    public Pane rotationButtons(){
        Rectangle principal = new Rectangle();
        principal.setFill(Color.TRANSPARENT);
        principal.setStroke(Color.WHITE);
        principal.setHeight(RECTANGLE_SIZE.get_x()*2.3f);
        principal.setWidth(RECTANGLE_SIZE.get_y()*1.5f);

        //TODO buttons error not understood
        Button rightRotate = new Button("Right rotation");
        rightRotate.setTranslateX(70); rightRotate.setTranslateY(-20);
        Button leftRotate = new Button("Left rotation");
        leftRotate.setTranslateX(-80); leftRotate.setTranslateY(-20);
        Text text = new Text("Rotate piece number:");
        text.setTranslateX(-50);text.setTranslateY(-70);
        text.setFont(Font.font("Verdana", 20));
        text.setFill(Color.WHITE);
        Text text1 = new Text("Move the selected piece with the arrows");
        text1.setTranslateY(30);
        text1.setFont(Font.font("Verdana", 20));
        text1.setFill(Color.WHITE);
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setTranslateX(100);choiceBox.setTranslateY(-70);
        for (int i = 0; i < actualPlayer.getPiecesList().size(); i++) {
            choiceBox.getItems().add(i+1);
        }
        Button enter = new Button("ENTER");
        enter.setTranslateY(80);
        StackPane layout = new StackPane();
        layout.getChildren().addAll(text,text1,principal,rightRotate,leftRotate,choiceBox,enter);

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
            allPieces.getChildren().add(new Text(Integer.toString(++pieceCounter)));
            allPieces.getChildren().add(drawPiece(pieceLeft.getShape(),players[playerNbr].getColor()));
            allPieces.getChildren().add(new Text(" "));
        }
        return allPieces;
    }

    public Parent drawPiece(int [][] pieceTable, Color playerColor){
        GridPane piece = new GridPane();
        for (int i = 0; i < pieceTable.length; i++) {
            for (int j = 0; j < pieceTable[i].length; j++) {
                Rectangle tile = new Rectangle(CELL_SIZE/2, CELL_SIZE/2);
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
