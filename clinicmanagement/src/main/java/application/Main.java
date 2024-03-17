package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
public class Main extends Application{

	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/LoginScreen.fxml"));
			System.out.println(root);
			Scene scene = new Scene(root);
			String css = this.getClass().getResource("/css/style.css").toExternalForm();
			Image image = new Image(getClass().getResourceAsStream("/images/healthcare.png"));
			scene.getStylesheets().add(css);
			primaryStage.getIcons().add(image);
			primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			System.out.println(e);
//			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
	
}
