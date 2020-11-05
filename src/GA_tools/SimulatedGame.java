package GA_tools;

import DataBase.Data;
import DataBase.Piece;
import Game.Game;
import GameBoard.Board;
import GameBoard.BoardUI;
import Move.Move;
import Player.Player;
import Tools.Vector2d;
import javafx.scene.layout.Pane;
import Player.GeneticPlayer;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

// there is a lot of code duplication with respect to class Board UI, if we had coded better in phase 1 this would not be
//necessary. Fixing it would be more tedious than writing a new class from scratch.

public class SimulatedGame {
    public Board board;
    public Player[] players; //Initialize in game object
    private Player actualPlayer;
    private int playerCounter;
    private final int NUMBER_OF_PIECES_PER_PLAYER=20;
    private BoardUI.GameState state;
    private ArrayList<Move> movesLog=new ArrayList<>();
    private  int nbrMoves=0;
    private int moveLimit=5;

    public SimulatedGame(int DIMENSION, Player[] players){
        String[] playersName= new String[players.length];
        this.players = this.initializePlayers(playersName, DIMENSION, players);
         this.board = new Board(this.players);
         state= BoardUI.GameState.AI_MOVE;

        //game state of human move not considered
    }

public void startSimulation(){
        actualPlayer=players[0];
        handleAITurn();
}


    /**
     * to be called in every move
     */
    private void updateState(){
        if(moveLimit<=nbrMoves) {
            state = BoardUI.GameState.END;
            System.out.println(nbrMoves);
            try {
                TimeUnit.SECONDS.sleep(4);
            }
            catch (Exception e) {
                System.out.println("Oops! Something went wrong!");
            }
        }

        if( state== BoardUI.GameState.AI_MOVE) {
            if (noOneMoved()){
                state = BoardUI.GameState.END;
                System.out.println("In game status:  mo one moved");
            }
            else {
                System.out.println(actualPlayer.getName()+ " skipped last move?: "+actualPlayer.getSkippedLastMove());
                //System.out.println("In game status:  someone did move moved");
                nextTurn();

                    state = BoardUI.GameState.AI_MOVE;
                    handleAITurn();

            }
           // debuggingPiecesUsed();
        }
        if (state== BoardUI.GameState.END){
            countPoints();
            System.out.println("THE GAME HAS ENDED");
        }
    }


    private void handleAITurn(){
         {
            System.out.println("handle");
            //So far this will only print the current piece into the board,
            //then the user will drag it manually into there
            //TODO make it so that no action can be taken while ai is taking its turn
            //TODO handle logic and animation for ai move
            Move move = null;
            if (actualPlayer instanceof GeneticPlayer) {
                System.out.println("move: " + nbrMoves);
                board.print();

                move = ((GeneticPlayer) actualPlayer).calculateMove(board);
                makeMove(move);
                moveAllowed(move.getPiece());

                System.out.println("number of moves"+nbrMoves);


            }
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
    private void countPointsPlayer(Player player){

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

        if (piecesPlaced==NUMBER_OF_PIECES_PER_PLAYER){//if I check if number of unused pieeces is 0, the size might be 1 when it should be 0 cause the deletion is done in the ui logic after the make move...but just maybe, I have not checked
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


    public void moveAllowed(Piece pieceRoot){
        //System.out.println("piece removed");
        actualPlayer.removePiece(pieceRoot.getLabel());
        //actualPlayer.getPiecesList().remove(pieceRoot);
        updateState();

    }

    /**
     * Handles turns flow
     *
     * If if it is player 1 turn, then next turn will correspond to player 2,
     after the last player, we go back to the first one
     */
    private void nextTurn(){

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



    //writes the piece into the board and adds it to the log
    public boolean makeMove(Move move){
        if(move.makeMove(board)) {
            movesLog.add(move);
            nbrMoves++;
            //updateState();
            return true;
        }
        return false;

    }


    /**
     * method used to initialize every player
     * players array given with 2 or 4 genetic players
     */
    public static Player[] initializePlayers(String[] playersName, int DIMENSION, Player[] players){
        Color[] colors = {Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE};
        for(int i=1; i<= playersName.length; i++){
            players[i-1].setColor(colors[i-1]);
            players[i-1].setName(playersName[i-1]);
        }
        Game.initializePlayerPieces(playersName, players);
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



    public static void main(String[] args) {
        //testing

        int dimension= 20;
        Player [] players = new Player[4];
        for (int i=0; i<4; i++){
            players[i]=new GeneticPlayer(i+1);
        }
        SimulatedGame simulation= new SimulatedGame(dimension, players);
        simulation.startSimulation();

    }


}
