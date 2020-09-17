import DataBase.Pieces.*;
import GameBoard.Board;
import GameBoard.BoardUI;
import Player.HumanPlayer;
import Player.Player;
import Tools.Vector2d;
import com.sun.glass.ui.Screen;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import GameBoard.BoardUI;
import DataBase.*;
import org.w3c.dom.css.Rect;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

public class Game extends Application {

    private Player[] players;
    private Board board;
    private Player currentTurn;
    private GameStatus status;
    private ArrayList<Move> movesPlayed= new ArrayList<>();
    private int numberOfPlayers = 4;
    private ArrayList<Move> movesLog=new ArrayList<>();

    @Override
    public void start(Stage stage){
        initializeNewGame(numberOfPlayers,new Vector2d(50,50));
        BoardUI gameBoard = new BoardUI(4,players);
        Parent root = gameBoard.gameBoardRep;
        stage.setTitle("Blokus Game Group 15");
        Scene scene = new Scene(root, 800, 800);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public enum GameStatus {
        INTRO,
        THINKING_MOVE,
        MOVE_CHOSEN,
        WINNER,

    }

//NO BOT OPTION
    public void initializeNewGame(int numberOfPlayers, Vector2d boardDimensions){

        this.board= new Board(numberOfPlayers);
        initializePlayers(numberOfPlayers);

        //Draw everything
        currentTurn=players[0];



    }
    private void initializePlayers(int numberOfPlayers){
        players= new Player[numberOfPlayers];
        for(int i=1; i<= numberOfPlayers; i++){
            players[i-1]=new HumanPlayer(i);
        }
        initializePlayerPieces(numberOfPlayers);
        initializePlayersColours(numberOfPlayers);
    }

    private void initializePlayersColours(int numberOfPlayers){
        Color[] colors = {Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE};
        for(int i=1; i<= numberOfPlayers; i++){
            players[i-1].setColor(colors[i-1]);
        }
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

    //writes the piece into the board and adds it to the log
    public boolean makeMove(Piece piece, Vector2d position){
        Move move = new Move(currentTurn, piece, position);
            if(move.makeMove(board)) {
                movesLog.add(move);
                nextTurn();
                return true;
            }
            return false;

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
