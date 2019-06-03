import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PasswordController implements Initializable {
    @FXML
    private JFXTextArea msgField;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private JFXPasswordField passwordField;

    private int maxCharacters = 7;
    private double progressValue = 0;
    private int maxMsgSize;
    private double progressValueBar = 0.00;
    private double updateVal;

    private MainWindow mainWindow = new MainWindow();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordField.setPromptText("Upto 8 Chars");
        passwordField.setOnKeyTyped(e -> {
            progressValue += 0.125f;
            progress.setProgress(progressValue);
            if (passwordField.getText().length() > maxCharacters) {
                System.out.println("Exceeded");
                System.out.println(passwordField.getText());
                e.consume();
            }
        });

        try {
            setMsgSizeMax();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(maxMsgSize);

        msgField.setPromptText("Max Message Size is " + maxMsgSize + " Characters");
        updateVal = maxMsgSize;
        updateVal = (1 / updateVal);

        msgField.setOnKeyTyped(e -> {
            progressValueBar += updateVal;
            progressBar.setProgress(progressValueBar);
            if (msgField.getText().length() > maxMsgSize - 1) {
                System.out.println("Max Message Length Exceeded");
                e.consume();
            }
        });
    }

    private void gotoEncoderMain(ActionEvent event) throws IOException {
        mainWindow.throwWindow("/fxml/qualitySelection.fxml", event);
    }

    private void setMsgSizeMax() throws IOException {
        String imgPath;
        try (BufferedReader br = new BufferedReader(new FileReader(new File("D:/stegoInfo.txt")))) {
            imgPath = br.readLine();
        }
        File file = new File(imgPath);
        BufferedImage image = ImageIO.read(file);
        int numMcUs = ((image.getWidth() * image.getHeight()) / 64);
        maxMsgSize = numMcUs * 2;
    }

    public void writePswrdMsg(ActionEvent event) throws IOException {
        // write password
        String password = passwordField.getText() + System.lineSeparator();
        Files.write(Paths.get("D:/stegoInfo.txt"), password.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);

        // write message
        String msg = msgField.getText() + System.lineSeparator();
        Files.write(Paths.get("D:/stegoInfo.txt"), msg.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
        // goto EncoderMain Window
        gotoEncoderMain(event);
    }

}
