package Game;

import GameBoard.Board;
import GameBoard.BoardUI;
import Move.Move;
import Player.BotPlayer;
import Player.GeneticPlayer;
import Player.HumanPlayer;
import Player.Player;
import Tools.Vector2d;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import DataBase.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Rectangle;
import GameBoard.BoardUI;

import java.sql.SQLOutput;
import java.util.Scanner;
import java.awt.*;
import java.util.ArrayList;
import GameBoard.BoardUI;
import java.util.List;
import java.util.LinkedList;
//import Player.GeneticPlayer;

import javax.sound.midi.Soundbank;

public class Game extends Application {

    private Player[] players;
    private int DIMENSION;
    private String[] playersName;

    /**
     * method overiden from the application
     * @param stage
     */
    @Override
    public void start(Stage stage){
        DIMENSION = Data.getDIMENSION();
        playersName = Data.getPlayersName();
        players=initializePlayers(playersName, DIMENSION, players);
        for(Player player:players)
            System.out.println(player.getPlayerNumber());

        BoardUI gameBoard = new BoardUI(players,stage);
        Parent root = gameBoard.gameBoard;
        stage.setTitle("Blokus Game.Game Group 15");
        Scene scene = new Scene(root, 1000, 1000);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
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


/*If if it is player 1 turn, then next turn will correspond to player 2,
         after the last player, we go back to the first one
         */
    /*
    private void nextTurn(){


        if (currentTurn.getPlayerNumber()<numberOfPlayers)
            currentTurn=players[currentTurn.getPlayerNumber()]; //player 2 occupies index 1 in array of players
        else
            currentTurn=players[0];
    }*/
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

            }
        }

 */


    /**
     * If none of the players made its move, then the game just ended
     * @return true if none of the plaayers made its move
     *//*
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
     *//*
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

    */
}
