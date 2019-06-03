import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class PasswordInputController implements Initializable {
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
                e.consume();
            }
        });

        try {
            setMsgSizeMax();
        } catch (IOException e) {
            e.printStackTrace();
        }

        msgField.setPromptText("Max Message Size is " + maxMsgSize + " Characters");
        updateVal = maxMsgSize;
        updateVal = (1 / updateVal);

        msgField.setOnKeyTyped(e -> {
            progressValueBar += updateVal;
            progressBar.setProgress(progressValueBar);
            if (msgField.getText().length() > maxMsgSize - 1) {
                e.consume();
            }
        });
    }

    private void gotoEncoderMain(ActionEvent event) throws IOException {
        mainWindow.throwWindow("/fxml/qualitySelection.fxml", event);
    }

    private void setMsgSizeMax() throws IOException {
        String imgPath;
        try (BufferedReader br = new BufferedReader(new FileReader(MainWindow.tempFile))) {
            imgPath = br.readLine();
        }

        //read image file from its path in the temporary directory
        File file = new File(imgPath);
        BufferedImage image = ImageIO.read(file);

        // set the maxumum payload size value
        int numMcUs = ((image.getWidth() * image.getHeight()) / 64);
        maxMsgSize = numMcUs * 2;
    }

    // Write the password and message input by the user to the temporary file.
    public void writePswrdMsg(ActionEvent event) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(MainWindow.tempFile, true));

        // write password
        String password = passwordField.getText() + System.lineSeparator();
        writer.write(password);

        // write message
        String msg = msgField.getText() + System.lineSeparator();
        writer.write(msg);

        writer.close();

        // goto the encoder view
        gotoEncoderMain(event);
    }

}
