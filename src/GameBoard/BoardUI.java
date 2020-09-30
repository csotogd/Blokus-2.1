package GameBoard;

import DataBase.Piece;
import Move.Move;
import Player.Player;
import Tools.Vector2d;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
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

import Player.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//TEST
public class BoardUI{
    public Board board;
    public Parent gameBoard;
    public Player[] players; //Initialize in game object
    private Background background;
    private final Vector2d RECTANGLE_SIZE = new Vector2d(100,280);
    private final int CELL_SIZE = 25;
    private Player actualPlayer;
    private int playerCounter;

    private GameState state;
    private ArrayList<Move> movesLog=new ArrayList<>();

    Pane center;
    Pane right;
    Pane bottom;
    Pane left;
    Pane top;
    FlowPane allPieces[];
    ChoiceBox choiceBox;
    Text turnOfPlayerText;

    public enum GameState {
        HUMAN_MOVE,
        AI_MOVE,
        END

    }



    public BoardUI(Player[] players){
        this.players = players;
        allPieces = new FlowPane[players.length];
        this.playerCounter = 0;
        this.actualPlayer = this.players[playerCounter++];
        this.background = createBackGround();
        this.board = new Board(players);
        this.gameBoard = createBoard();
        makePiecesOpaque();

        state=GameState.HUMAN_MOVE; //TODO change this for second period

    }

    public void makePiecesOpaque(){

        for (Player player : players) {
            int isPiece = 0;
            for (Object object:allPieces[player.getNumber()-1].getChildren()) {
                if(object.getClass().equals(GridPane.class)){
                    isPiece++;
                    if (isPiece!=(int)choiceBox.getSelectionModel().getSelectedItem()&&actualPlayer==player){
                        GridPane piece = (GridPane) object;
                        piece.setOpacity(0.3);
                        piece.setDisable(true);
                    }else if(isPiece==(int)choiceBox.getSelectionModel().getSelectedItem()&&actualPlayer==player){
                        GridPane piece = (GridPane) object;
                        piece.setOpacity(1);
                        piece.setDisable(false);
                    }else if(actualPlayer!=player){
                        GridPane piece = (GridPane) object;
                        piece.setOpacity(0.3);
                        piece.setDisable(true);
                    }

                }
            }
        }
    }

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
        center.getChildren().add(board);
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
        turnOfPlayerText = new Text(actualPlayer.getName());
        turnOfPlayerText.setTranslateY(15);turnOfPlayerText.setTranslateX(-50);
        turnOfPlayerText.setFont(Font.font("Verdana", 30));
        turnOfPlayerText.setFill(actualPlayer.getColor());
        StackPane layout = new StackPane();
        if(players.length!=2){
            Node player4 = pieceOfPlayer(3);
            layout.getChildren().addAll(principal,text,turnOfPlayerText,player4);
        }else {
            layout.getChildren().addAll(principal,text,turnOfPlayerText);
        }



