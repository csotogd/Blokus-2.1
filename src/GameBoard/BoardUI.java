package GameBoard;

import DataBase.Data;
import DataBase.Piece;
import Game.Game;
import Move.Move;
import Player.Player;
import Tools.Vector2d;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
/*
* It´s a ui class but many logic operations happen here too, take a look
*
* */

//TEST
public class BoardUI{
    public Pane principal;
    public GridPane gameBoardRep;
    private final int DIMENSION = Data.getDIMENSION();
    private Game game;

    public Parent gameBoard;
    public Player[] players; //Initialize in game object
    private Background background;
    private final Vector2d RECTANGLE_SIZE = new Vector2d(100,280);
    private final int CELL_SIZE = 25;


    Pane center;
    Pane right;
    Pane bottom;
    Pane left;
    Pane top;
    public FlowPane allPieces[];
    int actualSelectedPieceNbr;
    public Text turnOfPlayerText;
    public boolean beginning = true;

    public Button rightRotate;
    public Button flip;
    public Button leftRotate;


    /**
     *
     */
    public BoardUI(Game game){
        this.game = game;
        players = game.getPlayers();
        allPieces = new FlowPane[players.length];
        this.background = Data.createBackGround();
        this.principal = new Pane();
        paint();
        beginning = false;

        this.gameBoard = createBoard();
        makePiecesOpaque();
    }

    public void paint() {
        if(!beginning){
            makePiecesOpaque();
            turnOfPlayerText.setText(game.getActualPlayer().getName());
            turnOfPlayerText.setFill(game.getActualPlayer().getColor());
            refreshPieces();
        }

        //Clear previous cells
        principal.getChildren().clear();
        gameBoardRep = new GridPane();

        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {

                Rectangle tile = new Rectangle(CELL_SIZE, CELL_SIZE);
                tile.setFill(paintColor(i,j));
                tile.setStrokeWidth(2.0);
                tile.setStroke(Color.BLACK);

                Text text = new Text();
                text.setScaleX(0.8);
                text.setScaleY(0.8);

                GridPane.setRowIndex(tile, i);
                GridPane.setColumnIndex(tile, j);
                GridPane.setRowIndex(text, i);
                GridPane.setColumnIndex(text, j);

                if (players.length==2){
                    if(i==0&&j==0){
                        text.setText("HERE");
                        text.setFill(Color.RED);
                    }else if(i==DIMENSION-1&&j==DIMENSION-1){
                        text.setText("HERE");
                        text.setFill(Color.YELLOW);
                    }
                }else if(players.length==4){
                    if(i==0&&j==0){
                        text.setText("HERE");
                        text.setFill(Color.RED);
                    }else if(i==DIMENSION-1&&j==DIMENSION-1){
                        text.setText("HERE");
                        text.setFill(Color.GREEN);
                    }else if(i==DIMENSION-1&&j==0){
                        text.setText("HERE");
                        text.setFill(Color.BLUE);
                    }else if(i==0&&j==DIMENSION-1){
                        text.setText("HERE");
                        text.setFill(Color.YELLOW);
                    }
                }

                gameBoardRep.getChildren().addAll(tile,text);
            }
        }

