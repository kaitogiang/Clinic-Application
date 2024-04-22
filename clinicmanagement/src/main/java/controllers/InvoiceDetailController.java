package controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import entity.Invoice;
import entity.Medicine;
import entity.PrescriptionDetail;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.Database;

public class InvoiceDetailController implements Initializable{

    @FXML
    private Label address;
    
    @FXML
    private Label date;

    @FXML
    private Label invoiceId;
    
    @FXML
    private Label patientName;
    
    @FXML
    private TableColumn<PrescriptionDetail, String> medicineNameColumn;

    @FXML
    private TableColumn<PrescriptionDetail, Integer> orderNumberColumn;
    
    @FXML
    private TableView<PrescriptionDetail> prescriptionTable;

    @FXML
    private TableColumn<PrescriptionDetail, Float> priceColumn;

    @FXML
    private TableColumn<PrescriptionDetail, Integer> quantityColumn;

    @FXML
    private TableColumn<PrescriptionDetail, Float> totalAmount;

    @FXML
    private VBox vboxContainer;
    
    //User-defined variable
    private ObservableList<PrescriptionDetail> medicineList;
    
    private HBox totalContainer;
    
    private Label totalLabel;

    private Label totalTitle;

    private float sum = 0; 

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		totalContainer = new HBox();
    	totalLabel = new Label();
    	
    	totalTitle = new Label("Tổng thành tiền: ");
    	totalTitle.getStyleClass().add("diagnosis-content");
    	totalTitle.setStyle("-fx-font-size: 18px");
    	
    	totalLabel.getStyleClass().add("diagnosis-content");
    	totalLabel.setStyle("-fx-font-size: 18px");

    	
    	totalContainer.getChildren().addAll(totalTitle,totalLabel);
    	totalContainer.setAlignment(Pos.CENTER_RIGHT);
    	totalContainer.setSpacing(10);
    	vboxContainer.getChildren().add(totalContainer);
		medicineList = FXCollections.observableArrayList();
		displayPrescriptionDetail();
	}
    
	public void setInvoice(Invoice invoice) {
		//Đặt id hóa đơn
		invoiceId.setText("Id hóa đơn: "+invoice.getInvoiceIdValue());
		//Đặt ngày tháng năm
		String d = invoice.getCreationDateValue();
		String[] dateString = d.split("-");
		date.setText("Ngày " + dateString[0] + " tháng " + dateString[1] + " năm "+dateString[2]);
		//Đặt thông tin người mua
		Map<String, String> patientInfo = getPatientInfo(invoice.getPrescriptionIdValue());
		patientName.setText(patientInfo.get("patientName"));
		address.setText(patientInfo.get("address"));
		//Đặt danh sách thuốc
		medicineList.addAll(medicineList(invoice.getPrescriptionIdValue()));
		for(PrescriptionDetail e: medicineList) {
    		sum+= e.getMedicineQuantityValue()*e.getMedicineValue().getUnitPriceValue();
    	}
    	totalLabel.setText(String.valueOf(sum)+" VNĐ");
	}
	
	public Map<String, String> getPatientInfo(String prescriptionId) {
		String sql = "SELECT p.patient_name, p.address "
				+ "FROM `diagnosis` d JOIN patients p "
				+ "ON d.patient_id = p.patient_id WHERE d.prescription_id = ?";
		Map<String, String> map = new HashMap<>();
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, prescriptionId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				map.put("patientName", rs.getString("patient_name"));
				map.put("address", rs.getString("address"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public ObservableList<PrescriptionDetail> medicineList(String prescriptionId) {
		ObservableList<PrescriptionDetail> list = FXCollections.observableArrayList();
		String sql = "SELECT m.medicine_id, m.medicine_name, "
				+ "m.unit_price, pre.medicine_amount, pre.medicine_usage  "
				+ "FROM `prescriptiondetail` pre JOIN medicine m "
				+ "ON pre.medicine_id = m.medicine_id WHERE prescription_id = ?";
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, prescriptionId);
			ResultSet rs = ps.executeQuery();
			int order = 1;
			while(rs.next()) {
				String medicineId = rs.getString("medicine_id");
				String medicineName = rs.getString("medicine_name");
				float unitPrice = rs.getFloat("unit_price");
				int medicineAmount = rs.getInt("medicine_amount");
				String usage = rs.getString("medicine_usage");
				Medicine m = new Medicine(medicineId, medicineName, unitPrice);
				PrescriptionDetail p = new PrescriptionDetail(order++, medicineName, medicineAmount, usage, m);
				list.add(p);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void displayPrescriptionDetail() {
    	orderNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
    	medicineNameColumn.setCellValueFactory(cellData -> cellData.getValue().getMedicineName());
    	quantityColumn.setCellValueFactory(cellData -> cellData.getValue().getMedicineQuantity().asObject());
    	priceColumn.setCellValueFactory(cellData -> cellData.getValue().getMedicine().getValue().getUnit_price().asObject());
    	//Tính thành tiền mà không cần lưu trữ dữ liệu thành tiền trong lớp
    	totalAmount.setCellValueFactory(cellData -> {
    		PrescriptionDetail data = cellData.getValue();
    		float sum = data.getMedicineQuantityValue()*data.getMedicineValue().getUnitPriceValue();
    		return new SimpleFloatProperty(sum).asObject();
    	});
    	prescriptionTable.setItems(medicineList);
    }
	
    public void close() {
    	Stage currentStage = (Stage) date.getScene().getWindow();
    	currentStage.close();
    }




    
    
}
