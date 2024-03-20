package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;

import controllers.MedicineFormController;
import controllers.PharmacistController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.Database;

public class MedicineButton extends TableCell<Medicine, Void> {
	private final Button removeBtn = new Button("Xóa");
	private final Button updateBtn = new Button("Sửa");
	private final VBox container = new VBox();
	public MedicineButton(PharmacistController parentController) {
		FontAwesomeIconView editIcon = new FontAwesomeIconView();
		editIcon.setGlyphName("EDIT");
		editIcon.setGlyphSize(20);
		removeBtn.setOnAction(event -> {
			Medicine medicine = getTableView().getItems().get(getIndex());
			System.out.println("REMOVE "+medicine);
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setHeaderText(null);
			alert.setContentText("Bạn chắc chắn xóa "+medicine.getMedicineNameValue()+"?");
			if (alert.showAndWait().get().equals(ButtonType.OK)) {
				removeMedicineFromDatabase(medicine.getMedicineIdValue());
				getTableView().getItems().remove(getIndex());
			}
		});
		updateBtn.setOnAction(event -> {
			Medicine medicine = getTableView().getItems().get(getIndex());
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MedicineForm.fxml"));
			String css = this.getClass().getResource("/css/style.css").toExternalForm();
			try {
				AnchorPane root = loader.load();
				MedicineFormController controller = loader.getController();
				//tương dương với (item) -> patientList.add(item);
				controller.setMedicineForm(medicine, getTableView(), getIndex());
				controller.setParentController(parentController);
				Stage newStage = new Stage();
				Scene scene = new Scene(root);
				scene.getStylesheets().add(css);
				//Tạo icon cho stage
				Image image = new Image(getClass().getResourceAsStream("/images/healthcare.png"));
				newStage.getIcons().add(image);
				newStage.setScene(scene);
				newStage.setTitle("Editing medicine form");
				newStage.show();
			} catch(Exception e) {
				e.printStackTrace();
			}
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
	
	public void removeMedicineFromDatabase(String medicineId) {
		String sql = "DELETE FROM medicine WHERE medicine_id = ?";
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, medicineId);
			int delete = ps.executeUpdate();
			if (delete != 0) System.out.println("Delete medicine successfully!");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
