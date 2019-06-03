import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainWindow extends Application {

    public static File tempFile;

    @Override
    public void start(Stage primaryStage) throws IOException {
        createTempFile();

        // Read fxml file and draw interface.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/logIn.fxml"));
        Parent mainWindow = loader.load();

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Scene mainScene = new Scene(mainWindow);
        mainScene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    // LAUNCH THE MAIN APPLICATION
    public static void main(String[] args){launch(args);}


    /* Some Extra Functions */
    private Stage genNewStage(Parent parent, ActionEvent event) {
        // This Gets The Scene Info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene mainScene = new Scene(parent);
        mainScene.setFill(Color.TRANSPARENT);
        window.setScene(mainScene);
        window.centerOnScreen();

        // return the window
        return window;
    }

    public void throwWindow(String fxmlFile, ActionEvent event) throws IOException {
        Parent mainParent = FXMLLoader.load(getClass().getResource(fxmlFile));

        // This Gets The Scene Info
        Stage window = genNewStage(mainParent, event);
        window.show();
    }

    //create the necessary temporary file
    private void createTempFile() throws IOException{
        tempFile = File.createTempFile("stego", "tmp");
        tempFile.deleteOnExit();
    }

}