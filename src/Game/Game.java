package Game;

import MiniMax.MiniMax;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import GameBoard.Board;
import GameBoard.BoardUI;
import MonteCarlo.MonteCarlo;
import MonteCarlo.MCTS;
import Move.Move;
import Player.*;
import Tools.Vector2d;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import DataBase.*;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.util.Pair;
import menu.MenuItem;


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Game extends Application {

    private Player[] players;
    public static int nbrPlayers=0;
    private int DIMENSION = Data.getDIMENSION();
    private BoardUI boardUI;

    private Player actualPlayer;
    public GameState state;

    private ArrayList<Move> movesLog=new ArrayList<>();

    private Stage stage;

    MiniMax miniMax;

    public Board board;

    private int timeLimit = 3500; // timelimit for MC player and GAMC player

    Scene scene;


    /**
     * the game can have those 3 states
     */
    public enum GameState {
        HUMAN_MOVE,
        AI_MOVE,
        END

    }

    public Game(){
        this.stage = new Stage();
        initializeGame();
        start(stage);
    }

    /**
     * method overiden from the application that start the javafx framework
     * @param stage
     */
    @Override
    public void start(Stage stage){
        Parent root = this.boardUI.gameBoard;
        scene = new Scene(root, 1000, 800);
        this.stage = stage;
        this.stage.setTitle("Blokus Board Screen");
        this.stage.setScene(scene);
        this.stage.centerOnScreen();
        this.stage.setMaximized(true);
        this.stage.show();

    }

    /**
     * initialization of the game parameters
     */
    public void initializeGame(){

        players=initializePlayers(Data.getPlayersName(), DIMENSION, players);
        actualPlayer = players[0];
        nbrPlayers=players.length;
        //System.out.println("nbrplayers"+nbrPlayers);
        for(Player player:players)
            System.out.println(player.getPlayerNumber());
        board = new Board(players);
        boardUI = new BoardUI(this);
        miniMax = new MiniMax(players,board);
        for(Player p: players) if(p instanceof GAMCplayer) ((GAMCplayer) p).setMc(new MonteCarlo(players, board));
        else if(p instanceof GaMcTplayer) ((GaMcTplayer) p).setMc(new MCTS(players, board));
        else if(p instanceof MCPlayer)((MCPlayer) p).setMc(new MonteCarlo(players, board));
        else if(p instanceof MCTSplayer)((MCTSplayer) p).setMcts(new MCTS(players, board));
        if (actualPlayer instanceof HumanPlayer)
            state = GameState.HUMAN_MOVE;
        else {
            state = GameState.AI_MOVE;
            handleAITurn();
        }
    }

    /**
     * method used to initialize every player
     */
    public static Player[] initializePlayers(String[] playersName, int DIMENSION, Player[] players){
        Color[] colors = {Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE};
        players= new Player[playersName.length];
        nbrPlayers=players.length;
        //System.out.println("this code is in game.java line 66,67,68, jo, martin and gyu, comment where necessary it please");

        for(int i=1; i<= playersName.length; i++){
            String playerType = Data.getPlayerTypes()[i-1];
            if(playerType.equals("Human Player")){
                players[i-1]=new HumanPlayer(i);
            }else if(playerType.equals("Monte Carlo Player")){
                players[i-1]=new MCPlayer(i,playersName[i-1]);
            }else if(playerType.equals("Genetic Player")){
                players[i-1]=new GeneticPlayer(i);
            }else if(playerType.equals("MiniMax-MaxN Player")||playerType.equals("MiniMax-Paranoid Player")){
                players[i-1]=new MiniMaxPlayer(i);
            }else if(playerType.equals("GAMC Player")){
                players[i-1]=new GAMCplayer(i);
            }else if(playerType.equals("Monte Carlo Tree Player")){
                players[i-1]=new MCTSplayer(i);
            }else if(playerType.equals("Genetic Monte Carlo Tree Player")) {
                players[i - 1] = new GaMcTplayer(i);
            }
            players[i-1].setColor(colors[i-1]);
            players[i-1].setName(playersName[i-1]);
        }
        initializePlayerPieces(playersName, players);
        players[0].setStartingCorner(new Vector2d(0,0));
        if(players.length==4){
            players[1].setStartingCorner(new Vector2d(DIMENSION-1,0));
            players[2].setStartingCorner(new Vector2d(DIMENSION-1,DIMENSION-1));
            players[3].setStartingCorner(new Vector2d(0,DIMENSION-1));
        }else{
            players[1].setStartingCorner(new Vector2d(DIMENSION-1,DIMENSION-1));
        }

        return players;
    }


    /**
     * method to initialize each player pieces
     */
    public static void initializePlayerPieces(String[] playersName,  Player[] players){

        for(int i=1; i<= playersName.length; i++){
            PieceFactory pieceFactory= PieceFactory.get();
            List<Piece> pieces;
            if(players.length==4){
                if(Data.containMin()){
                    pieces = pieceFactory.getSomePieces();
                }else{
                    pieces = pieceFactory.getAllPieces();
                }
            }else{
                pieces = pieceFactory.getAllPieces();
            }
            players[i-1].setPiecesList(pieces);
        }

    }

    /**
     * method that handles when a move has been allowed on the board
     * @param piece is the representation of the piece of the player
     * @param pieceRoot is the object of the piece of the player
     * @param allPieces is the array of all the left pieces of each player
     */
    public void moveAllowed(Pane piece, Piece pieceRoot, Pane allPieces) throws Exception {
        //System.out.println("piece removed");
        if(piece!=null){
            allPieces.getChildren().remove(piece); //every piece also has an internal used state which is updated
        }
        actualPlayer.removePiece(pieceRoot.getLabel());
        updateState();
        boardUI.paint();
    }

    /**
     * Handles turns flow
     *
     * If if it is player 1 turn, then next turn will correspond to player 2,
     after the last player, we go back to the first one
     */
    public void nextTurn() throws Exception {
        if (actualPlayer.getPlayerNumber()<players.length)
            actualPlayer=players[actualPlayer.getPlayerNumber()]; //player 2 occupies index 1 in array of players
        else
            actualPlayer=players[0];

        //after the new player is assigned, we should check if thath player is able to do at least one move, else we skip him
        if( !  actualPlayer.possibleMove(board)){
            actualPlayer.setSkippedLastMove(true);//no move made, player out of the game.
            System.out.println("player "+actualPlayer.getName()+" can not move, no available moves");
            updateState();
        }
        else
            actualPlayer.setSkippedLastMove(false);

    }

    /**
     * to be called in every move
     * method that updates the game display
     */
    public void updateState() throws Exception {
        if(state== GameState.HUMAN_MOVE || state== GameState.AI_MOVE) {
            if (noOneMoved()){
                state = GameState.END;
                System.out.println("In game status:  mo one moved");
            }else{
                System.out.println(actualPlayer.getName()+ " skipped last move?: "+actualPlayer.getSkippedLastMove());
                boolean playerSkippedLastMove = false;
                if (actualPlayer.getSkippedLastMove()){
                    playerSkippedLastMove = true;
                }
                //System.out.println("In game status:  someone did move moved");
                nextTurn();
                if (actualPlayer instanceof HumanPlayer)
                    state = GameState.HUMAN_MOVE;
                else {
                    if(state!=GameState.END){
                        state = GameState.AI_MOVE;
                        if (!playerSkippedLastMove) {
                            handleAITurn();
                        }
                    }
                }
            }
            debuggingPiecesUsed();
        }


        if (state== GameState.END){
            countPoints();
            new EndScreen();
            this.stage.close();
        }
    }

    /**
     * method used to handle a future AI move
     */
    public void handleAITurn(){
        boardUI.leftRotate.setDisable(true);
        boardUI.rightRotate.setDisable(true);
        boardUI.flip.setDisable(true);
        Service<Move> calculateMove = new Service<>(){
            @Override
            protected Task<Move> createTask() {
                return new Task<>(){
                    @Override
                    protected Move call(){
                        Move move = null;
                        if( actualPlayer instanceof  GAMCplayer){
                            move = ((GAMCplayer) actualPlayer).getBestMove(board,timeLimit);
                            ((GAMCplayer)actualPlayer).addTurn();
                        }else if(actualPlayer instanceof MCTSplayer){
                            move = ((MCTSplayer) actualPlayer).getMcts().simulation(actualPlayer.getNumber()-1, timeLimit);
                        }else if(actualPlayer instanceof GaMcTplayer){
                            move = ((GaMcTplayer) actualPlayer).getBestMove(board,timeLimit);
                            ((GeneticPlayer) actualPlayer).addTurn();
                        }else if(actualPlayer instanceof GeneticPlayer){
                            move = ((GeneticPlayer) actualPlayer).calculateMove(board);
                            ((GeneticPlayer) actualPlayer).addTurn();
                        }else if(actualPlayer instanceof MCPlayer) {
                            move = ((MCPlayer) actualPlayer).getMc().simulation(actualPlayer.getNumber()-1, timeLimit);
                        }else if(actualPlayer instanceof MiniMaxPlayer){
                            move = miniMax.getMove(actualPlayer.getPlayerNumber());
                        }
                        return move;
                    }
                };
            }
        };
        calculateMove.start();

        calculateMove.setOnSucceeded(e -> {
            // this code executed when task is successfully completed
            // this is executed on the FX Application Thread, so you can
            // safely modify the UI

            Move move = calculateMove.getValue();
            if (move!=null&&move.makeMove(board)) {
                Transition transition = boardUI.animateAIMove(move);
                transition.setOnFinished(f -> {
                    try {
                        moveAllowed(null, move.getPiece(), boardUI.allPieces[actualPlayer.getNumber() - 1]);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
                transition.play();
            }
        });
    }


    /**
     * If none of the players made its move, then the game just ended
     * @return true if none of the plaayers made its move
     */
    public boolean noOneMoved(){
        for (Player player: players){
            if(!player.getSkippedLastMove())
                return false;
        }
        return true;
    }

    /**
     * method that counts the score of each player
     */
    public void countPoints(){
        System.out.println("In countPoints(): ");
        for(Player player: players) {
            countPointsPlayer(player);

            System.out.println(player.getName()+"has "+player.getPoints()+" points");
        }
    }



    /**
     * When a game ends, each player counts every square that he/she did NOT place on the board,
     * each counting as a negative (-1) point (e.g. a tetromino is worth -4 points).
     * The player with the highest score wins. A player who played all of his or her pieces is awarded a +20 point bonus
     * if the last piece played was a monomino, or a +15 point bonus for any other piece
     * @param player
     */
    public void countPointsPlayer(Player player){
        int points=0;
        int piecesPlaced=0;
        int blocksNotPlaced=0;
        for(Piece piece : player.getPiecesUsed()){
            //in board ui, pieces are not marked as used
            piecesPlaced++;
            //System.out.println("piece used");
        }
        for(Piece piece : player.getPiecesList()) {
            blocksNotPlaced += piece.getNumberOfBlocks();
            // System.out.println("number of blocks: "+piece.getNumberOfBlocks());
        }


        points-=blocksNotPlaced;

        if (piecesPlaced==21){//if I check if number of unused pieces is 0, the size might be 1 when it should be 0 cause the deletion is done in the ui logic after the make move...but just maybe, I have not checked
            Piece lastPiece= player.getMoveLog().peek().getPiece();
            System.out.println("last piece placed"+ lastPiece.getLabel());
            if(lastPiece.getNumberOfBlocks()==1)
                points+=20;
            else
                points+=15;

        }
        System.out.println(player.getName()+" Points: "+ points);

        player.setPoints(points);


    }

    /**
     * writes the piece into the board and adds it to the log
     * @param move move asked to be handle
     * @return true if the move is allowed on the board, false otherwise
     */
    public boolean makeMove(Move move){

        if(move.makeMove(board)) {
            movesLog.add(move);
            return true;
        }
        return false;

    }

    /**
     * method needed when needs to debug pieces bug
     */
    public void debuggingPiecesUsed() {
        for (Piece piece : actualPlayer.getPiecesList()) {
            if (piece.isUsed())
                System.out.println("piece " + piece.getLabel() + "is used");

        }
    }

    public Player getActualPlayer() {
        return actualPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }
    public class EndScreen extends Application{
        Stage stage;

        public EndScreen() throws Exception {
            this.stage = new Stage();
            start(stage);
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            List<Pair<String, Runnable>> menuData = Arrays.asList(
                    new Pair<String, Runnable>("Restart", () -> {
                        new Game();
                        this.stage.close();
                    }),

                    new Pair<String, Runnable>("Exit to Menu", () -> {
                        try {
                            new StartScreen().start(stage);
                            this.stage.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
            );
            VBox menuBox = new VBox(-5);

            int bestPts = Integer.MIN_VALUE;
            String winner = "";
            Color winnerColor = Color.WHITE;
            for (Player player:players) {
                if(player.getPoints()>bestPts){
                    bestPts = player.getPoints();
                    winner = player.getName();
                    winnerColor = player.getColor();
                }
            }
            Text win = new Text("GAME END - The winner is " + winner);
            win.setFill(winnerColor);

            VBox scores = new VBox(20);
            for (Player player:players) {
                Text score = new Text();
                score.setFill(player.getColor());
                score.setText(player.getName() + " score: " + player.getPoints());
                scores.getChildren().add(score);
            }

            menuData.forEach(data -> {
                menu.MenuItem item = new MenuItem(data.getKey());
                item.setOnAction(data.getValue());
                //item.setTranslateX(-300);

                javafx.scene.shape.Rectangle clip = new Rectangle(300, 30);
                clip.translateXProperty().bind(item.translateXProperty().negate());

                item.setClip(clip);

                menuBox.getChildren().addAll(item);
            });
            ScaleTransition st = new ScaleTransition(Duration.seconds(1));
            st.setToY(1);
            st.setOnFinished(e -> {

                for (int i = 0; i < menuBox.getChildren().size(); i++) {
                    Node n = menuBox.getChildren().get(i);

                    TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
                    tt.setToX(0);
                    tt.setOnFinished(e2 -> n.setClip(null));
                    tt.play();
                }
            });
            st.play();

            GridPane pieces = new GridPane();
            pieces.add(boardUI.pieceOfPlayer(0),0,0);
            if(players.length==4){
                pieces.add(boardUI.pieceOfPlayer(1),0,1);
                pieces.add(boardUI.pieceOfPlayer(2),0,2);
                pieces.add(boardUI.pieceOfPlayer(3),0,3);
            }else{
                pieces.add(boardUI.pieceOfPlayer(1),0,1);
            }
            pieces.setDisable(true);

            BorderPane principal = new BorderPane();
            principal.setBackground(Data.createBackGround());
            principal.setTop(win);
            FlowPane left = new FlowPane();
            left.getChildren().add(scores);
            left.getChildren().add(menuBox);
            principal.setLeft(left);
            principal.setCenter(boardUI.principal);
            boardUI.principal.setVisible(true);
            //boardUI.principal.setScaleX(5);boardUI.principal.setScaleY(5);
            principal.setRight(pieces);

            scene = new Scene(principal, 1400, 800);
            stage.setScene(scene);
            this.stage.setMaximized(true);
            this.stage.show();
        }
    }
}
