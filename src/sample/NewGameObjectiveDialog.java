package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Charles on 24/10/2016.
 */
public class NewGameObjectiveDialog extends Dialog {

    /* - Dialog widgets - */
    private TextField textFieldGameObjectiveName;
    private ImageView imageView;
    private Button buttonImportImage;
    private Slider scaleBar;
    private FileChooser fileChooser;
    private File file;

    public NewGameObjectiveDialog() {
        // Set the dialog style.
        initStyle(StageStyle.UTILITY);
        // Set the button types.
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Instanciating the pane to place the widget.
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        // Initialize the dialog widgets.
        textFieldGameObjectiveName = new TextField();
        textFieldGameObjectiveName.setPromptText("Nom de l'obstacle");

        imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        buttonImportImage = new Button("Importer une image...");
        buttonImportImage.setOnMouseClicked(event -> chooseFile());

        // Params : Min, max, current value.
        scaleBar = new Slider(1, 2, 1);

        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        // Adding the widgets to the grid pane.
        gridPane.add(new Label("Nom de l'obstacle:"), 0, 0);
        gridPane.add(textFieldGameObjectiveName, 1, 0);
        gridPane.add(imageView, 1, 1, 1, 2);
        gridPane.add(buttonImportImage, 0, 2);
        gridPane.add(new Label("Taille de l'objectif"), 0, 3);
        gridPane.add(scaleBar, 0, 4);
        getDialogPane().setContent(gridPane);
    }

    public String getGameObjectiveName() {
        return textFieldGameObjectiveName.getText();
    }

    public Image getGameObjectiveImage() {
        return imageView.getImage();
    }

    public String getGameObjectiveImagePath() {
        return "file:///" + this.file.getAbsolutePath();
    }

    public double getGameObjectiveScale() {
        return scaleBar.getValue();
    }

    private void chooseFile() {
        this.file = fileChooser.showOpenDialog(getOwner());
        try {
            BufferedImage bufferedImage = ImageIO.read(this.file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(image);
        } catch (Exception ex) {
            imageView.setImage(null);
        }
    }


}

