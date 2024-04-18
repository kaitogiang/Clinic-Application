package controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

import entity.Message;
import entity.Patient;
import entity.Pharmacist;
import entity.PrescriptionDetail;
import entity.SelectionPrescriptionButton;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.Database;

public class InvoiceController implements Initializable{

	@FXML
	private Label address;
	 
    @FXML
    private Label date;
    
    @FXML
    private Label doctorName;
    
    @FXML
    private Label invoiceId;
    
    @FXML
    private Label patientName;
    
    @FXML
    private TableView<PrescriptionDetail> prescriptionTable;

    @FXML
    private TableColumn<PrescriptionDetail, Float> priceColumn;

    @FXML
    private TableColumn<PrescriptionDetail, Integer> quantityColumn;

    @FXML
    private TableColumn<PrescriptionDetail, Float> totalAmount;
    
    @FXML
    private TableColumn<PrescriptionDetail, String> medicineNameColumn;

    @FXML
    private TableColumn<PrescriptionDetail, Integer> orderNumberColumn;

    @FXML
    private VBox vboxContainer;
    
	//user-defined variable
    private LocalDate currentDate;
    
    private Patient patient;
    
    private Pharmacist pharmacist;
    
    private String prescriptionId;
    
    private String id;
    
    private ObservableList<PrescriptionDetail> medicineList;
    
    private HBox totalContainer;
    
    private Label totalTitle;
    
    private Label totalLabel;
    
    private float sum = 0; 
    
    private PharmacistController parentController;
    
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
    }
    
    @SuppressWarnings("unchecked")
	public void setData(Map<String, Object> data) {
    	//Thiết lập ngày tháng name
    	currentDate = (LocalDate) data.get("date");
    	patient = (Patient) data.get("patient");
    	pharmacist = (Pharmacist) data.get("pharmacist");
    	id = (String) data.get("invoiceId");
    	prescriptionId = (String) data.get("prescriptionId");
    	medicineList = (ObservableList<PrescriptionDetail>) data.get("medicineList");
    	//Gán giá trị tương ứng với các nhãn
    	date.setText("Ngày "+currentDate.getDayOfMonth()+" tháng "+currentDate.getMonthValue()+" năm "+currentDate.getYear());
    	patientName.setText(patient.getPatientNameValue().toUpperCase());
    	address.setText(patient.getPatientAddressValue());
    	this.invoiceId.setText("ID hóa đơn: "+id);
    	System.out.println(medicineList.size());
    	displayPrescriptionDetail();
    	for(PrescriptionDetail e: medicineList) {
    		sum+= e.getMedicineQuantityValue()*e.getMedicineValue().getUnitPriceValue();
    	}
    	totalLabel.setText(String.valueOf(sum)+" VNĐ");
    }
    
    public void setparentController(PharmacistController controller) {
    	parentController = controller;
    }
    
    //Hàm hiển thị danh sách thuốc trong đơn thuốc
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
    //Hàm thanh toán hóa đơn cho bệnh nhân
    public void payInvoice() {
    	if (isPaiedInvoice(id)) {
    		Message.showMessage("Đơn này đã thanh toán!!", AlertType.ERROR);
    	} else {
    		String sql = "INSERT INTO invoice VALUES(?,?,?,NOW(),?)";
    		try(Connection con = Database.connectDB()) {
    			PreparedStatement ps = con.prepareStatement(sql);
    			ps.setString(1, id);
    			ps.setString(2, prescriptionId);
    			ps.setString(3, pharmacist.getPharmacistId());
    			ps.setFloat(4, sum);
    			int insert = ps.executeUpdate();
    			if (insert != 0) System.out.println("Them hoa don thanh cong");
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    		SelectionPrescriptionButton selectedButton = parentController.getPressedButtonQueue().getLast();
        	selectedButton.setSuccessStatus();
        	Message.showMessage("Thanh toán hóa đơn thành công", AlertType.INFORMATION);
    	}
    }
    //Hàm kiểm tra hóa đơn này đã thanh toán chưa
    public boolean isPaiedInvoice(String invoiceId) {
    	String sql = "SELECT * FROM invoice WHERE invoice_id = ?";
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1, invoiceId);
    		ResultSet rs = ps.executeQuery();
    		if (rs.next()) {
    			return true;
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return false;
    }
    //Hàm in hóa đơn
    public void exportInvoice() {
    	System.out.println("In hoa don");
    }
    
}
