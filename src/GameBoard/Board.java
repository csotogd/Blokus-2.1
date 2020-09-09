package GameBoard;

import DataBase.Piece;
import Tools.Vector2d;

//TEST
public class Board {

    public int[][]board;
    private int nbrPlayer;
    private final Vector2d DIMENSION = new Vector2d(20,20);

    public Board(int nbrPlayer){
        this.board = new int[DIMENSION.get_x()][DIMENSION.get_y()];
        this.nbrPlayer = nbrPlayer;

        createBoard();
    }

    public void createBoard(){
        for (int i = 0; i < DIMENSION.get_x(); i++) {
            for (int j = 0; j < DIMENSION.get_y(); j++) {
                board[i][j]=0;
            }
        }
        board[0][0]=1;
    }

    public boolean caseUsed(int col, int row){
        if(board[col][row]!=0){
            return true;
        }
        return false;
    }

    /**
     *
     * @param piece
     * @param coordinates of the top-left corner of the piece shape
     * @return
     */
    public boolean moveISAllowed(Piece piece, Vector2d coordinates){
        /*
         *  For a move to be allowed, the following 3 conditions have to be TRUE
         * 1. every "block" of the piece should be occupying an empty board space
         * 2. There has to be a piece touching the corner of at least ONE of the blocks
         * 3. NONE of the blocks is in contact with a piece of the same player in a none- CORNER context
         *
         */
        boolean emptyspace=true;
        boolean cornerContact=true;
        boolean noDirectContact=true;





        return emptyspace && cornerContact && noDirectContact;
    }
}

