import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import dao.UserDao;
import dto.User;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SignUpController implements Initializable{
	@FXML
	ImageView userIco;
	
	@FXML
	ImageView lockIco;
	
	@FXML
	Text loginText;
	
	@FXML
	JFXTextField userName;
	
	@FXML
	JFXPasswordField password;
	
	public void setUserGreen() {
		Image img=new Image("/images/userg.png");
		userIco.setImage(img);
	}
	
	public void setUserWhite() {
		Image img=new Image("/images/user0.png");
		userIco.setImage(img);
	}
	
	public void setLockGreen() {
		Image img=new Image("/images/lockg.png");
		lockIco.setImage(img);
	}
	
	public void seLockWhite() {
		Image img=new Image("/images/lock0.png");
		lockIco.setImage(img);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loginText.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				try {
					toLogin(event);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	private void toLogin(MouseEvent event) throws IOException {
		Parent signupStage=FXMLLoader.load(getClass().getResource("/fxml/logIn.fxml"));
		Scene mainScene=new Scene(signupStage);

		//This Gets The Scene Info
		Stage window=(Stage) ((Node) event.getSource()).getScene().getWindow();
		mainScene.setFill(Color.TRANSPARENT);
		window.setScene(mainScene);
		window.centerOnScreen();
		window.show();
	}
	
	public void saveUser() throws ClassNotFoundException, SQLException {
		User user=new User();
		user.setUsername(userName.getText());
		user.setPassword(password.getText());
		
		if (userName.getText().equals("") | password.getText().equals("")) {
			Notifications notifications=Notifications.create()
					.title("Blank Username/Password")
					.text("No blank username or passwords allowed.")
					.position(Pos.BOTTOM_CENTER)
					.hideAfter(Duration.seconds(1.5));
			notifications.showWarning();
			resetfields();
			return;
		}
		
		UserDao userDao=new UserDao();
		boolean save=userDao.saveUser(user);
		
		if (save) {
			Notifications notifications=Notifications.create()
					.title("Save Successful")
					.text("User credentials were saved successfully.")
					.position(Pos.BOTTOM_CENTER)
					.hideAfter(Duration.seconds(2));
			notifications.showInformation();
			resetfields();
		}else {
			Notifications notifications=Notifications.create()
					.title("Duplicate Entry")
					.text("User with that name already exists. Try another.")
					.position(Pos.BOTTOM_CENTER)
					.hideAfter(Duration.seconds(2));
			notifications.showError();
			resetfields();
		}
		

	}
	
	private void resetfields() {
		//clear the input fields
		userName.clear();
		password.clear();
	}
}
