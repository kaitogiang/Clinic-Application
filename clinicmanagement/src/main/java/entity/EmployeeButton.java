package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import controllers.AdminController;
import controllers.EmployeeDetailController;
import javafx.collections.ObservableList;
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

public class EmployeeButton extends TableCell<Employee, Void> {
	private final Button removeBtn = new Button("Xóa");
	private final Button updateBtn = new Button("Sửa");
	private final VBox container = new VBox();
	private AdminController parentController;
	public EmployeeButton(AdminController parentController) {
		this.parentController = parentController;
		removeBtn.setOnAction(event -> {
			Employee e = getTableView().getItems().get(getIndex());
			System.out.println("Click chọn xóa");
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setHeaderText(null);
			alert.setContentText("Bạn chắc chắn xóa "+e.getEmployeeNameValue()+"?");
			if (alert.showAndWait().get().equals(ButtonType.OK)) {
				removeEmployee();
			}
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
  		Employee e = list.get(getIndex());
  		list.remove(getIndex()); //Xóa một phần tử tại chỉ số đã chọn
  		parentController.removeElementEmployeeList(e);
  		System.out.println("Đã xóa employee");
  		//Xóa trên cơ sở dữ liệu
  		int roleIndex = 0;
  		int accountId = e.getAccountId();
  		int imageId = 0;
  		String employeeId = "";
  		//Gán các dữ liệu cần thiết
  		if (e.getData() instanceof Receptionist) {
  			roleIndex = 1;
  			Receptionist data = (Receptionist) e.getData();
  			imageId = data.getImageId();
  			employeeId = data.getReceptionistId();
  		} else if (e.getData() instanceof Doctor) {
  			roleIndex = 2;
  			Doctor data = (Doctor) e.getData();
  			imageId = data.getImageId();
  			employeeId = data.getDoctorId();
  		} else if (e.getData() instanceof Pharmacist) {
  			roleIndex = 3;
  			Pharmacist data = (Pharmacist) e.getData();
  			imageId = data.getImageId();
  			employeeId = data.getPharmacistId();
  		}
  		//Gọi hàm thực thi việc xóa trên cơ sở dữ liệu
  		removeEmployeeFromDatabase(roleIndex, accountId, imageId, employeeId);
  	}
  	
  	//Hàm xóa phần tử trên cơ sở dữ liệu
  	public void removeEmployeeFromDatabase(int roleIndex, int accountId, int imageId, String employeeId) {
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = null;
  			if (roleIndex == 1) {
  	  			//Nhân viên lễ tân
  				String sql = "DELETE FROM receptionists WHERE receptionist_id = ?";
  	  			ps = con.prepareStatement(sql);
  	  			ps.setString(1, employeeId);
  	  		} else if (roleIndex == 2) {
  	  			//Bác sĩ
  	  			if (isRegistedAccount(employeeId)) {
	  	  			String sql = "UPDATE doctors SET doctor_name = 'unknow', doctor_email = 'unknow', "
	  	  					+ "phone_number = 'unknow', address = 'unknow', experience_year = 0, "
	  	  					+ " account_id = -1, image_id = -1 WHERE doctor_id = ?";
		  			ps = con.prepareStatement(sql);
		  			ps.setString(1, employeeId);
  	  			} else {
	  	  			String sql = "DELETE FROM doctors WHERE doctor_id = ?";
	  	  			ps = con.prepareStatement(sql);
		  			ps.setString(1, employeeId);
  	  			}
  	  			
  	  		} else if (roleIndex == 3) {
  	  			//Nhân viên bán thuốc
	  	  		String sql = "DELETE FROM pharmacists WHERE pharmacist_id = ?";
	  			ps = con.prepareStatement(sql);
	  			ps.setString(1, employeeId);
  	  		}
  			int update = ps.executeUpdate();
  			if (update!=0) {
  				System.out.println("Xóa người dùng thành công");
  			}
  			//Xóa tài khoản
  			String sql = "DELETE FROM accounts WHERE account_id = ?";
  			ps = con.prepareStatement(sql);
  			ps.setInt(1, accountId);
  			int removed = ps.executeUpdate();
  			if (removed!=0) {
  				System.out.println("Đã xóa tài khoản");
  			}
  			
  			//Xóa ảnh
  			String imgSql = "DELETE FROM images WHERE image_id = ?";
  			ps = con.prepareStatement(imgSql);
  			ps.setInt(1, imageId);
  			int removedImg = ps.executeUpdate();
  			if (removedImg!=0) {
  				System.out.println("Đã xóa ảnh");
  			}
  			
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  	}
	
  	//Hàm kiểm tra đã có tài khoản chưa
  	public boolean isRegistedAccount(String doctorId) {
  		String sql = "SELECT CASE "
  				+ "WHEN acc.username = 'Chưa tạo' "
  				+ "THEN 0 ELSE 1 END isRegistered "
  				+ "FROM doctors d JOIN accounts acc ON d.account_id = acc.account_id "
  				+ "WHERE d.doctor_id = ?";
  		boolean isRegistered = true;
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ps.setString(1, doctorId);
  			ResultSet rs = ps.executeQuery();
  			if (rs.next()) {
  				if (rs.getInt("isRegistered") == 0) {
  					isRegistered = false;
  				}
  			}
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		
  		return isRegistered;
  	}
}
