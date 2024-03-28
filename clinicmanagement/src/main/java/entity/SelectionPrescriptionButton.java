package entity;

import java.util.List;
import java.util.function.Consumer;

import controllers.PharmacistController;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.layout.StackPane;

public class SelectionPrescriptionButton extends TableCell<Prescription, Void> {
	private final Button removeBtn = new Button("Hủy");
	private final Button selectionBtn = new Button("Chọn");
	private final StackPane container = new StackPane(removeBtn,selectionBtn);
	private Consumer<ObservableList<PrescriptionDetail>> addedPrescriptionDetailCallBack;
	public SelectionPrescriptionButton(PharmacistController parentController) {
		addedPrescriptionDetailCallBack = parentController::setPrescriptionDetailList;
		selectionBtn.setOnAction(event -> {
			Prescription item = getTableView().getItems().get(getIndex());
			ObservableList<PrescriptionDetail> newPrescriptionDetail = item.getMedicineList();
			addedPrescriptionDetailCallBack.accept(newPrescriptionDetail);
			Patient patient = item.getPatientValue();
			parentController.addPrescriptionQueue(item);
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
			setGraphic(container);
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		}
	}
	
	
}
