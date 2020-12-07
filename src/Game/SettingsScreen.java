package Game;

import DataBase.Data;
import Game.StartScreen;
import javafx.animation.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import menu.MenuTitle;

public class SettingsScreen extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private String[] playersName;

    //each button contains a function that is run everytime it is pressed

    private Stage stage;

    private StackPane root = new StackPane();
    private VBox menuBox = new VBox(-5);

    @Override
    public void start(Stage primaryStage) throws Exception {
        root.setId("settings-screen-pane");
        Background background = Data.createBackGround();
        root.setBackground(background);
        Scene scene = new Scene(root, 800, 800);
        addContent();
        scene.setFill(Color.BLACK);
        //TODO fix issues with the settings
        primaryStage.setTitle("Blokus Settings Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;
    }

    private void addContent() {
        addTitle();
        double lineX = WIDTH / 2. - 100;
        double lineY = HEIGHT / 3. + 50;
        addMenu(lineX + 5, lineY + 5);

        startAnimation();
    }

    private void addTitle() {
        MenuTitle title = new MenuTitle("BLOKUS:Settings");
        title.setTranslateX(WIDTH / 2. - title.getTitleWidth() / 2);
        title.setTranslateY(HEIGHT / 3.);
        root.getChildren().add(title);
    }

    private void addMenu(double x, double y) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
//        grid.setPadding(new Insets(25, 25, 25, 25));
        Text opt1 = new Text("How many players :");
        grid.add(opt1, 0, 1);

        //this is how we manage choice box

        // string array
        String options1[] = {"2 Players", "4 Players" };

        // create a choiceBox
        ChoiceBox c1 = new ChoiceBox(FXCollections.observableArrayList(options1));
        c1.getSelectionModel().select(1);

        grid.add(c1, 1, 1);


        TextField opt2TextField = new TextField("");
        TextField opt3TextField = new TextField("");
        TextField opt4TextField = new TextField("");
        TextField opt5TextField = new TextField("");

        Text opt2 = new Text("Player 1 name :");
        Text opt3 = new Text("Player 2 name :");
        Text opt4 = new Text("Player 3 name :");
        Text opt5 = new Text("Player 4 name :");

        String optionsP[] = {"Human Player", "Monte Carlo Player", "Genetic Player","MiniMax Player","GAMCplayer" };

        grid.add(opt2, 0, 2);

        grid.add(opt2TextField, 1, 2);

        // create a choiceBox
        ChoiceBox c1P1 = new ChoiceBox(FXCollections.observableArrayList(optionsP));
        c1P1.getSelectionModel().select(0);
        grid.add(c1P1, 2, 2);

        grid.add(opt3, 0, 3);

        grid.add(opt3TextField, 1, 3);

        // create a choiceBox
        ChoiceBox c1P2 = new ChoiceBox(FXCollections.observableArrayList(optionsP));
        c1P2.getSelectionModel().select(0);
        grid.add(c1P2, 2, 3);

        grid.add(opt4, 0, 4);

        grid.add(opt4TextField, 1, 4);

        // create a choiceBox
        ChoiceBox c1P3 = new ChoiceBox(FXCollections.observableArrayList(optionsP));
        c1P3.getSelectionModel().select(0);
        grid.add(c1P3, 2, 4);

        grid.add(opt5, 0, 5);

        grid.add(opt5TextField, 1, 5);

        // create a choiceBox
        ChoiceBox c1P4 = new ChoiceBox(FXCollections.observableArrayList(optionsP));
        c1P4.getSelectionModel().select(0);
        grid.add(c1P4, 2, 5);

        c1.setOnAction(event -> {
            if(c1.getSelectionModel().getSelectedItem().equals("4 Players")){
                opt4.setVisible(true); opt5.setVisible(true);opt4TextField.setVisible(true);opt5TextField.setVisible(true);
                c1P3.setVisible(true); c1P4.setVisible(true);
            }else{
                opt4.setVisible(false); opt5.setVisible(false);opt4TextField.setVisible(false);opt5TextField.setVisible(false);
                c1P3.setVisible(false); c1P4.setVisible(false);
            }
        });

        Text opt6 = new Text("Board dimension :");
        grid.add(opt6, 0, 7);
        TextField opt6X = new TextField("");
        grid.add(opt6X, 1, 7);

        Button exitButton = new Button("Back to Menu");
        exitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    if(opt6X.getText().equals("")){
                        Data.setDIMENSION(20);
                    }else{
                        Data.setDIMENSION(Integer.parseInt(opt6X.getText()));
                    }
                    if(c1.getSelectionModel().getSelectedItem().equals("2 Players")) {
                        playersName = new String[2];

                        if(opt2TextField.getText().equals("")){
                            playersName[0] = "PLAYER 1";
                        }else{
                            playersName[0] = opt2TextField.getText();
                        }

                        if(opt3TextField.getText().equals("")){
                            playersName[1] = "PLAYER 2";
                        }else{
                            playersName[1] = opt3TextField.getText();
                        }
                        Data.setDIMENSION(14);

                    }else{
                        playersName = new String[4];
                        if(opt2TextField.getText().equals("")){
                            playersName[0] = "PLAYER 1";
                        }else{
                            playersName[0] = opt2TextField.getText();
                        }

                        if(opt3TextField.getText().equals("")){
                            playersName[1] = "PLAYER 2";
                        }else{
                            playersName[1] = opt3TextField.getText();
                        }

                        if(opt4TextField.getText().equals("")){
                            playersName[2] = "PLAYER 3";
                        }else{
                            playersName[2] = opt4TextField.getText();
                        }

                        if(opt5TextField.getText().equals("")){
                            playersName[3] = "PLAYER 4";
                        }else{
                            playersName[3] = opt5TextField.getText();
                        }
                    }
                    Data.setPlayersName(playersName);
                    Data.setPlayerTypes(new String[]{(String) c1P1.getSelectionModel().getSelectedItem(), (String) c1P2.getSelectionModel().getSelectedItem(),
                            (String) c1P3.getSelectionModel().getSelectedItem(), (String) c1P4.getSelectionModel().getSelectedItem()});
                    new StartScreen().start(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        grid.add(exitButton,0,9);
        exitButton.setTranslateX(80);

        root.getChildren().add(grid);
    }

    private void startAnimation() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1));
        st.setToY(1);
        st.setOnFinished(e -> {

            for (int i = 0; i < menuBox.getChildren().size(); i++) {
                Node n = menuBox.getChildren().get(i);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
                tt.setToX(0);
                tt.setOnFinished(e2 -> n.setClip(null));
                tt.play();
            }
        });
        st.play();
    }

}