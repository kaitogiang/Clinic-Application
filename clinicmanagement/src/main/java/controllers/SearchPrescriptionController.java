package controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import entity.Message;
import entity.Patient;
import entity.PrescriptionDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import services.Database;

public class SearchPrescriptionController implements Initializable{
	 @FXML
	 private Label address;
	 
    @FXML
    private Label patientAge;

    @FXML
    private Label patientName;
	 
    @FXML
    private Label patientWeight;

    @FXML
    private Label phoneNumber;
    
    @FXML
    private TextField searchText;
    
    @FXML
    private HBox defaultTextContainer;

    @FXML
    private VBox informationContainer;

    @FXML
    private Label diagnosis;
    
    @FXML
    private TableColumn<PrescriptionDetail, String> medicineNameColumn;

    @FXML
    private TableColumn<PrescriptionDetail, Integer> orderNumberColumn;
    
    @FXML
    private TableView<PrescriptionDetail> prescriptionTable;

    @FXML
    private TableColumn<PrescriptionDetail, Integer> quantityColumn;
    
    @FXML
    private TableColumn<PrescriptionDetail, String> usageColumn;

    //user-defined variable
    
	private String patientId = "";
	
	private String diagnosisText = "";
	
	private ObservableList<PrescriptionDetail> prescriptionDetailList;
	
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//Hiển thị text ban đầu khi người dùng chưa tìm kiếm
		defaultTextContainer.setVisible(true);
		informationContainer.setVisible(false);
		//Hiển thị giá trị bảng ban đầu
		Label initalPrescription = new Label("Chưa có đơn thuốc nào");
		initalPrescription.setFont(new Font(17));
		prescriptionTable.setPlaceholder(initalPrescription);
		prescriptionDetailList = FXCollections.observableArrayList();
		displayPrescription(); //Hiển thị dữ liệu đơn thuốc
		
		searchText.textProperty().addListener((observe, oldValue, newValue) -> {
			if (newValue.isEmpty()) {
				removeAll();
			}
		});
	}
    
    public void search() {
    	String text = searchText.getText();
    	if (text.isEmpty()) {
    		Message.showMessage("Vui lòng nhập id đơn thuốc", AlertType.ERROR);
    	} else {
    		prescriptionDetailList.addAll(searchPrescriptionDatabase(text));
    		informationContainer.setVisible(true);
    		defaultTextContainer.setVisible(false);
    		Patient patient = getPatientInfo();
    		if (patient==null) {
    			defaultTextContainer.setVisible(true);
    			informationContainer.setVisible(false);
    			diagnosis.setText("Chưa có dữ liệu");

    			return;
    		}
    		patientName.setText(patient.getPatientNameValue());
    		patientAge.setText(String.valueOf(patient.getPatientAgeValue())+" tuổi");
    		patientWeight.setText(String.valueOf(patient.getPatientWeightValue())+" kg");
    		address.setText(patient.getPatientAddressValue());
    		phoneNumber.setText(patient.getPatientPhoneNumberValue());
    		diagnosis.setText(diagnosisText);
    		diagnosis.setAlignment(Pos.TOP_LEFT);
    	}
    }
    
    public void removeAll() {
    	informationContainer.setVisible(false);
		defaultTextContainer.setVisible(true);
    	patientName.setText("");
		patientAge.setText("");
		patientWeight.setText("");
		address.setText("");
		phoneNumber.setText("");
		diagnosis.setText("Chưa có dữ liệu");
		diagnosis.setAlignment(Pos.CENTER);
		prescriptionDetailList.clear();
		patientId = "";
    }
    
    public ObservableList<PrescriptionDetail> searchPrescriptionDatabase(String id) {
    	String sql = "SELECT p.patient_id, m.medicine_name, "
    			+ "pre.medicine_amount, pre.medicine_usage,d.diagnosis "
    			+ "FROM `prescriptiondetail` pre JOIN medicine m "
    			+ "ON pre.medicine_id = m.medicine_id "
    			+ "JOIN diagnosis d ON pre.prescription_id = d.prescription_id JOIN patients p "
    			+ "ON p.patient_id = d.patient_id WHERE pre.prescription_id = ?";
    	ObservableList<PrescriptionDetail> list = FXCollections.observableArrayList();
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1, id);
    		ResultSet rs = ps.executeQuery();
    		int num = 1;
    		while(rs.next()) {
    			patientId = rs.getString("patient_id");
    			String medicineName = rs.getString("medicine_name");
    			int quantity = rs.getInt("medicine_amount");
    			String usage = rs.getString("medicine_usage");
    			diagnosisText = rs.getString("diagnosis");
    			PrescriptionDetail pd = new PrescriptionDetail(num++, medicineName, quantity, usage);
    			list.add(pd);
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public Patient getPatientInfo() {
    	String sql = "SELECT * FROM patients WHERE patient_id = ?";
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1, patientId);
    		ResultSet rs = ps.executeQuery();
    		if (rs.next()) {
    			String patientId = rs.getString("patient_id");
    			String patientName = rs.getString("patient_name");
    			int age = rs.getInt("age");
    			float weight = rs.getFloat("weight");
    			String address = rs.getString("address");
    			String phoneNumber = rs.getString("phone_number");
    			return new Patient(patientId, patientName, age, weight, phoneNumber, address);
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public void displayPrescription() {
    	orderNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
    	medicineNameColumn.setCellValueFactory(cellData -> cellData.getValue().getMedicineName());
    	quantityColumn.setCellValueFactory(cellData -> cellData.getValue().getMedicineQuantity().asObject());
    	usageColumn.setCellValueFactory(cellData -> cellData.getValue().getUsage());
    	
    	prescriptionTable.setItems(prescriptionDetailList);
    }
}
