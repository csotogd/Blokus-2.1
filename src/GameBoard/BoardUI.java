package GameBoard;

import DataBase.Piece;
import Move.Move;
import Player.Player;
import Tools.Vector2d;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import java.util.Vector;

//TEST
public class BoardUI{
    public Board board;
    public Parent gameBoard;
    public Player[] players;
    private Background background;
    private final Vector2d RECTANGLE_SIZE = new Vector2d(100,280);
    private final int CELL_SIZE = 25;
    private Player actualPlayer;
    private int playerCounter;
    Pane center;
    double[] centerSize;
    FlowPane allPieces[];

    public BoardUI(Player[] players){
        this.players = players;
        allPieces = new FlowPane[players.length];
        this.playerCounter = 0;
        this.actualPlayer = this.players[playerCounter++];
        this.background = createBackGround();
        this.board = new Board(players);
        this.gameBoard = createBoard();

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
        center = new StackPane();
        center.setTranslateX(100);
        center.setTranslateY(80);

        principal.setBackground(background);
        center.getChildren().add(board);
        principal.setCenter(center);
        centerSize = new double[]{center.getWidth(), center.getHeight()};

        top.getChildren().add(pieceOfPlayer(0));
        principal.setTop(top);
        if(players.length==2){
            right.getChildren().add(rotationButtons());
            principal.setRight(right);
            bottom.getChildren().add(pieceOfPlayer(1));
            principal.setBottom(bottom);
            left.getChildren().add(playersTurn());
            principal.setLeft(left);
        }else{
            right.getChildren().add(rotationButtons());
            principal.setRight(right);
            left.getChildren().add(playersTurn());
            principal.setLeft(left);
            bottom.getChildren().add(pieceOfPlayer(2));
            principal.setBottom(bottom);
        }
        //setResizable(principal,false);setResizable(center,false);setResizable(bottom,false);setResizable(top,false);setResizable(right,false);setResizable(left,false);
        return principal;
    }

    public void setResizable(Pane pane,boolean resizable){
        if(!resizable){
            //pane.setT
        }
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
        if(players.length!=2){
            Node player4 = pieceOfPlayer(3);
            layout.getChildren().addAll(principal,text,text1,player4);
        }else {
            layout.getChildren().addAll(principal,text,text1);
        }



        return layout;
    }

