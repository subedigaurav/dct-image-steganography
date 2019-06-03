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

        // This Gets The Scene Info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File imageFile = fileChooser.showOpenDialog(window);
        if (imageFile != null) {
            window.hide();

            // write file path to the txt file
            String filePath = imageFile.getAbsolutePath() + System.lineSeparator();
//            Files.write(Paths.get("D:/stegoInfo.txt"), filePath.getBytes(StandardCharsets.UTF_8),
//                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            BufferedWriter writer = new BufferedWriter(new FileWriter(MainWindow.tempFile));
            writer.write(filePath);
            writer.close();

            // go to password input window
            mainWindow.throwWindow("/fxml/PasswordInput.fxml", event);
        }
    }
}
