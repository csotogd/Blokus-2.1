import GameBoard.Board;
import GameBoard.BoardUI;
import Move.Move;
import Player.HumanPlayer;
import Player.Player;
import Tools.Vector2d;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import DataBase.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Rectangle;
import GameBoard.BoardUI;

import java.awt.*;
import java.util.ArrayList;
import GameBoard.BoardUI;
import java.util.List;
import java.util.LinkedList;

public class Game extends Application {

    private Player[] players;
    private Player currentTurn;
    private GameStatus status;
    private ArrayList<Move> movesPlayed= new ArrayList<>();
    private int numberOfPlayers = 4;
    private ArrayList<Move> movesLog=new ArrayList<>();
    private final Vector2d DIMENSION = new Vector2d(20, 20);

    @Override
    public void start(Stage stage){
        initializeNewGame(numberOfPlayers,new Vector2d(50,50));
        BoardUI gameBoard = new BoardUI(players);
        Parent root = gameBoard.gameBoard;
        stage.setTitle("Blokus Game Group 15");
        Scene scene = new Scene(root, 1000, 1000);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public enum GameStatus {
        INTRO,
        MOVE,
        END,

    }

//NO BOT OPTION
    public void initializeNewGame(int numberOfPlayers, Vector2d boardDimensions){

        initializePlayers(numberOfPlayers);

        //Draw everything
        currentTurn=players[0];



    }
    private void initializePlayers(int numberOfPlayers){
        Color[] colors = {Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE};
        players= new Player[numberOfPlayers];
        for(int i=1; i<= numberOfPlayers; i++){
            players[i-1]=new HumanPlayer(i);
            players[i-1].setColor(colors[i-1]);
        }
        initializePlayerPieces(numberOfPlayers);
        players[0].setStartingCorner(new Vector2d(0,0));
        if(players.length==4){
            players[1].setStartingCorner(new Vector2d(0,19));
            players[2].setStartingCorner(new Vector2d(19,19));
            players[3].setStartingCorner(new Vector2d(19,0));
        }else{
            players[1].setStartingCorner(new Vector2d(19,19));
        }

    }


    private void initPlayersStartCorner(int numberOfPlayers){
        Vector2d boardDimension= DIMENSION;
        players[0].setStartingCorner(new Vector2d(0,0));
        players[1].setStartingCorner(new Vector2d(boardDimension.get_x()-1, boardDimension.get_y()-1));
        if(numberOfPlayers>2)
            players[2].setStartingCorner(new Vector2d(boardDimension.get_x()-1, 0));
        if(numberOfPlayers>3)
            players[3].setStartingCorner(new Vector2d(0,boardDimension.get_y()-1));
    }

    private void initializePlayerPieces(int numberOfPlayers){

        for(int i=1; i<= numberOfPlayers; i++){
            PieceFactory pieceFactory= PieceFactory.get();
            List<Piece> pieces = pieceFactory.getAllPieces();

            players[i-1].setPiecesList(pieces);

        }

    }

    private void nextTurn(){
        /*If if it is player 1 turn, then next turn will correspond to player 2,
         after the last player, we go back to the first one
         */

        if (currentTurn.getPlayerNumber()<numberOfPlayers)
            currentTurn=players[currentTurn.getPlayerNumber()]; //player 2 occupies index 1 in array of players
        else
            currentTurn=players[0];
    }
/*
    //writes the piece into the board and adds it to the log
    public boolean makeMove(Piece piece, Vector2d position){
        Move move = new Move(currentTurn, piece, position);
            if(move.makeMove(board)) {
                movesLog.add(move);
                updateState();
                return true;
            }
            return false;

        }

 */

    //To be called after every move
    private void updateState(){
            if(status==GameStatus.INTRO)
                ;

            if(status==GameStatus.MOVE) {
                if (noOneMoved())
                    status = GameStatus.END;
                else
                    nextTurn();
            }

            if (status==GameStatus.END){
                /*
                countPoints();
                displayWinner();
                //should show something ,ike play again?
                  */
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
        if(piece.isUsed())
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


    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
