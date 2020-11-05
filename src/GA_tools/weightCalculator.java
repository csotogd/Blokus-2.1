package GA_tools;

import DataBase.Piece;
import GameBoard.Board;
import GameBoard.BoardUI;
import Player.Player;
import Game.Game;
import Tools.Vector2d;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Player.*;

/*many games will be simulated at once*/
public class weightCalculator {


    public static void main(String[] args) {
        /*
         * //WE need to
         *
         * 1. Reproducing winners by altering weights
         * 2. introduce mutations
         * 3. run the algorithm several times
         *
         *
         * */



        //to create a new game first we need to define new genetic players
        Player[] players = new Player[5];
        GeneticPlayer player1= new GeneticPlayer(1);
        float player1Weights[]= {0.132f, 0.4745f, 0.8537f, 0.67319f, 0.0489f};
        players[0]=player1;
        //we do that for every player....


        int DIMENSION=20;
        SimulatedGame simulation= new SimulatedGame(DIMENSION, players);
        simulation.simulate();
    //we crete and simulate a new game between those players
        Player winner=simulation.getWinner();

        //if we want to create a new game we repeat the proccess.
        //knowing these, we can write a ga algorithm.



    }


}