        return layout;
    }

    public Pane rotationButtons(){
        Rectangle principal = new Rectangle();
        principal.setFill(Color.TRANSPARENT);
        principal.setStroke(Color.WHITE);
        principal.setHeight(RECTANGLE_SIZE.get_x()*2.3f);
        principal.setWidth(RECTANGLE_SIZE.get_y()*1.5f);

        choiceBox = new ChoiceBox();
        choiceBox.setTranslateX(140);choiceBox.setTranslateY(-75);
        for (int i = 0; i < actualPlayer.getPiecesList().size(); i++) {
            choiceBox.getItems().add(i+1);
        }
        choiceBox.getSelectionModel().select(0);
        choiceBox.setOnAction(actionEvent ->  {
            makePiecesOpaque();
        });
        Button rightRotate = new Button("Right rotation");
        rightRotate.setTranslateX(105); rightRotate.setTranslateY(-20);
        //TODO fix drag after rotating a piece and also rotate the array INTEGER of the piece
        rightRotate.setOnAction(actionEvent ->  {
            int pieceNbr = (int) choiceBox.getSelectionModel().getSelectedItem();
            Piece piece = actualPlayer.getPiecesList().get(pieceNbr-1);
            piece.rotateRight();
            int isPiece = 0;
            for (Object object:allPieces[actualPlayer.getNumber()-1].getChildren()) {
                if(object.getClass().equals(GridPane.class)){
                    isPiece++;
                    if((isPiece==pieceNbr)){
                        int index = allPieces[actualPlayer.getNumber()-1].getChildren().indexOf(object);
                        allPieces[actualPlayer.getNumber()-1].getChildren().remove(index);
                        allPieces[actualPlayer.getNumber()-1].getChildren().add(index,drawPiece(actualPlayer.getColor(),piece,allPieces[actualPlayer.getNumber()-1]));
                        break;
                    }
                }
            }
        });

        Button flip = new Button("Flip");
        //TODO finish the flip rotation
        flip.setTranslateX(0); flip.setTranslateY(-20);
        flip.setOnAction(actionEvent ->  {
            int pieceNbr = (int) choiceBox.getSelectionModel().getSelectedItem();
            Piece piece = actualPlayer.getPiecesList().get(pieceNbr-1);
            piece.rotateUpsideDown();
            int isPiece = 0;
            for (Object object:allPieces[actualPlayer.getNumber()-1].getChildren()) {
                if(object.getClass().equals(GridPane.class)){
                    isPiece++;
                    if((isPiece==pieceNbr)){
                        int index = allPieces[actualPlayer.getNumber()-1].getChildren().indexOf(object);
                        allPieces[actualPlayer.getNumber()-1].getChildren().remove(index);
                        allPieces[actualPlayer.getNumber()-1].getChildren().add(index,drawPiece(actualPlayer.getColor(),piece,allPieces[actualPlayer.getNumber()-1]));
/*
                        allPieces[playerCounter-1].getChildren().remove(index);
                        allPieces[playerCounter-1].getChildren().add(index,drawPiece(actualPlayer.getColor(),piece,allPieces[playerCounter-1]));
               */
                        break;
                    }
                }
            }
        });

        Button leftRotate = new Button("Left rotation");
        leftRotate.setTranslateX(-110); leftRotate.setTranslateY(-20);
        //TODO fix drag after rotating a piece and also rotate the array INTEGER of the piece
        leftRotate.setOnAction(actionEvent ->  {
            int pieceNbr = (int) choiceBox.getSelectionModel().getSelectedItem();
            Piece piece = actualPlayer.getPiecesList().get(pieceNbr-1);
            piece.rotateLeft();
            int isPiece = 0;
            for(Object object:allPieces[actualPlayer.getNumber()-1].getChildren()) {
                if(object.getClass().equals(GridPane.class)){
                    isPiece++;
                    if((isPiece==pieceNbr)){
                        int index = allPieces[actualPlayer.getNumber()-1].getChildren().indexOf(object);
                        /*
                        allPieces[playerCounter-1].getChildren().remove(index);
                        allPieces[playerCounter-1].getChildren().add(index,drawPiece(actualPlayer.getColor(),piece,allPieces[playerCounter-1]));
                        */
                        allPieces[actualPlayer.getNumber()-1].getChildren().remove(index);
                        allPieces[actualPlayer.getNumber()-1].getChildren().add(index,drawPiece(actualPlayer.getColor(),piece,allPieces[actualPlayer.getNumber()-1]));

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
            layout.getChildren().addAll(text,text1,principal,rightRotate,leftRotate,flip,choiceBox,text2);
        }else {
            layout.getChildren().addAll(text,text1,principal,rightRotate,leftRotate,flip,choiceBox,text2);
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

                //System.out.println(coordinates.getX()+ " "+coordinates.getY());
                Vector2d position = new Vector2d((int)((MousePosX-coordinates.getX())/27),(int)((MousePosY-coordinates.getY())/27));
                //System.out.println(position.get_x() + "  " + position.get_y());
                //System.out.println("starting corner: "+actualPlayer.getStartingCorner().get_x()+" "+actualPlayer.getStartingCorner().get_y());

                Move move = new Move(actualPlayer,pieceRoot,position);

                if(makeMove(move)){
                    //System.out.println("piece removed");
                    allPieces.getChildren().remove(piece); //every piece also has an internal used state which is updated
                    actualPlayer.getPiecesList().remove(pieceRoot);
                    updateState();
                   // actualPlayer = players[playerCounter++];
                   // if(playerCounter>=players.length) playerCounter=0;
                    //choiceBox.getSelectionModel().select(0);
                    makePiecesOpaque();
                    turnOfPlayerText.setText(actualPlayer.getName());
                    turnOfPlayerText.setFill(actualPlayer.getColor());
                    refreshPieces();
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

    /*If if it is player 1 turn, then next turn will correspond to player 2,
          after the last player, we go back to the first one
          */
    private void nextTurn(){

        if (actualPlayer.getPlayerNumber()<players.length)
            actualPlayer=players[actualPlayer.getPlayerNumber()]; //player 2 occupies index 1 in array of players
        else
            actualPlayer=players[0];

        //after the new player is assigned, we should check if thath player is able to do at least one move, else we skip him
        if( !  actualPlayer.possibleMove(board)){
            actualPlayer.setSkippedLastMove(true);
            System.out.println("player "+actualPlayer.getName()+" can not move, no available moves");
            updateState();
        }
        else
            actualPlayer.setSkippedLastMove(false);

    }

    /**
     * to be called in every move
     */
    private void updateState(){

        if(state== GameState.HUMAN_MOVE || state==GameState.AI_MOVE) {
            if (noOneMoved()){
                state = GameState.END;
            System.out.println("In game status:  mo one moved");
            }
            else {
                System.out.println(actualPlayer.getName()+ " skipped last move?: "+actualPlayer.getSkippedLastMove());
                //System.out.println("In game status:  someone did move moved");
                nextTurn();
                if (actualPlayer instanceof BotPlayer)
                state = GameState.HUMAN_MOVE;
                else
                state = GameState.AI_MOVE;


            }
        }


        if (state==GameState.END){

                countPoints();
                System.out.println("THE GAME HAS ENDED");
                //displayWinner();
                //should show something ,like play again?

        }



    }

    /**
     * If none of the players made its move, then the game just ended
     * @return true if none of the plaayers made its move
     */
    private boolean noOneMoved(){
        for (Player player: players){
            if(!player.getSkippedLastMove())
                return false;


        }
        return true;
    }

    private void countPoints(){
        for(Player player: players)
            countPointsPlayer(player);
    }



    /**
     * When a game ends, each player counts every square that he/she did NOT place on the board,
     * each counting as a negative (-1) point (e.g. a tetromino is worth -4 points).
     * The player with the highest score wins. A player who played all of his or her pieces is awarded a +20 point bonus
     * if the last piece played was a monomino, or a +15 point bonus for any other piece
     * @param player
     */
    private void countPointsPlayer(Player player){
        int points=0;
        int piecesPlaced=0;
        int blocksNotPlaced=0;
        for(Piece piece : player.getPiecesList()){
            if(piece.isUsed())   //in board ui, pieces are not marked as used
                piecesPlaced++;
            else
                blocksNotPlaced+=piece.getNumberOfBlocks();
        }
        points-=blocksNotPlaced;

        if (piecesPlaced==player.getPiecesList().size()){
            Piece lastPiece= player.getMoveLog().peek().getPiece();
            if(lastPiece.getNumberOfBlocks()==1)
                points+=20;
            else
                points+=15;

        }

        player.setPoints(points);

    }



    //writes the piece into the board and adds it to the log
    public boolean makeMove(Move move){
        if(move.makeMove(board)) {
            movesLog.add(move);
            //updateState();
            return true;
        }
        return false;

    }


}
