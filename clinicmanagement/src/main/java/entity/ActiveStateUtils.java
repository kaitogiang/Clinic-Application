package entity;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ActiveStateUtils {

	private static String[] style = {"active-selection-background","active-selection-icons", "active-selection-button-title"};
	
	//Tạo active cho các tab chính của ứng dụng
	public static void addStyleClass(Button parent) {
		//Dùng hàm setGraphic để ép buộc khởi tạo các con hoàn toàn trong hàm initialize
		HBox childHBox = (HBox) parent.getGraphic();
		Node icons = childHBox.getChildren().get(0);
		Node label = childHBox.getChildren().get(1);
		if (!childHBox.getStyleClass().contains(style[0])) {
			childHBox.getStyleClass().add(style[0]);
			icons.getStyleClass().add(style[1]);
			label.getStyleClass().add(style[2]);
		}
		
	}
	public static void removeStyleClass(Button parent) {
		HBox childHBox = (HBox) parent.getGraphic();
		Node icons = childHBox.getChildren().get(0);
		Node label = childHBox.getChildren().get(1);
		childHBox.getStyleClass().remove(style[0]);
		icons.getStyleClass().remove(style[1]);
		label.getStyleClass().remove(style[2]);
	}
	
	
	//Tạo active cho tabpane tình trạng bệnh
	public static void addActiveForTab(Button btn) {
		if (!btn.getStyleClass().contains("veritcal-navigation-item-active")) {
			btn.getStyleClass().add("veritcal-navigation-item-active");
		}
	}
	
	public static void removeActiveForTab(Button btn) {
		btn.getStyleClass().remove("veritcal-navigation-item-active");
	}
}