    public Pane rotationButtons(){
        Rectangle principal = new Rectangle();
        principal.setFill(Color.TRANSPARENT);
        principal.setStroke(Color.WHITE);
        principal.setHeight(RECTANGLE_SIZE.get_x()*2.3f);
        principal.setWidth(RECTANGLE_SIZE.get_y()*1.5f);

        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setTranslateX(100);choiceBox.setTranslateY(-70);
        for (int i = 0; i < actualPlayer.getPiecesList().size(); i++) {
            choiceBox.getItems().add(i+1);
        }
        Button rightRotate = new Button("Right rotation");
        rightRotate.setTranslateX(70); rightRotate.setTranslateY(-20);
        //TODO fix drag after rotating a piece and also rotate the array INTEGER of the piece
        rightRotate.setOnAction(actionEvent ->  {
            int pieceNbr = (int) choiceBox.getSelectionModel().getSelectedItem();
            int isPiece = 0;
            for (Object object:allPieces[playerCounter-1].getChildren()) {
                if(object.getClass().equals(GridPane.class)){
                    isPiece++;
                    if((isPiece==pieceNbr)){
                        GridPane piece = (GridPane) object;
                        double xCenter = piece.getTranslateX() + piece.getWidth()/2;
                        double yCenter = piece.getTranslateY() + piece.getHeight()/2;
                        System.out.println(xCenter + " " + yCenter);
                        Rotate rotation = new Rotate(90);
                        rotation.setPivotX(xCenter);rotation.setPivotY(yCenter);
                        piece.getTransforms().add(rotation);
                        break;
                    }
                }
            }

        });
        Button leftRotate = new Button("Left rotation");
        leftRotate.setTranslateX(-80); leftRotate.setTranslateY(-20);
        //TODO fix drag after rotating a piece and also rotate the array INTEGER of the piece
        leftRotate.setOnAction(actionEvent ->  {
            int pieceNbr = (int) choiceBox.getSelectionModel().getSelectedItem();
            int isPiece = 0;
            for (Object object:allPieces[playerCounter-1].getChildren()) {
                if(object.getClass().equals(GridPane.class)){
                    isPiece++;
                    if((isPiece==pieceNbr)){
                        GridPane piece = (GridPane) object;
                        double xCenter = piece.getTranslateX() + piece.getWidth()/2;
                        double yCenter = piece.getTranslateY() + piece.getHeight()/2;
                        System.out.println(xCenter + " " + yCenter);
                        Rotate rotation = new Rotate(-90);
                        rotation.setPivotX(xCenter);rotation.setPivotY(yCenter);
                        piece.getTransforms().add(rotation);
                        break;
                    }
                }
            }

        });
        Text text = new Text("Rotate piece number:");
        text.setTranslateX(-50);text.setTranslateY(-70);
        text.setFont(Font.font("Verdana", 20));
        text.setFill(Color.WHITE);
        Text text1 = new Text("Move the selected piece with the arrows");
        text1.setTranslateY(30);
        text1.setFont(Font.font("Verdana", 20));
        text1.setFill(Color.WHITE);
        Button enter = new Button("ENTER");
        enter.setTranslateY(80);
        StackPane layout = new StackPane();
        if(players.length!=2){
            Node player2 = pieceOfPlayer(1);
            layout.getChildren().add(player2);
            layout.getChildren().addAll(text,text1,principal,rightRotate,leftRotate,choiceBox,enter);
        }else {
            layout.getChildren().addAll(text,text1,principal,rightRotate,leftRotate,choiceBox,enter);
        }



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
        allPieces[playerNbr] = new FlowPane();
        Text text = new Text("Player " + players[playerNbr].getNumber() + " pieces left:  ");
        text.setFont(Font.font("Verdana", 20));
        text.setFill(Color.WHITE);
        allPieces[playerNbr].getChildren().add(text);
        int pieceCounter = 0;
        for (Piece pieceLeft:players[playerNbr].getPiecesList()) {
            if(!pieceLeft.isUsed()){
                allPieces[playerNbr].getChildren().add(new Text(Integer.toString(++pieceCounter)));
                Node piece = drawPiece(pieceLeft.getShape(), players[playerNbr].getColor(),pieceLeft,allPieces[playerNbr]);
                pieceLeft.setPosInBoardX(piece.getTranslateX());
                pieceLeft.setPosInBoardY(piece.getTranslateX());
                allPieces[playerNbr].getChildren().add(piece);
                allPieces[playerNbr].getChildren().add(new Text(" "));
            }
        }
        allPieces[playerNbr].setMinSize(allPieces[playerNbr].getWidth(),allPieces[playerNbr].getHeight());
        return allPieces[playerNbr];
    }

    public Node drawPiece(int [][] pieceTable, Color playerColor,Piece pieceRoot,Pane allPieces){
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
        final double[] xPos = new double[1];
        final double[] yPos = new double[1];
        piece.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                piece.setScaleX(2);piece.setScaleY(2);
                xPos[0] = piece.getTranslateX();
                yPos[0] = piece.getTranslateY();
                event.consume();
            }
        });

        piece.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                double deltaX = event.getX() - xPos[0] ;
                double deltaY = event.getY() - yPos[0] ;
                piece.setTranslateX(piece.getTranslateX() + deltaX);
                piece.setTranslateY(piece.getTranslateY() + deltaY);
                event.consume();
            }
        });

        piece.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                double MousePosX = event.getScreenX();double MousePosY=event.getScreenY();

                Bounds bounds = board.gameBoardRep.getLayoutBounds();
                Point2D coordinates = board.gameBoardRep.localToScene(bounds.getMinX(), bounds.getMinY());

                System.out.println(coordinates.getX()+ " "+coordinates.getY());
                Vector2d position = new Vector2d((int)((MousePosX-coordinates.getX())/25),(int)((MousePosY-coordinates.getY())/25));
                System.out.println(position.get_x() + "  " + position.get_y());
                Move move = new Move(actualPlayer,pieceRoot,position);
                if(move.makeMove(board)){
                    System.out.println("piece removed");
                    allPieces.getChildren().remove(piece);
                    center.setPrefSize(centerSize[0],centerSize[1]);
                    actualPlayer = players[playerCounter++];
                }else{
                    piece.setScaleX(1);piece.setScaleY(1);
                    piece.setTranslateX(pieceRoot.getPosInBoardX());
                    piece.setTranslateY(pieceRoot.getPosInBoardY());
                }
                System.out.println(actualPlayer.getPiecesList().get(0).getPosInBoardX()+" "+actualPlayer.getPiecesList().get(0).getPosInBoardY  ());
                event.consume();
            }
        });

        return piece;
    }


}
