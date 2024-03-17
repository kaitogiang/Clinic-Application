package entity;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LogoutHandler {

	public static void logout(Stage currentStage, FXMLLoader loader) {
		try {
			AnchorPane loginScreen = loader.load();
			Scene scene = new Scene(loginScreen);
			currentStage.close();
			currentStage.setScene(scene);
			currentStage.show();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
