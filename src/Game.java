import DataBase.Pieces.FPiece;
import GameBoard.Board;
import Player.Player;
import Tools.Vector2d;
import Player.HumanPlayer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import GameBoard.BoardUI;
import DataBase.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Game extends Application {

    private Player[] players;
    private Board board;
    private Player currentTurn;
    private GameStatus status;
    private ArrayList<Move> movesPlayed= new ArrayList<>();
    private int numberOfPlayers;
    private ArrayList<Move> movesLog=new ArrayList<>();

    @Override
    public void start(Stage stage){
        initializeNewGame(2,new Vector2d(50,50));
        BoardUI gameBoard = new BoardUI(4,players);
        Parent root = gameBoard.gameBoardRep;
        stage.setTitle("Hello World");
        Scene scene = new Scene(root, 1000, 800);
        stage.setScene(scene);
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

        //this.board= new Board(boardDimensions);
        players= new Player[numberOfPlayers];
        for(int i=1; i<= numberOfPlayers; i++){
            players[i-1]=new HumanPlayer(i);
            LinkedList<Piece> pieces = new LinkedList<>();
            pieces.add(new FPiece());
            players[i-1].setPiecesList(pieces);

        }
        //Draw everything
        currentTurn=players[0];

    }



    public void nextTurn(){
        /*If if it is player 1 turn, then next turn will correspond to player 2,
         after the last player, we go back to the first one
         */

        if (currentTurn.getPlayerNumber()<numberOfPlayers)
            currentTurn=players[currentTurn.getPlayerNumber()]; //player 2 occupies index 1 in array of players
        else
            currentTurn=players[0];
    }

    //writes the piece into the board and adds it to the log
    public void makeMove(Piece piece, Vector2d position){
        Move move = new Move(currentTurn, piece, position);

            movesLog.add(move);

        }



    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public Player[] getPlayers() {
        return players;
    }
}
