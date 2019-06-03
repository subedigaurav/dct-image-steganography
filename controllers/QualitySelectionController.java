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
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.util.Duration;
import encoder.CaesarCipher;
import encoder.JPEGEncoder;

public class QualitySelectionController implements Initializable {

    @FXML
    private JFXSlider progressSlider;

    private static int quality = 80;
    private static JPEGEncoder encoder;
    private static String password;
    private static String plainMsg;

    private MainWindow mainWindow = new MainWindow();

    private void startEncode() throws IOException {
        // retrieve file path from the temporary txt file and send it to the encoder
        String imgPath;
        try (BufferedReader br = new BufferedReader(new FileReader(MainWindow.tempFile))) {
            imgPath = br.readLine();
        }

        File file = new File(imgPath);
        encodeImage(file);
    }

    private static void encodeImage(File file) throws IOException {
        double KB = file.length() / 1024;
        KB = Double.parseDouble(String.format("%.1f", KB));

        BufferedImage image = ImageIO.read(file);
        File dstFile = new File("D:/stegoImage.jpg");

        BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(dstFile));

        try (BufferedReader br = new BufferedReader(new FileReader(MainWindow.tempFile))) {
            br.readLine();
            password = br.readLine();
            plainMsg = br.readLine();
        }

        String scrambledMsg = CaesarCipher.encrypt(plainMsg, 2);

        encoder = new JPEGEncoder(quality, image, outStream, scrambledMsg, password);
        encoder.getYCbCr();
        encoder.writeHeaders();
        encoder.writeEOI();

        outStream.close();

        System.out.println("*** Message Hidden Successfuly ***");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void setQualityStartEncode(ActionEvent event) throws IOException {
        quality = (int) progressSlider.getValue();

        startEncode();

        Notifications notification = Notifications.create().title("Hiding Successful!")
                .text("Message Written Successfully.").position(Pos.BOTTOM_RIGHT).hideAfter(Duration.seconds(3))
                .position(Pos.BOTTOM_CENTER).hideCloseButton();
        notification.showInformation();
        goHome(event);
    }

    private void goHome(ActionEvent event) throws IOException {
        mainWindow.throwWindow("/fxml/mainView.fxml", event);
    }
}
