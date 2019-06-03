import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class StegoImageSelectionController {

	@FXML
	JFXButton okBtn;

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
//			Files.write(Paths.get("D:/stegoInfo.txt"), filePath.getBytes(StandardCharsets.UTF_8),
//					StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			BufferedWriter writer = new BufferedWriter(new FileWriter(MainWindow.tempFile));
			writer.write(filePath);
			writer.close();

			// go to password input window//
			Parent encoderParent = FXMLLoader.load(getClass().getResource("/fxml/passwordVerify.fxml"));
			Scene encoderScene = new Scene(encoderParent);

			// This Gets The Scene Info
			window = (Stage) ((Node) event.getSource()).getScene().getWindow();

			encoderScene.setFill(Color.TRANSPARENT);
			window.setScene(encoderScene);
			window.show();
		}
	}
}
