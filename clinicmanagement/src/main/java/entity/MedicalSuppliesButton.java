package entity;

import controllers.AdminController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;

public class MedicalSuppliesButton extends TableCell<MedicalSupplies, Void> {
	private final Button removeBtn = new Button("Xóa");
	private final Button updateBtn = new Button("Sửa");
	private final VBox container = new VBox();
	private AdminController parentController;
	public MedicalSuppliesButton(AdminController parentController) {
		this.parentController = parentController;
		removeBtn.setOnAction(event -> {
			System.out.println("Click xóa vật tụ");
		});
		updateBtn.setOnAction(event -> {
			System.out.println("Click chỉnh sửa vật tư");
		});
		removeBtn.setPrefWidth(75);
		removeBtn.setMinWidth(75);
		removeBtn.setMaxWidth(75);
		removeBtn.getStyleClass().add("cancel-button");
		
		updateBtn.setPrefWidth(75);
		updateBtn.setMinWidth(75);
		updateBtn.setMaxWidth(75);
		updateBtn.getStyleClass().add("selection-button");
		
		container.setSpacing(10);
		container.setPadding(new Insets(10, 0, 10, 0));
		container.getChildren().addAll(updateBtn,removeBtn);

	}
	
	protected void updateItem(Void item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
		} else {
			container.setAlignment(Pos.CENTER);
			setGraphic(container);
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		}
	}
	
}
