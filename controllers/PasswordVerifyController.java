import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;

import decoder.JPEGDecoder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import encoder.CaesarCipher;

public class PasswordVerifyController {

	@FXML
	private JFXPasswordField passwordField;

	@FXML
	JFXButton okBtn;

	private static ShortBuffer[] sb = new ShortBuffer[3];
	private static PrintWriter writer;
	private static short[] zzS = new short[64];
	private static String message;
	private static StringBuilder outputMsg;
	private static String password;
	private static JPEGDecoder decode;
	private static int patterns[][] = { { 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40 },
			{ 40, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25 },
			{ 28, 27, 26, 25, 29, 30, 31, 32, 33, 34, 35, 36, 40, 39, 38, 37 },
			{ 25, 26, 27, 28, 36, 35, 34, 33, 32, 31, 30, 29, 37, 38, 39, 40 } };

	public static int checkPass(String inputPassword) throws IOException {
		File file = new File("D:\\stegoImage.jpg");
		InputStream istream = new FileInputStream(file);
		decode = new JPEGDecoder(istream);
		decode.startDecode();

		password = decode.getPass();

		Files.write(Paths.get("D:/decoder_info.txt"), password.getBytes(StandardCharsets.UTF_8),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

		return inputPassword.compareTo(password);
	}

	public void checkPassword(ActionEvent event) throws IOException {
		String ipPass = passwordField.getText();
		int result = checkPass(ipPass);
		if (result == 0) {
			decodeMsg();
			gotoDecoderView(event);
		} else {
			Notifications notifications = Notifications.create().title("Wrong Password!")
					.text("The password does not match with the original password :(").position(Pos.BOTTOM_CENTER)
					.hideAfter(Duration.seconds(1.5));
			notifications.showError();
			goHome(event);
		}
	}

	public static void decodeMsg() throws IOException {
		writer = new PrintWriter(new File("D:\\dct_data.txt"));
		InputStream istream = new FileInputStream(new File("D:\\stegoImage.jpg"));
		decode = new JPEGDecoder(istream);
		decode.startDecode();

		message = grabMessage();
		System.out.println(message);
		// write message
		Files.write(Paths.get("D:/decoder_info.txt"), message.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	}

	private static String grabMessage() throws IOException {
		for (int i = 0; i < sb.length; i++) {
			sb[i] = ShortBuffer.allocate(decode.getImageHeight() * decode.getImageHeight());
		}

		// the data is in shortbuffer sb[i] for each color component
		decode.decodeDCTCoeffs(sb, decode.getNumMCURows());
		for (int i = 0; i < sb.length; i++) {
			sb[i].position(0);
			for (int j = 0; j < sb[i].remaining(); j++) {
				writer.print(sb[i].get(j) + " ");
			}
			writer.println();
		}

		// initialize the string of message
		outputMsg = new StringBuilder();
		int mcuid = 0;
		int patternClass;
		int remainMsgBits = decode.getMsgLength() * 8;
		int reqmscu = (int) Math.ceil(remainMsgBits / 16.0);

		for (int i = 0; i < reqmscu; i++) {
			sb[1].get(zzS);

			// select pattern class
			patternClass = mcuid % 4;
			mcuid++;

			// decode data from zzS
			int b = 0, cnt = 0;
			for (int j = 0; j < 16 && remainMsgBits != 0; j++) {
				b = b << 1;
				b = (b + (zzS[patterns[patternClass][j]] & 0x1));
				cnt++;

				if (cnt == 8) {
					cnt = 0;
					if (b == 0)
						break;
					outputMsg.append((char) b);
					b = 0;
				}
			}
		}
		String encMsg = outputMsg.toString();
		return (CaesarCipher.decrypt(encMsg, 2));
	}

	public void gotoDecoderView(ActionEvent event) throws IOException {
		Parent encoderParent = FXMLLoader.load(getClass().getResource("/fxml/decoderView.fxml"));
		Scene encoderScene = new Scene(encoderParent);

		// This Gets The Scene Info
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		encoderScene.setFill(Color.TRANSPARENT);
		window.setScene(encoderScene);
		window.centerOnScreen();
		window.show();
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