        principal.getChildren().add(gameBoardRep);

    }

    public  Color paintColor(int col, int row){
        if(game.board.boardArray[col][row]==0){
            return Color.WHITE;
        }else if(game.board.boardArray[col][row]==1){
            return Color.RED;
        }else if(game.board.boardArray[col][row]==2){
            return Color.YELLOW;
        }else if(game.board.boardArray[col][row]==3){
            return Color.GREEN;
        }else if(game.board.boardArray[col][row]==4){
            return Color.BLUE;
        }
        return null;
    }

    /**
     * method that makes each pieces opaque except the actual chosen piece
     */
    public void makePiecesOpaque(){

        for (Player player : players) {
            if(player.isHumanPlayer()){
                int isPiece = 0;
                for (Object object:allPieces[player.getNumber()-1].getChildren()) {
                    if(object.getClass().equals(GridPane.class)){
                        isPiece++;
                        if (isPiece!=actualSelectedPieceNbr&&game.getActualPlayer().equals(player)){
                            GridPane piece = (GridPane) object;
                            piece.setOpacity(0.3);
                            //piece.setDisable(true);
                        }else if(isPiece==actualSelectedPieceNbr&&game.getActualPlayer().equals(player)){
                            GridPane piece = (GridPane) object;
                            piece.setOpacity(1);
                            //piece.setDisable(false);
                        }else if(!game.getActualPlayer().equals(player)){
                            GridPane piece = (GridPane) object;
                            piece.setOpacity(0.3);
                            piece.setDisable(true);
                        }

                    }
                }
            }else{
                for (Object object:allPieces[player.getNumber()-1].getChildren()) {
                    if(object.getClass().equals(GridPane.class)){
                        GridPane piece = (GridPane) object;
                        piece.setOpacity(0.3);
                        piece.setDisable(true);
                    }
                }
            }
        }
    }

    /**
     * method used to update the pieces of each player
     */
    public void refreshPieces(){
        top.getChildren().clear();
        top.getChildren().add(pieceOfPlayer(0));
        if(players.length==2){
            right.getChildren().clear();
            right.getChildren().add(rotationButtons());
            bottom.getChildren().clear();
            bottom.getChildren().add(pieceOfPlayer(1));
            left.getChildren().clear();
            left.getChildren().add(playersTurn());
        }else{
            right.getChildren().clear();
            right.getChildren().add(rotationButtons());
            left.getChildren().clear();
            left.getChildren().add(playersTurn());
            bottom.getChildren().clear();
            bottom.getChildren().add(pieceOfPlayer(2));
        }
        makePiecesOpaque();
    }

    /**
     * method used to create and refresh the entire board included the central game board, each left piece of each player, the info
     * of actual player turn and the info to rotate and flip the chosen piece
     * @return the node of the board
     */
    public Parent createBoard() {
        BorderPane principal = new BorderPane();
        left = new StackPane();
        left.setTranslateY(100);
        right = new StackPane();
        right.setTranslateY(100);
        top = new StackPane();
        top.setTranslateX(280);
        bottom = new StackPane();
        bottom.setTranslateX(280);
        bottom.setTranslateY(-30);
        center = new StackPane();
        center.setTranslateX(100);
        center.setTranslateY(80);

        principal.setBackground(background);
        center.getChildren().add(this.principal);
        principal.setCenter(center);

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

    /**
     * method used to create and refresh the actual player turn after each move IN THE UI!!!!!
     * @return the node of the info of the player turn
     */
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
        turnOfPlayerText = new Text(game.getActualPlayer().getName());
        turnOfPlayerText.setTranslateY(15);turnOfPlayerText.setTranslateX(-50);
        turnOfPlayerText.setFont(Font.font("Verdana", 30));
        turnOfPlayerText.setFill(game.getActualPlayer().getColor());
        StackPane layout = new StackPane();
        if(players.length!=2){
            Node player4 = pieceOfPlayer(3);
            layout.getChildren().addAll(principal,text,turnOfPlayerText,player4);
        }else {
            layout.getChildren().addAll(principal,text,turnOfPlayerText);
        }
        return layout;
    }

    /**
     * method used to create and refresh the rotation and flip button of the chosen piece and also contains the
     * action listener of the flip and left, right rotation
     * @return node of the rotation and flip button of the chosen piece
     */
    public Pane rotationButtons(){
        Rectangle principal = new Rectangle();
        principal.setFill(Color.TRANSPARENT);
        principal.setStroke(Color.WHITE);
        principal.setHeight(RECTANGLE_SIZE.get_x()*2.3f);
        principal.setWidth(RECTANGLE_SIZE.get_y()*1.5f);

        actualSelectedPieceNbr = 1;
        rightRotate = new Button("Right rotation");
        rightRotate.setTranslateX(105); rightRotate.setTranslateY(-20);
        //TODO fix drag after rotating a piece and also rotate the array INTEGER of the piece
        rightRotate.setOnAction(actionEvent ->  {
            int pieceNbr = actualSelectedPieceNbr;
            Piece piece = game.getActualPlayer().getPiecesList().get(pieceNbr-1);
            piece.rotateRight();
            int isPiece = 0;
            for (Object object:allPieces[game.getActualPlayer().getNumber()-1].getChildren()) {
                if(object.getClass().equals(GridPane.class)){
                    isPiece++;
                    if((isPiece==pieceNbr)){
                        int index = allPieces[game.getActualPlayer().getNumber()-1].getChildren().indexOf(object);
                        allPieces[game.getActualPlayer().getNumber()-1].getChildren().remove(index);
                        allPieces[game.getActualPlayer().getNumber()-1].getChildren().add(index,drawPiece(game.getActualPlayer().getColor(),piece,allPieces[game.getActualPlayer().getNumber()-1]));
                        break;
                    }
                }
            }
        });

        flip = new Button("Flip");
        //TODO finish the flip rotation
        flip.setTranslateX(0); flip.setTranslateY(-20);
        flip.setOnAction(actionEvent ->  {
            int pieceNbr = actualSelectedPieceNbr;
            Piece piece = game.getActualPlayer().getPiecesList().get(pieceNbr-1);
            piece.rotateUpsideDown();
            int isPiece = 0;
            for (Object object:allPieces[game.getActualPlayer().getNumber()-1].getChildren()) {
                if(object.getClass().equals(GridPane.class)){
                    isPiece++;
                    if((isPiece==pieceNbr)){
                        int index = allPieces[game.getActualPlayer().getNumber()-1].getChildren().indexOf(object);
                        allPieces[game.getActualPlayer().getNumber()-1].getChildren().remove(index);
                        allPieces[game.getActualPlayer().getNumber()-1].getChildren().add(index,drawPiece(game.getActualPlayer().getColor(),piece,allPieces[game.getActualPlayer().getNumber()-1]));
                        break;
                    }
                }
            }
        });

        leftRotate = new Button("Left rotation");
        leftRotate.setTranslateX(-110); leftRotate.setTranslateY(-20);
        //TODO fix drag after rotating a piece and also rotate the array INTEGER of the piece
        leftRotate.setOnAction(actionEvent ->  {
            int pieceNbr = actualSelectedPieceNbr;
            Piece piece = game.getActualPlayer().getPiecesList().get(pieceNbr-1);
            piece.rotateLeft();
            int isPiece = 0;
            for(Object object:allPieces[game.getActualPlayer().getNumber()-1].getChildren()) {
                if(object.getClass().equals(GridPane.class)){
                    isPiece++;
                    if((isPiece==pieceNbr)){
                        int index = allPieces[game.getActualPlayer().getNumber()-1].getChildren().indexOf(object);
                        allPieces[game.getActualPlayer().getNumber()-1].getChildren().remove(index);
                        allPieces[game.getActualPlayer().getNumber()-1].getChildren().add(index,drawPiece(game.getActualPlayer().getColor(),piece,allPieces[game.getActualPlayer().getNumber()-1]));

                        break;
                    }
                }
            }
        });
        Text text = new Text("1) Select a piece and rotate it:");
        text.setTranslateX(-50);text.setTranslateY(-75);
        text.setFont(Font.font("Verdana", 20));
        text.setFill(Color.WHITE);
        Text text1 = new Text("2) Drag the piece on the board");
        text1.setTranslateY(30);text1.setTranslateX(-47);
        text1.setFont(Font.font("Verdana", 20));
        text1.setFill(Color.WHITE);
        Text text2 = new Text("3) Release mouse on the case wanted");
        text2.setTranslateY(80);text2.setTranslateX(-12);
        text2.setFont(Font.font("Verdana", 20));
        text2.setFill(Color.WHITE);
        StackPane layout = new StackPane();
        if(players.length!=2){
            Node player2 = pieceOfPlayer(1);
            layout.getChildren().add(player2);
            layout.getChildren().addAll(text,text1,principal,rightRotate,leftRotate,flip,text2);
        }else {
            layout.getChildren().addAll(text,text1,principal,rightRotate,leftRotate,flip,text2);
        }
        return layout;
    }

    /**
     * method used to create and refresh each left piece of each player
     * @param playerNbr give the info on the number of player
     * @return the node of each left piece of each player
     */
    public FlowPane pieceOfPlayer(int playerNbr){
        allPieces[playerNbr] = new FlowPane();
        Text text = new Text(players[playerNbr].getName());
        text.setFont(Font.font("Verdana", 20));
        text.setFill(players[playerNbr].getColor());
        Text text1 = new Text(" pieces left:  ");
        text1.setFont(Font.font("Verdana", 20));
        text1.setFill(Color.WHITE);
        allPieces[playerNbr].getChildren().addAll(text,text1);
        int pieceCounter = 0;
        for (Piece pieceLeft:players[playerNbr].getPiecesList()) {
            if(!pieceLeft.isUsed()){
                allPieces[playerNbr].getChildren().add(new Text(Integer.toString(++pieceCounter)));
                Node piece = drawPiece(players[playerNbr].getColor(),pieceLeft,allPieces[playerNbr]);
                pieceLeft.setPosInBoardX(piece.getTranslateX());
                pieceLeft.setPosInBoardY(piece.getTranslateY());
                allPieces[playerNbr].getChildren().add(piece);
            }
        }
        allPieces[playerNbr].setMinSize(allPieces[playerNbr].getWidth(),allPieces[playerNbr].getHeight());
        return allPieces[playerNbr];
    }

    /**
     * method used to draw a specific piece and also contains the action listener of the drag and drop
     * @param playerColor gives info on the actual player color
     * @param pieceRoot
     * @param allPieces
     * @return the node of that specific piece
     */
    public Pane drawPiece(Color playerColor,Piece pieceRoot,Pane allPieces){
        GridPane piece = new GridPane();
        for (int i = 0; i < pieceRoot.getShape().length; i++) {
            for (int j = 0; j < pieceRoot.getShape()[i].length; j++) {
                Rectangle tile = new Rectangle(CELL_SIZE/2, CELL_SIZE/2);
                if(pieceRoot.getShape()[i][j]==1){
                    tile.setFill(playerColor);
                    tile.setStrokeWidth(2.0);
                    tile.setStroke(Color.BLACK);
                    piece.add(new StackPane(tile),j,i);
                }else{
                    tile.setFill(Color.TRANSPARENT);
                    piece.add(new StackPane(tile),j,i);
                }

            }
        }
        final double[] xPos = new double[1];
        final double[] yPos = new double[1];
        piece.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                piece.setScaleX(2);piece.setScaleY(2);
                xPos[0] = piece.getTranslateX()+5; //so that the mouse holds the piece in the middle of the first square
                yPos[0] = piece.getTranslateY()+5;

                int isPiece = 0;
                for (Object object : allPieces.getChildren()) {
                    if(object.getClass().equals(GridPane.class)){
                        isPiece++;
                        if (!object.equals(piece)){
                            GridPane piece = (GridPane) object;
                            piece.setOpacity(0.3);
                        }else if(object.equals(piece)){
                            actualSelectedPieceNbr = isPiece;
                            GridPane piece = (GridPane) object;
                            piece.setOpacity(1);
                        }

                    }
                }


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

                Bounds bounds = gameBoardRep.getLayoutBounds();
                Point2D coordinates = gameBoardRep.localToScene(bounds.getMinX(), bounds.getMinY());

                //System.out.println(coordinates.getX()+ " "+coordinates.getY());
                Vector2d position = new Vector2d((int)((MousePosX-coordinates.getX())/27),(int)((MousePosY-coordinates.getY())/27));
                //System.out.println(position.get_x() + "  " + position.get_y());
                //System.out.println("starting corner: "+actualPlayer.getStartingCorner().get_x()+" "+actualPlayer.getStartingCorner().get_y());

                Move move = new Move(game.getActualPlayer(),pieceRoot,position);

                if(game.makeMove(move)){
                    game.moveAllowed(piece,pieceRoot,allPieces);
                }else{
                    piece.setScaleX(1);piece.setScaleY(1);
                    piece.setTranslateX(pieceRoot.getPosInBoardX());
                    piece.setTranslateY(pieceRoot.getPosInBoardY());
                }
                //System.out.println(actualPlayer.getPiecesList().get(0).getPosInBoardX()+" "+actualPlayer.getPiecesList().get(0).getPosInBoardY  ());
                event.consume();
            }
        });

        return piece;
    }
}
