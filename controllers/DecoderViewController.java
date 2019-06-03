import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class DecoderViewController {
    @FXML
    private TextArea messageArea;

    private MainWindow mainWindow = new MainWindow();

    public void populatePassword() throws IOException {

        //read the decoded message from the temporary file
        BufferedReader br = new BufferedReader(new FileReader(MainWindow.tempFile));
        String message = br.readLine();
        br.close();

        // populate the password in the message area
        messageArea.setText(message);
    }

    public void goHome(ActionEvent event) throws IOException {
        mainWindow.throwWindow("/fxml/mainView.fxml", event);
    }
}
