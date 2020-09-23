package GameBoard;

import DataBase.Piece;
import Tools.Vector2d;

//TEST
public class Board {

    public int[][] board;
    private int nbrPlayer;
    private final Vector2d DIMENSION = new Vector2d(20, 20);

    public Board(int nbrPlayer) {
        this.board = new int[DIMENSION.get_x()][DIMENSION.get_y()];
        this.nbrPlayer = nbrPlayer;

        createBoard();
    }

    public void createBoard() {
        for (int i = 0; i < DIMENSION.get_x(); i++) {
            for (int j = 0; j < DIMENSION.get_y(); j++) {
                board[i][j] = 0;
            }
        }
    }

    public boolean caseUsed(int col, int row) {
        if (board[col][row] != 0) {
            return true;
        }
        return false;
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
