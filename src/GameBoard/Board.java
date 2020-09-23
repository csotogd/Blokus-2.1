package GameBoard;

import DataBase.Piece;
import Player.Player;
import Tools.Vector2d;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

//TEST
public class Board extends Pane {

    private final int BOARD_SIZE = 20;
    private final int CELL_SIZE = 25;
    public int[][] board;
    private final Vector2d DIMENSION = new Vector2d(20, 20);
    private GridPane gameBoardRep;
    Player[] players;
    Pane allPieces;

    public Board(Player[] players,FlowPane allPieces) {
        this.board = new int[DIMENSION.get_x()][DIMENSION.get_y()];
        this.players = players;
        this.allPieces = allPieces;
        createBoard();
    }

    public void createBoard() {
        for (int i = 0; i < DIMENSION.get_x(); i++) {
            for (int j = 0; j < DIMENSION.get_y(); j++) {
                board[i][j] = 0;
            }
        }
        paint();
    }

    public void paint() {
        //Clear previous cells
        getChildren().clear();
        gameBoardRep = new GridPane();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {

                Rectangle tile = new Rectangle(CELL_SIZE, CELL_SIZE);
                tile.setFill(paintColor(i,j));
                tile.setStrokeWidth(2.0);
                tile.setStroke(Color.BLACK);

                gameBoardRep.add(new StackPane(tile), j, i);
            }
        }
        getChildren().add(gameBoardRep);

        System.out.println("updated");
    }

    public  Color paintColor(int col, int row){
        if(board[col][row]==0){
            return Color.WHITE;
        }else if(board[col][row]==1){
            return Color.RED;
        }else if(board[col][row]==2){
            return Color.YELLOW;
        }else if(board[col][row]==3){
            return Color.GREEN;
        }else if(board[col][row]==4){
            return Color.BLUE;
        }
        return null;
    }

    public void updatePieces(){
        if(allPieces==null){
            allPieces = new FlowPane();
        }
        allPieces.getChildren().clear();

        for (Player player:players) {
            int pieceCounter = 0;
            for (Piece pieceLeft:player.getPiecesList()) {
                if(!pieceLeft.isUsed()){
                    allPieces.getChildren().add(new Text(Integer.toString(++pieceCounter)));
                    allPieces.getChildren().add(drawPiece(pieceLeft.getShape(),player.getColor(),pieceLeft));
                    allPieces.getChildren().add(new Text(" "));
                }
            }
            System.out.println(pieceCounter);
        }



    }
    public Parent drawPiece(int [][] pieceTable, Color playerColor, Piece pieceRoot) {
        GridPane piece = new GridPane();
        for (int i = 0; i < pieceTable.length; i++) {
            for (int j = 0; j < pieceTable[i].length; j++) {
                Rectangle tile = new Rectangle(CELL_SIZE / 2, CELL_SIZE / 2);
                if (pieceTable[i][j] == 1) {
                    tile.setFill(playerColor);
                    tile.setStrokeWidth(2.0);
                    tile.setStroke(Color.BLACK);
                    piece.add(new StackPane(tile), i, j);
                } else {
                    tile.setFill(Color.TRANSPARENT);
                    piece.add(new StackPane(tile), i, j);
                }

            }
        }
        return piece;
    }

    public Vector2d getDIMENSION() {
        return DIMENSION;
    }

    public boolean inBoard(Vector2d position){
        if(position.get_y()<0||position.get_x()<0||
        position.get_x()>=DIMENSION.get_x()||
        position.get_y()>=DIMENSION.get_y()) return false;
        return true;
    }

    public boolean isOccupiedBy(Vector2d position, int player){
        if(position.get_y()<0||position.get_x()<0||
                position.get_x()>=DIMENSION.get_x()||
                position.get_y()>=DIMENSION.get_y()) return false;
        if(board[position.get_y()][position.get_x()]==player) return true;
        return false;
    }
}
