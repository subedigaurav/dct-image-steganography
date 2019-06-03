import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import encoder.CaesarCipher;
import encoder.JPEGEncoder;

public class QualitySelectionController implements Initializable {

	@FXML
	private JFXButton okBtn;

	@FXML
	private JFXSlider progressSlider;

	public static int quality = 80;
	private static JPEGEncoder encoder;
	static BufferedOutputStream outStream;
	private static String password;
	private static String plainMsg;

	public void startEncode() throws IOException {
		// retrieve file path from the temporary txt file and send it to the encoder
		String imgPath;
		try (BufferedReader br = new BufferedReader(new FileReader(new File("D:/stegoInfo.txt")))) {
			imgPath = br.readLine();
			br.close();
		}
		File file = new File(imgPath);
		encodeImage(file);
	}

	public static void encodeImage(File file) throws IOException {
		double fileSize = file.length();
		double KB = fileSize / 1024;
		KB = Double.parseDouble(String.format("%.1f", KB));
		System.out.println("Size of Original Image(in KB):" + KB);
		BufferedImage image = ImageIO.read(file);
		System.out.println("Now Encoding");
		File dstFile = new File("D:\\stegoImage.jpg");
		BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(dstFile));

		try (BufferedReader br = new BufferedReader(new FileReader(new File("D:/stegoInfo.txt")))) {
			br.readLine();
			password = br.readLine();
			plainMsg = br.readLine();
		}
		System.out.println(password);
		System.out.println(plainMsg);
		String scrambledMsg = CaesarCipher.encrypt(plainMsg, 2);
		encoder = new JPEGEncoder(quality, image, outStream, scrambledMsg, password);

		encoder.getYCbCr();
		encoder.writeHeaders();
		encoder.writeEOI();
		outStream.close();
		System.out.println("Message Written Successfuly...");

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	void setQualityStartEncode(ActionEvent event) throws IOException {
		quality = (int) progressSlider.getValue();
		System.out.println(quality);
		startEncode();
		Notifications notification = Notifications.create().title("Hiding Successful!")
				.text("Message Written Successfully.").position(Pos.BOTTOM_RIGHT).hideAfter(Duration.seconds(3))
				.position(Pos.BOTTOM_CENTER).hideCloseButton();
		notification.showInformation();
		goHome(event);
	}

	public void goHome(ActionEvent event) throws IOException {
		Parent homeStage = FXMLLoader.load(getClass().getResource("/fxml/mainView.fxml"));
		Scene homeScene = new Scene(homeStage);

		// This Gets The Scene Info
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		homeScene.setFill(Color.TRANSPARENT);
		window.setScene(homeScene);
		window.centerOnScreen();
		window.show();
	}
}
