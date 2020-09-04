package GameBoard;

//TEST
public class BoardConstructor {

    private int[][]board;
    private int nbrPlayer;
    private final int BOARD_LENGTH = 20;

    public BoardConstructor(int nbrPlayer){
        this.board = new int[BOARD_LENGTH][BOARD_LENGTH];
        this.nbrPlayer = nbrPlayer;

        createBoard();
    }

    public void createBoard(){
        for (int i = 0; i < BOARD_LENGTH; i++) {
            for (int j = 0; j < BOARD_LENGTH; j++) {
                board[i][j]=0;
            }
        }
    }
}
