import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DecoderViewController {

	@FXML
	private Button homeBtn;

	@FXML
	private TextArea messageArea;

	private String message;

	public void populatePassword() throws IOException {
		String fileName = "D:\\decoder_info.txt";
		File file = new File(fileName);
		FileReader fr = new FileReader(file);
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(fr);
		message = br.readLine();
		messageArea.setText(message);

		// flush file
		FileWriter fw = new FileWriter(file, false);
		fw.flush();
		fw.close();
	}

	public void goHome(ActionEvent event) throws IOException {
		Parent homeStage = FXMLLoader.load(getClass().getResource("/fxml/mainView.fxml"));
		Scene homeScene = new Scene(homeStage);

		// This Gets The Scene Info
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		homeScene.setFill(Color.TRANSPARENT);
		window.setScene(homeScene);
		window.show();
	}
}
