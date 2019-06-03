import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import dao.UserDao;
import dto.User;
import util.DbUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class LoginController implements Initializable {
    @FXML
    TabPane tabPane;

    @FXML
    private ImageView userImg;

    @FXML
    private ImageView lockImg;

    @FXML
    private JFXTextField user;

    @FXML
    private JFXPasswordField password;

    @FXML
    JFXButton loginBtn;

    @FXML
    JFXButton signUpBtn;

    private MainWindow mainWindow = new MainWindow();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("INITIALIZED Login Screen!");
        try {
            DbUtil.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // exit the program
    public void cancelBtnActn() {
        System.out.println("EXITED LOGIN SCREEN!");
        System.exit(0);
    }

    public void setUserPurple() {
        Image img = new Image("/images/user.png");
        userImg.setImage(img);
    }

    public void setUserWhite() {
        Image img = new Image("/images/user0.png");
        userImg.setImage(img);
    }

    public void setLockPurple() {
        Image img = new Image("/images/lock.png");
        lockImg.setImage(img);
    }

    public void seLockWhite() {
        Image img = new Image("/images/lock0.png");
        lockImg.setImage(img);
    }

    // Login Button Action
    public void checkUser(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        User userObj = new User();
        userObj.setUsername(user.getText());
        userObj.setPassword(password.getText());

        UserDao userDao = new UserDao();

        if (userDao.checkUser(userObj)) {
            showMainView(event);
        } else {
            Notifications notification = Notifications.create().title("Access Denied")
                    .text("Please enter the correct details and try again.").position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(1)).position(Pos.BOTTOM_CENTER).hideCloseButton();
            notification.showWarning();
            user.clear();
            password.clear();
            System.out.println("NOT REGISTERED USER!!");
        }
    }

    //On Successful login, show the main window of the application
    private void showMainView(ActionEvent event) throws IOException {
        mainWindow.throwWindow("/fxml/mainView.fxml", event);
    }

    //show the signUp window
    public void toSignUp(ActionEvent event) throws IOException {
        mainWindow.throwWindow("/fxml/signUp.fxml", event);
    }
}
