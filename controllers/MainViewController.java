import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.sun.corba.se.spi.activation.ActivatorOperations;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainViewController implements Initializable {
    @FXML
    private Button dateButton;

    @FXML
    private Button exitBtn;

    @FXML
    private Button encodeBtn;

    @FXML
    private TextField myTextField;

    @FXML
    private ImageView fbImg;

    @FXML
    private ImageView gitImg;

    @FXML
    private Button decodeBtn;

    private ColorAdjust cs;

    @FXML
    private Text marqueeText;

    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cs = new ColorAdjust();
        cs.setBrightness(1);
        gitImg.setEffect(cs);
        fbImg.setEffect(cs);
    }

    private Effect eff;

    private void throwWindow(String fxmlFile, ActionEvent event) throws IOException {
        Parent coverSelectionStage = FXMLLoader.load(getClass().getResource(fxmlFile));
        Scene coverSelectionScene = new Scene(coverSelectionStage);

        //This Gets The Scene Info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        coverSelectionScene.setFill(Color.TRANSPARENT);
        window.setScene(coverSelectionScene);
        window.centerOnScreen();
        window.show();
    }

    @FXML
    public void gotoCoverSelection(ActionEvent event) throws IOException {
        throwWindow("/fxml/coverImageSelection.fxml", event);
    }

//   @FXML
//   public void gotoDecoderView(ActionEvent event) throws IOException {
//		Parent decoderParent=FXMLLoader.load(getClass().getResource("decoderView.fxml"));
//		Scene decoderScene=new Scene(decoderParent);
//		
//		//This Gets The Scene Info
//		Stage window=(Stage) ((Node) event.getSource()).getScene().getWindow();
//		decoderScene.setFill(Color.TRANSPARENT);
//		window.setScene(decoderScene);
//		window.centerOnScreen();
//		window.show();
//	}

    @FXML
    public void gotoStegoSelection(ActionEvent event) throws IOException {
        Parent decoderParent = FXMLLoader.load(getClass().getResource("/fxml/stegoImageSelection.fxml"));
        Scene decoderScene = new Scene(decoderParent);

        //This Gets The Scene Info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        decoderScene.setFill(Color.TRANSPARENT);
        window.setScene(decoderScene);
        window.centerOnScreen();
        window.show();
    }

    // When user click on myButton
    // this method will be called.
    public void exitProgram(ActionEvent event) {
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
