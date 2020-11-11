package Game;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import GameBoard.Board;
import GameBoard.BoardUI;
import MonteCarlo.MonteCarlo;
import Move.Move;
import Player.BotPlayer;
import Player.GeneticPlayer;
import Player.HumanPlayer;
import Player.Player;
import Tools.Vector2d;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import DataBase.*;

import java.util.ArrayList;
import java.util.List;
//import Player.GeneticPlayer;


public class Game extends Application {

    private Player[] players;
    private int DIMENSION;
    private BoardUI boardUI;

    private Player actualPlayer;
    public GameState state;

    private ArrayList<Move> movesLog=new ArrayList<>();

    private Stage stage;

    MonteCarlo mc;

    public Board board;


    /**
     * the game can have those 3 states
     */
    public enum GameState {
        HUMAN_MOVE,
        AI_MOVE,
        END

    }

    public Game(Stage stage){
        this.stage = stage;
        initializeGame();
        start(stage);
    }

    /**
     * method overiden from the application
     * @param stage
     */
    @Override
    public void start(Stage stage){

        Parent root = this.boardUI.gameBoard;
        stage.setTitle("Blokus Game.Game Group 15");
        Scene scene = new Scene(root, 1000, 1000);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void initializeGame(){
        DIMENSION = Data.getDIMENSION();
        players=initializePlayers(Data.getPlayersName(), DIMENSION, players);
        actualPlayer = players[0];
        for(Player player:players)
            System.out.println(player.getPlayerNumber());
        board = new Board(players);
        boardUI = new BoardUI(this);
        mc = new MonteCarlo(players,board);
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
        //System.out.println("this code is in game.java line 66,67,68, jo, martin and gyu, comment where necessary it please");

        for(int i=1; i<= playersName.length; i++){
            String playerType = Data.getPlayerTypes()[i-1];
            if(playerType.equals("Human Player")){
                players[i-1]=new HumanPlayer(i);
            }else if(playerType.equals("Monte Carlo Player")){
                System.out.println("Bot");
                //TODO add the Monte Carlo Player class
                players[i-1]=new BotPlayer(i,playersName[i-1]);
            }else if(playerType.equals("Genetic Player")){
                players[i-1]=new GeneticPlayer(i);
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
            List<Piece> pieces = pieceFactory.getAllPieces();

            players[i-1].setPiecesList(pieces);

        }

    }

    public void moveAllowed(Pane piece, Piece pieceRoot, Pane allPieces){
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
    public void nextTurn(){

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
     */
    public void updateState(){
        if(state== GameState.HUMAN_MOVE || state== GameState.AI_MOVE) {
            if (noOneMoved()){
                state = GameState.END;
                System.out.println("In game status:  mo one moved");
            }else{
                System.out.println(actualPlayer.getName()+ " skipped last move?: "+actualPlayer.getSkippedLastMove());
                //System.out.println("In game status:  someone did move moved");
                nextTurn();
                if (actualPlayer instanceof HumanPlayer)
                    state = GameState.HUMAN_MOVE;
                else {
                    if(state!=GameState.END){
                        state = GameState.AI_MOVE;
                        handleAITurn();
                    }
                }
            }
            debuggingPiecesUsed();
        }


        if (state== GameState.END){
            countPoints();
            System.out.println("THE GAME HAS ENDED");
            VBox dialogVbox = new VBox(20);
            dialogVbox.getChildren().add(new Text("GAME END"));
            for (Player player:players) {
                Text score = new Text();
                score.setFill(player.getColor());
                score.setText(player.getName() + " score: " + player.getPoints());
                dialogVbox.getChildren().add(score);
            }
            Pane pane = new FlowPane();
            pane.setBackground(Data.createBackGround());
            pane.getChildren().add(dialogVbox);
            Scene dialogScene = new Scene(pane, 300, 200);
            stage.setScene(dialogScene);
            stage.show();

        }
    }

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
                        if(actualPlayer instanceof GeneticPlayer){
                            move = ((GeneticPlayer) actualPlayer).calculateMove(board);
                        }else if(actualPlayer instanceof BotPlayer) {
                            move = mc.simulation(actualPlayer.getNumber()-1, 5000);
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
            if (calculateMove.isRunning()&&move.makeMove(board)) {
                boardUI.leftRotate.setDisable(true);
                boardUI.rightRotate.setDisable(true);
                boardUI.flip.setDisable(true);
                moveAllowed(null, move.getPiece(), boardUI.allPieces[actualPlayer.getNumber() - 1]);
            }
        });



        //boardUI.leftRotate.setDisable(false);
        //boardUI.rightRotate.setDisable(false);
        //boardUI.flip.setDisable(false);


        /*
        Move move = null;
        if(actualPlayer instanceof GeneticPlayer){
            move = ((GeneticPlayer) actualPlayer).calculateMove(board);
        }else if(actualPlayer instanceof BotPlayer) {
            move = mc.simulation(actualPlayer.getNumber()-1, 5000);
        }
        if (move.makeMove(board)) {
            move.writePieceIntoBoard(board);
            move.getPlayer().getMoveLog().push(move);
            move.getPiece().setUsed(true);//TODO erase this none sense line of code, completely useless
            move.getPlayer().getPiecesUsed().add(move.getPiece());
            if (move.getPlayer().isFirstMove()) move.getPlayer().setFirstMove(false);
            moveAllowed(null, move.getPiece(), boardUI.allPieces[actualPlayer.getNumber() - 1]);
        }

         */
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
            System.out.println("piece used");
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



    //writes the piece into the board and adds it to the log
    public boolean makeMove(Move move){
        if(move.makeMove(board)) {
            movesLog.add(move);
            //updateState();
            return true;
        }
        return false;

    }

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
}
