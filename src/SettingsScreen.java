import DataBase.Data;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
        Background background = createBackGround();
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

    public Background createBackGround(){
        Image image = new Image("https://images.hdqwalls.com/wallpapers/simple-gray-background-4k-br.jpg",800,800,false,true);

        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);

        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);

        Background background = new Background(backgroundImage);
        return background;
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

        grid.add(c1, 1, 1);


        TextField opt2TextField = new TextField("");
        TextField opt3TextField = new TextField("");
        TextField opt4TextField = new TextField("");
        TextField opt5TextField = new TextField("");
        c1.setOnAction(event -> {
            if(c1.getSelectionModel().getSelectedItem().equals("4 Players")){
                //this is how we manage textfield boxes
                Text opt2 = new Text("Player 1 name :");
                grid.add(opt2, 0, 2);

                grid.add(opt2TextField, 1, 2);

                Text opt3 = new Text("Player 2 name :");
                grid.add(opt3, 0, 3);

                grid.add(opt3TextField, 1, 3);

                Text opt4 = new Text("Player 3 name :");
                grid.add(opt4, 0, 4);

                grid.add(opt4TextField, 1, 4);

                Text opt5 = new Text("Player 4 name :");
                grid.add(opt5, 0, 5);

                grid.add(opt5TextField, 1, 5);
            }else{
                //this is how we manage textfield boxes
                Text opt2 = new Text("Player 1 name :");
                grid.add(opt2, 0, 2);

                grid.add(opt2TextField, 1, 2);

                Text opt3 = new Text("Player 2 name :");
                grid.add(opt3, 0, 3);

                grid.add(opt3TextField, 1, 3);
            }
        });


        Button exitButton = new Button("Back to Menu");
        exitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {

                    if(c1.getSelectionModel().getSelectedItem().equals("2 Players")) {
                        playersName = new String[2];
                        playersName[0] = opt2TextField.getText();
                        playersName[1] = opt3TextField.getText();
                    }else{
                        playersName = new String[4];
                        playersName[0] = opt2TextField.getText();
                        playersName[1] = opt3TextField.getText();
                        playersName[2] = opt4TextField.getText();
                        playersName[3] = opt5TextField.getText();
                    }
                    Data.setPlayersName(playersName);

                    new StartScreen().start(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        grid.add(exitButton,0,6);

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