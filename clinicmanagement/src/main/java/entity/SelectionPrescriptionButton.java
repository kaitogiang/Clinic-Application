package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.function.Consumer;

import controllers.PharmacistController;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.StackPane;
import services.Database;

public class SelectionPrescriptionButton extends TableCell<Prescription, Void> {
	private  Button removeBtn = new Button("Hủy");
	private  Button selectionBtn = new Button("Chọn");
	private Label doneLabel = new Label("Đã thanh toán");
	private  StackPane container = new StackPane(removeBtn,selectionBtn,doneLabel);
	private Consumer<ObservableList<PrescriptionDetail>> addedPrescriptionDetailCallBack;
	private Prescription selectedItem;
	private PharmacistController parentController;
	
	public SelectionPrescriptionButton(PharmacistController parentController) {
		doneLabel.setVisible(false);
		doneLabel.getStyleClass().add("done-label");
		this.parentController = parentController;
		addedPrescriptionDetailCallBack = parentController::setPrescriptionDetailList;
		selectionBtn.setOnAction(event -> {
			System.out.println(this.hashCode());
			
			Prescription item = getTableView().getItems().get(getIndex());
			selectedItem = item;
			ObservableList<PrescriptionDetail> newPrescriptionDetail = item.getMedicineList();
			addedPrescriptionDetailCallBack.accept(newPrescriptionDetail);
			Patient patient = item.getPatientValue();
			parentController.addPrescriptionQueue(item);
			parentController.addSelectedButtonToQueue(this); //Lưu trữ chỉ số đã chọn
			//Tính tổng tiền
			float sum=0;
			for(PrescriptionDetail e: newPrescriptionDetail) {
				sum += e.getMedicineQuantityValue()*e.getMedicineValue().getUnitPriceValue();
			}
			parentController.setInitialPatientInfo(patient.getPatientNameValue(), patient.getPatientAddressValue(), String.valueOf(sum));
			removeBtn.setVisible(true);
			selectionBtn.setVisible(false);
		});
		
		
		removeBtn.setOnAction(event -> {
			//Lấy đơn thuốc đang được chọn hiện tại
			Prescription selectedItem = getTableView().getItems().get(getIndex());
			//Xóa đơn thuốc này khi người dùng bấm hủy
			parentController.removePrescriptionQueue(selectedItem);
			parentController.removeSelectedButtonFromQueue(this); //Xóa chỉ số đã chọn
			//Lấy giá trị kề phần tử mới xóa và tiến hàng gán giá trị
			List<Prescription> queue = parentController.getPrescriptionQueue();
			if (queue.size() == 0) {
				parentController.resetPrescriptionDetailList();
				parentController.setInitialPatientInfo();
			} else {
				Prescription item = parentController.getPrescriptionQueue().getLast();
				ObservableList<PrescriptionDetail> selectedPrescription = item.getMedicineList();
				Patient patient = item.getPatientValue();
				//Cập nhật bảng hiển thị 
				addedPrescriptionDetailCallBack.accept(selectedPrescription);
				//Tính tổng đơn thuốc
				float sum = 0;
				for(PrescriptionDetail e: selectedPrescription) {
					sum += e.getMedicineQuantityValue()*e.getMedicineValue().getUnitPriceValue();
				}
				//Hiển thị thông tin người bệnh
				parentController.setInitialPatientInfo(patient.getPatientNameValue(), patient.getPatientAddressValue(), String.valueOf(sum));
			}
			
			removeBtn.setVisible(false);
			selectionBtn.setVisible(true);
		});
		
		
		
		selectionBtn.setPrefWidth(75);
		selectionBtn.setMinWidth(75);
		selectionBtn.setMaxWidth(75);
		selectionBtn.getStyleClass().add("selection-button");
		
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
			int index = getIndex();
			String currentPrescriptionId = getTableView().getItems().get(index).getPrescriptionIdvalue();
			if (isPaidPrescription(currentPrescriptionId)) {
				setSuccessStatus();
			}
			setGraphic(container);
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		}
	}
	

	public void setSuccessStatus() {
		doneLabel.setVisible(true);
		
		selectionBtn.setVisible(false);
		removeBtn.setVisible(false);
		//Xóa đơn thuốc và tham chiếu của đối tượng này
		parentController.removePrescriptionQueue(selectedItem);
		parentController.removeSelectedButtonFromQueue(this);
		//Lấy giá trị kề phần tử mới xóa và tiến hàng gán giá trị
		List<Prescription> queue = parentController.getPrescriptionQueue();
		if (queue.size() == 0) {
			parentController.resetPrescriptionDetailList();
			parentController.setInitialPatientInfo();
		} else {
			Prescription item = parentController.getPrescriptionQueue().getLast();
			ObservableList<PrescriptionDetail> selectedPrescription = item.getMedicineList();
			Patient patient = item.getPatientValue();
			//Cập nhật bảng hiển thị 
			addedPrescriptionDetailCallBack.accept(selectedPrescription);
			//Tính tổng đơn thuốc
			float sum = 0;
			for(PrescriptionDetail e: selectedPrescription) {
				sum += e.getMedicineQuantityValue()*e.getMedicineValue().getUnitPriceValue();
			}
			//Hiển thị thông tin người bệnh
			parentController.setInitialPatientInfo(patient.getPatientNameValue(), patient.getPatientAddressValue(), String.valueOf(sum));
		}
	}
	
	public void setSuccessLabel() {
		System.out.println("Goi ham hien thi");
		doneLabel.setVisible(true);
		selectionBtn.setVisible(false);
		removeBtn.setVisible(false);
	}
	
	public boolean isPaidPrescription(String prescriptionId) {
		String sql = "SELECT * FROM invoice WHERE prescription_id = ?";
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, prescriptionId);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch(Exception e) {
			System.out.println(e);
		}
		return false;
	}
}
