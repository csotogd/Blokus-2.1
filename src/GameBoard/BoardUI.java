package GameBoard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//TEST
public class BoardUI extends Application {
    private final int BOARD_SIZE = 20;
    Parent gameBoard;

    public BoardUI(){
        BoardConstructor board = new BoardConstructor(4);

        this.gameBoard = drawBoard();
    }
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage.setTitle("Hello World");
        Scene scene = new Scene(root, 300, 275);
        stage.setScene(scene);
        stage.show();
    }

    public Parent drawBoard() {

        GridPane gameBoard = new GridPane();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {

                //if board[i][j] = smth, then paint that case in the smth color

                Rectangle tile = new Rectangle(50, 50);
                tile.setFill(Color.BURLYWOOD);
                tile.setStroke(Color.BLACK);

                gameBoard.add(tile, j, i);
            }
        }
        return gameBoard;
    }
}
