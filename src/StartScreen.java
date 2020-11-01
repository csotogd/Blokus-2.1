import DataBase.Data;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import menu.MenuItem;
import menu.MenuTitle;

import java.util.Arrays;
import java.util.List;

public class StartScreen extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;


    //each button contains a function that is run everytime it is pressed

    private Stage stage;

    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("Play", () -> {
                new Game().start(stage);
            }),
            new Pair<String, Runnable>("Settings", () -> {
                try {
                    new SettingsScreen().start(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),

            new Pair<String, Runnable>("Exit to Desktop", Platform::exit)
    );

    private StackPane root = new StackPane();
    private VBox menuBox = new VBox(-5);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        root.setId("start-screen-pane");
        Background background = Data.createBackGround();
        root.setBackground(background);
        Scene scene = new Scene(root, 800, 800);
        addContent();
        scene.setFill(Color.BLACK);
        primaryStage.setTitle("Blokus Start Screen");
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

    private void addBlur(){
        BoxBlur bb = new BoxBlur();
        bb.setWidth(5);
        bb.setHeight(5);
        bb.setIterations(3);

        root.setEffect(bb);
    }

    private void addTitle() {
        MenuTitle title = new MenuTitle("BLOKUS");
        title.setTranslateX(WIDTH / 2. - title.getTitleWidth() / 2);
        title.setTranslateY(HEIGHT / 3.);
        root.getChildren().add(title);
    }

    private void addMenu(double x, double y) {
        menuBox.setTranslateX(x);
        menuBox.setTranslateY(y);
        menuData.forEach(data -> {
            MenuItem item = new MenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);
        });

        root.getChildren().add(menuBox);
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