import java.io.*;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class CoverImageSelectionController {

    @FXML
    JFXButton okBtn;

    private MainWindow mainWindow = new MainWindow();

    public void viewSelectionDialog(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Cover File");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Still JPEG Files", "*.jpg", "*.jpeg"));

        // get the scene info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File imageFile = fileChooser.showOpenDialog(window);
        if (imageFile != null) {
            window.hide();

            // write cover image's absolute file path to the temporary file
            String filePath = imageFile.getAbsolutePath() + System.lineSeparator();
            BufferedWriter writer = new BufferedWriter(new FileWriter(MainWindow.tempFile));
            writer.write(filePath);
            writer.close();

            // go to password input window
            mainWindow.throwWindow("/fxml/PasswordInput.fxml", event);
        }
    }
}
