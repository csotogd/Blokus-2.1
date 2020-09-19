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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import menu.MenuTitle;

public class SettingsScreen extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    //each button contains a function that is run everytime it is pressed

    private Stage stage;

    private StackPane root = new StackPane();
    private VBox menuBox = new VBox(-5);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        root.setId("settings-screen-pane");
        Scene scene = new Scene(root, 800, 800);
        addContent();
        scene.setFill(Color.BLACK);
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setTitle("Blockus Settings Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;
    }

    private void addContent() {
        addTitle();
        double lineX = WIDTH / 2 - 100;
        double lineY = HEIGHT / 3 + 50;
        addMenu(lineX + 5, lineY + 5);

        startAnimation();
    }

    private void addTitle() {
        MenuTitle title = new MenuTitle("BLOCKUS:Settings");
        title.setTranslateX(WIDTH / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(HEIGHT / 3);
        root.getChildren().add(title);
    }

    private void addMenu(double x, double y) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
//        grid.setPadding(new Insets(25, 25, 25, 25));

        Text opt1 = new Text("First");
        Font font = Font.loadFont("file:res/fonts/PlayMeGames-Demo.otf", 20);
        opt1.setFont(font);
        grid.add(opt1, 0, 1);

        //this is how we manage choice box

        // string array
        String options1[] = { "OPT1", "OPT2", "OPT3", "OPT4" };

        // create a choiceBox
        ChoiceBox c1 = new ChoiceBox(FXCollections.observableArrayList(options1));

        grid.add(c1, 1, 1);


        //this is how we manage textfield boxes
        Label opt2 = new Label("Second");
        opt2.setFont(font);
        grid.add(opt2, 0, 2);

        TextField opt2TextField = new TextField();
        grid.add(opt2TextField, 1, 2);

        Label opt3 = new Label("Third");
        opt3.setFont(font);
        grid.add(opt3, 0, 3);

        TextField opt3TextField = new TextField();
        grid.add(opt3TextField, 1, 3);

        Label opt4 = new Label("Fourth");
        opt4.setFont(font);
        grid.add(opt4, 0, 4);

        TextField opt4TextField = new TextField();
        grid.add(opt4TextField, 1, 4);

        Button exitButton = new Button("Back to Menu");
        exitButton.setFont(font);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    new StartScreen().start(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        grid.add(exitButton,0,5);

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