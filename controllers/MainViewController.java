import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainViewController implements Initializable {
    @FXML
    private ImageView fbImg;

    @FXML
    private ImageView gitImg;

    @FXML
    private ColorAdjust cs;

    private MainWindow mainWindow = new MainWindow();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cs = new ColorAdjust();
        cs.setBrightness(1);
        gitImg.setEffect(cs);
        fbImg.setEffect(cs);
    }

    private Effect eff;

    @FXML
    public void gotoCoverSelection(ActionEvent event) throws IOException {
        mainWindow.throwWindow("/fxml/coverImageSelection.fxml", event);
    }

    @FXML
    public void gotoStegoSelection(ActionEvent event) throws IOException {
        mainWindow.throwWindow("/fxml/stegoImageSelection.fxml", event);
    }

    public void exitProgram() {
        System.exit(0);
    }

    @FXML
    public void btnChangeFB() {
        Image image = new Image(getClass().getResourceAsStream("/images/facebook.png"));
        eff = fbImg.getEffect();
        ColorAdjust cs = new ColorAdjust();
        cs.setSaturation(1);
        cs.setContrast(0.70);
        fbImg.setEffect(cs);
        fbImg.setImage(image);
    }

    @FXML
    public void btnChangeResetFB() {
        Image image = new Image(getClass().getResourceAsStream("/images/facebook.png"));
        fbImg.setEffect(eff);
        fbImg.setImage(image);
    }

    @FXML
    public void openFBPage() {
        try {
            String url = "https://www.facebook.com";
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }


    //Controls for GitHub Button
    @FXML
    public void btnChangeGIT() {
        Image image = new Image(getClass().getResourceAsStream("/images/github.png"));
        eff = gitImg.getEffect();
        ColorAdjust cs = new ColorAdjust();
        cs.setSaturation(1);
        cs.setContrast(.85);
        gitImg.setEffect(cs);
        gitImg.setImage(image);
    }

    @FXML
    public void btnChangeResetGIT() {
        Image image = new Image(getClass().getResourceAsStream("/images/github-logo.png"));
        gitImg.setEffect(eff);
        gitImg.setImage(image);
    }

    @FXML
    public void openGITPage() {
        try {
            String url = "https://github.com/subedigaurav/DCT-LSB-Image-Steganography";
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
