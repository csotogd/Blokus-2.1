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

import java.util.ArrayList;

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

    public Board(int[][] board){
        this.board=board;
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


    /**
     * used to be STATIC METHOD dedined in Corner, where the board was passed as a parameter,
     * Get all corners on the board of the player with starting position startingPosition
     * @param startingPosition starting position of the player
     * @return
     */
    public  ArrayList<Corner> getCorner( Vector2d startingPosition){
    Board board=this;
        ArrayList<Corner> corners = new ArrayList<>();
        if(board.board[startingPosition.get_y()][startingPosition.get_x()]==0){
            Vector2d adjacent=startingPosition.add(new Vector2d(1,1));
            if(board.inBoard(adjacent)) {
                corners.add(new Corner(startingPosition,adjacent));
                return corners;
            }
            adjacent=startingPosition.add(new Vector2d(-1,-1));
            if(board.inBoard(adjacent)) {
                corners.add(new Corner(startingPosition,adjacent));
                return corners;
            }
            adjacent=startingPosition.add(new Vector2d(1,-1));
            if(board.inBoard(adjacent)) {
                corners.add(new Corner(startingPosition,adjacent));
                return corners;
            }
            adjacent=startingPosition.add(new Vector2d(-1,1));
            if(board.inBoard(adjacent)) {
                corners.add(new Corner(startingPosition,adjacent));
                return corners;
            }
        }
        boolean[][] checked = new boolean[board.getDIMENSION().get_y()][board.getDIMENSION().get_x()];
        findCorners( checked,startingPosition,corners,board.board[startingPosition.get_y()][startingPosition.get_x()]);

        return corners;
    }




    /**
     * helper method to find corners and add them to the arraylist
     * @param checked position on the board that is already checked
     * @param position current coordinates
     * @param corners arraylist that contains the corners
     * @param player id of the player
     */
//TODO : not use inboard function as it takes twice as many evaluations as necessary
    private void findCorners(boolean[][] checked, Vector2d position, ArrayList<Corner> corners, int player) {
        Board board =this;
        if(board.inBoard(position) && !checked[position.get_y()][position.get_x()]){
            checked[position.get_y()][position.get_x()]=true;
            if(board.board[position.get_y()][position.get_x()]==player){
                boolean top=true, down=true, left=true, right=true; //NOT occupied by own blocks ie"free"
                if(board.inBoard(position.add(new Vector2d(0,-1))) && board.board[position.get_y()-1][position.get_x()]==player){
                    top=false;
                    findCorners( checked, position.add(new Vector2d(0,-1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(0,1))) && board.board[position.get_y()+1][position.get_x()]==player){
                    down=false;
                    findCorners( checked, position.add(new Vector2d(0,1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(-1,0))) && board.board[position.get_y()][position.get_x()-1]==player){
                    left=false;
                    findCorners( checked, position.add(new Vector2d(-1,0)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(1,0))) && board.board[position.get_y()][position.get_x()+1]==player){
                    right=false;
                    findCorners( checked, position.add(new Vector2d(1,0)), corners, player);
                }

                Corner current=null;//current corner being evaluated

                if(top && left
                        && board.inBoard(new Vector2d(position.get_x()-1, position.get_y()-1))
                        && board.board[position.get_y()-1][position.get_x()-1]==0){
                    current = new  Corner(position,position.add(new Vector2d(-1,-1)));
                    corners.add(current);
                }
                if(top && right
                        && board.inBoard(new Vector2d(position.get_x()+1, position.get_y()-1))
                        && board.board[position.get_y()-1][position.get_x()+1]==0){
                    if(current==null) {
                        current = new Corner(position,position.add(new Vector2d(1,-1)));
                        corners.add(current);
                    }
                    else {
                        current.addAdjacent(position.add(new Vector2d(1,-1)));
                    }
                }
                if(down && left
                        && board.inBoard(new Vector2d(position.get_x()-1, position.get_y()+1))
                        && board.board[position.get_y()+1][position.get_x()-1]==0){
                    if(current==null) {
                        current = new Corner(position,position.add(new Vector2d(-1,1)));
                        corners.add(current);
                    }
                    else {
                        current.addAdjacent(position.add(new Vector2d(-1,1)));
                    }
                }
                if(down && right
                        && board.inBoard(new Vector2d(position.get_x()+1, position.get_y()+1))
                        && board.board[position.get_y()+1][position.get_x()+1]==0){
                    if(current==null) {
                        current = new Corner(position,position.add(new Vector2d(1,1)));
                        corners.add(current);
                    }
                    else {
                        current.addAdjacent(position.add(new Vector2d(1,1)));
                    }
                }

                //now check for corners
                if(board.isOccupiedBy(position.add(new Vector2d(1,1)),player)) findCorners( checked,position.add(new Vector2d(1,1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(-1,1)),player)) findCorners(checked,position.add(new Vector2d(-1,1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(-1,-1)),player)) findCorners(checked,position.add(new Vector2d(-1,-1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(1,-1)),player)) findCorners(checked,position.add(new Vector2d(1,-1)),corners,player);

            }

        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public Vector2d getBoardDimension(){
        return this.DIMENSION;
    }
}
