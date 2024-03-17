package entity;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Message {
    //Hàm hiển thị thông báo
	public static void showMessage(String content, AlertType type) {
    	if (type.equals(AlertType.INFORMATION)) {
    		Alert alert = new Alert(type);
    		alert.setTitle("Thông tin");
        	alert.setHeaderText(null);
        	alert.setContentText(content);
        	alert.showAndWait();
    	} else if (type.equals(AlertType.ERROR)) {
    		Alert alert = new Alert(type);
        	alert.setTitle("Lỗi");
        	alert.setHeaderText(null);
        	alert.setContentText(content);
        	alert.showAndWait();
    	}
    }
}
