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
    public GridPane gameBoardRep;
    Player[] players;

    public Board(Player[] players) {
        this.board = new int[DIMENSION.get_x()][DIMENSION.get_y()];
        this.players = players;
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
    public Vector2d getBoardDimension(){
        return this.DIMENSION;
    }
}
