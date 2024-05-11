package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;

import controllers.AdminController;
import controllers.MedicalSuppliesFormController;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.Database;

public class MedicalSuppliesButton extends TableCell<MedicalSupplies, Void> {
	private final Button removeBtn = new Button("Xóa");
	private final Button updateBtn = new Button("Sửa");
	private final VBox container = new VBox();
	private AdminController parentController;
	public MedicalSuppliesButton(AdminController parentController) {
		this.parentController = parentController;
		removeBtn.setOnAction(event -> {
			System.out.println("Click xóa vật tụ");
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setHeaderText(null);
			alert.setContentText("Bạn chắc chắn xóa vật tư này?");
			if (alert.showAndWait().get().equals(ButtonType.OK)) {
				String id = getTableView().getItems().get(getIndex()).getIdValue();
				removeMedicalSupplies(id);
				getTableView().getItems().remove(getIndex());
				Message.showMessage("Xóa vật tư thành công", AlertType.INFORMATION);
			}
		});
		updateBtn.setOnAction(event -> {
			System.out.println("Click chỉnh sửa vật tư");
			//Nạp giao diện
			MedicalSupplies mc = getTableView().getItems().get(getIndex());
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MedicalSuppliesFormScreen.fxml"));
			String css = this.getClass().getResource("/css/style.css").toExternalForm();
			try {
				//Nạp giao diện vào
				AnchorPane main = loader.load();
				MedicalSuppliesFormController controller = loader.getController();
				controller.setData(mc, parentController, true);
				Scene scene = new Scene(main);
				scene.getStylesheets().add(css);
				Stage stage = new Stage();
				stage.setScene(scene);
				
				stage.show();
				
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
	
	public void removeMedicalSupplies(String id) {
		String sql = "DELETE FROM medical_supplies WHERE supplies_id = ?";
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);
			int update = ps.executeUpdate();
			if (update!=0) System.out.println("Đã xóa vật tư thành công");
		} catch(Exception e) {
			e.printStackTrace();
		}
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
