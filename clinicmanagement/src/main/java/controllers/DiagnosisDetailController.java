package controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import entity.Diagnosis;
import entity.Medicine;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import services.Database;

public class DiagnosisDetailController implements Initializable{

    @FXML
    private Label checkDate;

    @FXML
    private Label diagnosisField;

    @FXML
    private Label doctorName;

    @FXML
    private TableColumn<Medicine, Integer> dosage;

    @FXML
    private TableColumn<Medicine, String> dosageUnit;

    @FXML
    private TableColumn<Medicine, String> medicineName;

    @FXML
    private TableColumn<Medicine, Integer> medicineOrder;

    @FXML
    private TableColumn<Medicine, Integer> quantity;
    
    @FXML
    private TableView<Medicine> usedMedicineTable;
    
    @FXML
    private Label noteField;

    @FXML
    private Label patientName;

    @FXML
    private Label testResultField;
    
    //user-defined variables
    private Diagnosis diagnosis;
    
    private ObservableList<Medicine> medicineList;
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	//Khởi tạo giá trị ban đầu cho bảng nếu không có dòng nào
    	Label initial = new Label("Chưa có dữ liệu");
    	initial.setFont(new Font(17));
    	initial.getStyleClass().add("diagnosis-content");
    	usedMedicineTable.setPlaceholder(initial);
		Platform.runLater(()->{
			medicineList = fetchPrescriptionDetail();
			showUsedMedicineTable(medicineList);
		});
	}
    
    public void setDiagnosisData(Diagnosis data) {
    	diagnosis = data;
    	checkDate.setText(String.valueOf(diagnosis.getDiagnosisDateValue()));
    	doctorName.setText(diagnosis.getDoctorNameValue());
    	patientName.setText(diagnosis.getPatientNameValue());
    	testResultField.setText(diagnosis.getTestResultValue());
    	diagnosisField.setText(diagnosis.getDiagnosisValue());
    	noteField.setText(diagnosis.getNotesValue());
    }
    
    public ObservableList<Medicine> fetchPrescriptionDetail() {
    	ObservableList<Medicine> list = FXCollections.observableArrayList();
    	int order = 1;
    	String sql = "SELECT m.*, pd.medicine_amount "
    			+ "FROM prescription p JOIN prescriptiondetail pd "
    			+ "ON p.prescription_id = pd.prescription_id "
    			+ "JOIN medicine m ON pd.medicine_id = m.medicine_id "
    			+ "WHERE p.prescription_id = ? AND DATE(p.creation_date) = DATE(NOW())";
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1, diagnosis.getPrescriptionIdValue());
    		ResultSet rs = ps.executeQuery();
    		while(rs.next()) {
    			String medicineId = rs.getString("medicine_id");
    			String medicineName = rs.getString("medicine_name");
    			String description = rs.getString("description");
    			String manufacturer = rs.getString("manufacturer");
    			int dosage = rs.getInt("dosage");
    			String dosageUnit = rs.getString("dosage_unit");
    			float unitPrice = rs.getFloat("unit_price");
    			// Lấy giá trị kiểu java.sql.Timestamp từ cơ sở dữ liệu
                java.sql.Timestamp timestamp = rs.getTimestamp("expiration_date");
                // Chuyển đổi thành kiểu java.time.LocalDate để chỉ lấy ngày
                LocalDate date = timestamp.toLocalDateTime().toLocalDate();
                //Chuyển đổi định dạng ngày sang dd-MM-yyyy
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    			String expirationDate = String.valueOf(formatter);
    			int medicineAmount = rs.getInt("medicine_amount");
    			
    			Medicine med = new Medicine(medicineId, order, 
    					medicineName, description, manufacturer, 
    					dosage, dosageUnit, medicineAmount, expirationDate, medicineAmount);
    			list.add(med);
    			order++;
    			
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }

	public void showUsedMedicineTable(ObservableList<Medicine> list) {
		medicineOrder.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
		medicineName.setCellValueFactory(cellData -> cellData.getValue().getMedicineName());
		dosage.setCellValueFactory(cellData -> cellData.getValue().getDosage().asObject());
		dosageUnit.setCellValueFactory(cellData -> cellData.getValue().getDosage_unit());
		quantity.setCellValueFactory(cellData -> cellData.getValue().getQuantity().asObject());
		usedMedicineTable.setItems(list);
	}
    
	public void close() {
		Stage currentStage = (Stage) testResultField.getScene().getWindow();
		currentStage.close();
	}
    
}
