package entity;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;

public class RemoveButton extends TableCell<PrescriptionDetail, Void> {
	private final Button removeBtn = new Button("Xóa");
	public RemoveButton() {
		removeBtn.setOnAction(event -> {
			PrescriptionDetail medicine = getTableView().getItems().get(getIndex());
			System.out.println("REMOVE "+medicine);
			getTableView().getItems().remove(getIndex());
		});
		removeBtn.setPrefWidth(75);
		removeBtn.setMinWidth(75);
		removeBtn.setMaxWidth(75);
		removeBtn.getStyleClass().add("cancel-button");

	}
	
	protected void updateItem(Void item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(removeBtn);
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		}
	}
}
