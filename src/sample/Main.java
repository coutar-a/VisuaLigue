package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    private int WINDOWWIDTH = 1080;
    private int WINDOWHEIGHT = 675;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("VisuaLigue");

        // Getting size of user screen :
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double width = primaryScreenBounds.getWidth();
        double height = primaryScreenBounds.getHeight();

        primaryStage.setScene(new Scene(root, width - (width / 10) , height - (height / 10)));
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);
        primaryStage.show();

        Bridge bridge = Bridge.getInstance();
        bridge.ps = primaryStage;
        bridge.main = this;

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Bridge bridge = Bridge.getInstance();

                if (bridge.hasImportedOnce == true) {
                    event.consume();
                    bridge.fc.askConfirmationOverideActualSave();
                }
                else
                {
                    bridge.fc.startSavingOnExit();
                    event.consume();
                    System.exit(0);
                }


            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
