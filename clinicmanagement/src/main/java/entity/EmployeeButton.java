package entity;

import controllers.AdminController;
import controllers.EmployeeDetailController;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmployeeButton extends TableCell<Employee, Void> {
	private final Button removeBtn = new Button("Xóa");
	private final Button updateBtn = new Button("Sửa");
	private final VBox container = new VBox();
	public EmployeeButton(AdminController parentController) {
		removeBtn.setOnAction(event -> {
			System.out.println("Click chọn xóa");
			removeEmployee();
		});
		updateBtn.setOnAction(event -> {
			System.out.println("Click chọn chỉnh sửa");
			Employee e = getTableView().getItems().get(getIndex());
			showEditScreen(e);
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
	
	public void showEditScreen(Employee emp) {
  		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EmployeeDetailScreen.fxml"));
		String css = this.getClass().getResource("/css/style.css").toExternalForm();
  		try {
  			AnchorPane main = loader.load();
  			Scene scene = new Scene(main);
  			Stage stage = new Stage();
  			EmployeeDetailController controller = loader.getController();
  			controller.setData(emp, true);
  			scene.getStylesheets().add(css);
  			stage.setScene(scene);
  			stage.setTitle("Employee edit");
  			stage.show();
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
	}
	
	//Hàm xóa một người dùng trong danh sách người dùng
  	public void removeEmployee() {
  		//Xóa employee trên giao diện
  		ObservableList<Employee> list = getTableView().getItems();
  		list.remove(getIndex()); //Xóa một phần tử tại chỉ số đã chọn
  		//Cập nhật lại số thứ tự
  		System.out.println("Đã xóa employee");
  	}
	
}
