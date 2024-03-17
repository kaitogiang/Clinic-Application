package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import controllers.DoctorController;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.StackPane;
import services.Database;

public class SelectionButton extends TableCell<Patient, Void> {
	private final Button selectionBtn = new Button("Chọn khám");
	private final Button cancelBtn = new Button("Hủy chọn");
	private StackPane container = new StackPane();
	private boolean isUpdatedPatientList;
	public SelectionButton(List<String> nameLabelList, 
			List<Patient> choosingPatientList, 
			boolean isUpdatePatientList, 
			Label patientNameLabel, 
			DoctorController controller) {
		this.isUpdatedPatientList = isUpdatePatientList;
		selectionBtn.setOnAction(event -> {
			Patient patient = getTableView().getItems().get(getIndex());
			System.out.println("Click on "+ patient.getOrderNumberValue());

			patientNameLabel.setText(patient.getPatientNameValue());
			nameLabelList.add(patient.getPatientNameValue());
			choosingPatientList.add(patient);
			
			System.out.println("Phan tu trong nametLabelList khi chon: ");
			nameLabelList.forEach(e->{
				System.out.println(e);
			});
			//Gọi hàm thay đổi trạng thái trên cơ sở dữ liệu
			Platform.runLater(() -> changeStatus(true, patient.getPatientIdValue()));
			controller.displayDiagnosisContentForCurrentPatient();
			patient.getStatus().set("Đang khám");
			selectionBtn.setVisible(false);
			cancelBtn.setVisible(true);
		});
		cancelBtn.setOnAction(event -> {
			Patient patient = getTableView().getItems().get(getIndex());
			System.out.println("Cancel selection "+ patient.getOrderNumberValue());
			System.out.println("Huy "+patient+ " hashCode = "+patient.hashCode());
			nameLabelList.remove(patient.getPatientNameValue());
			//Xóa đối tượng nếu chúng có cùng id
			choosingPatientList.removeIf(e->{
				return patient.getPatientIdValue().equals(e.getPatientIdValue());
			});
//			choosingPatientList.remove(patient);
			
			if (!nameLabelList.isEmpty()) {
				patientNameLabel.setText(nameLabelList.getLast());
			} else {
				patientNameLabel.setText("Chưa chọn");
			}
			System.out.println("Phan tu trong nametLabelList khi huy: ");
			nameLabelList.forEach(e->{
				System.out.println(e);
			});
			//Gọi hàm thay đổi trạng thái trên cơ sở dữ liệu
			patient.getStatus().set("Chờ khám");
			changeStatus(false, patient.getPatientIdValue());
			//Gọi hàm hiển thị lại nội dung đã nhập của người trước đó
			controller.displayDiagnosisContentForCurrentPatient();
			

			
			selectionBtn.setVisible(true);
			cancelBtn.setVisible(false);
		});
		
		selectionBtn.setPrefWidth(115);
		selectionBtn.setMinWidth(115);
		selectionBtn.setMaxWidth(115);
		
		cancelBtn.setPrefWidth(115);
		cancelBtn.setMinWidth(115);
		cancelBtn.setMaxWidth(115);
		
		container.getChildren().addAll(selectionBtn, cancelBtn);
		selectionBtn.setVisible(true);
		cancelBtn.setVisible(false);
		selectionBtn.getStyleClass().add("selection-button");
		cancelBtn.getStyleClass().add("cancel-button");
		
		//Kiểm tra xem người dùng đã chọn người nào và mô phỏng lại đã nhấn nút tương ứng đó
//		Patient patient = getTableView().getItems().get(getIndex());
//		System.out.println(patient.getPatientNameValue());
	}
	
	protected void updateItem(Void item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(container);
			Patient patient = getTableView().getItems().get(getIndex());
			if (patient.getStatusValue().equals("Đang khám")) {
//				selectionBtn.fire(); //Nguy hiem
				//Ẩn nút Chọn khám và hiện nút Hủy chọn
				selectionBtn.setVisible(false);
				cancelBtn.setVisible(true);
				System.out.println("Đã thực hiện hành động click nút Chọn khám");
			} else if (patient.getStatusValue().equals("Đã khám")) {
				selectionBtn.setDisable(true);
				selectionBtn.setVisible(true);
				cancelBtn.setVisible(false);
			}
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		}
	}
	//Hàm thay đổi trạng thái của bệnh nhân, nếu là true thì chuyển sang đang khám, nếu là false thì 
	//Chuyển sang chờ
	public void changeStatus(boolean isSelected, String patientId) {
		System.out.println("Call change status");
		String sql = "UPDATE history SET status = ? WHERE patient_id = ? AND DATE(date) = DATE(NOW())";
		if (isSelected) {
			try(Connection con = Database.connectDB()) {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, 1);
				ps.setString(2, patientId);
				int change = ps.executeUpdate();
				System.out.println("Ket qua cua bien change: "+change);
				if (change!=0) System.out.println("Change status to 1 successfully");
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			try(Connection con = Database.connectDB()) {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, 0);
				ps.setString(2, patientId);
				int change = ps.executeUpdate();
				if (change!=0) System.out.println("Change status to 0 successfully");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
//	public void printMessage(String text) {
//		System.out.println("Hello "+text);
//	}
}
