package controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.Medicine;
import entity.Message;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.Database;

public class MedicineFormController implements Initializable{

	    @FXML
	    private Label asideBarTitle;

	 	@FXML
	    private TextArea descriptionField;

	    @FXML
	    private TextField dosageField;

	    @FXML
	    private TextField dosageUnitField;

	    @FXML
	    private DatePicker expirationField;

	    @FXML
	    private TextField manufacturerField;

	    @FXML
	    private TextField medicineNameField;

	    @FXML
	    private TextField stockQuantityField;

	    @FXML
	    private TextField unitPriceField;

	    @FXML
	    private Button addingBtn;

	    @FXML
	    private Button updatingBtn;
	    
	    @FXML
	    private Button removeAllBtn;
	    
	    //user-defined variables
	    
	    private PharmacistController mainController;
	    
	    private Consumer<Medicine> medicineAddedCallback;
	    
	    private BiConsumer<Integer, Medicine> medicineEditingCallback;
	    
	    private Medicine oldMedicine;
	    
	    private TableView<Medicine> currentTable;
	    
	    public int selectedIndex;
	    
		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {

		}
	    
	    public void setParentController(PharmacistController controller) {
	    	this.medicineAddedCallback = controller::addMedicineToList;
	    	this.medicineEditingCallback = controller::updateMedicineList;
	    	this.mainController = controller;
	    }
	    //Kiểm tra các trường có rỗng không, có 1 trường rỗng thì trả về true
	    public boolean isEmptyField() {
	    	if (medicineNameField.getText().isEmpty() || descriptionField.getText().isEmpty()
	    		|| stockQuantityField.getText().isEmpty() || manufacturerField.getText().isEmpty()
	    		|| dosageField.getText().isEmpty() || dosageUnitField.getText().isEmpty() 
	    		|| unitPriceField.getText().isEmpty() || expirationField.getValue() == null) {
	    		return true;
	    	}
	    	return false;
	    }
	    //Kiểm tra định dạng cho các trường nhập kiểu số
	    public boolean isNotNumberFormat() {
	    	TextField[] numberFieldList = {stockQuantityField, dosageField};
	    	Pattern p = Pattern.compile("\\d+");
			Pattern floatReg = Pattern.compile("[0-9]+[.]{0,1}[0-9]*");
	    	for(TextField e: numberFieldList) {
	    		Matcher m = p.matcher(e.getText());
	    		if (!m.matches()) {
	    			return true;
	    		}
	    	}
	    	Matcher m = floatReg.matcher(unitPriceField.getText());
	    	return !m.matches();
	    }
	    
	    public void addMedicine() {
	    	if (isEmptyField()) {
	    		Message.showMessage("Vui lòng điền đầy đủ thông tin", AlertType.ERROR);
	    	} else if (isNotNumberFormat()) {
	    		Message.showMessage("Vui lòng nhập đúng định dạng", AlertType.ERROR);
	    	} else {
	    		String id = generateUniqueId();
	    		int orderNumber = mainController.getMedicineListSize() + 1;
	    		String name = medicineNameField.getText();
	    		String description = descriptionField.getText();
	    		String manufacturer = manufacturerField.getText();
	    		int dosage = Integer.valueOf(dosageField.getText());
	    		String dosageUnit = dosageUnitField.getText();
	    		float unitPrice = Float.valueOf(unitPriceField.getText());
	    		String expiration = expirationField.getValue().toString();
	    		Medicine med = new Medicine(id, orderNumber, name, description, manufacturer, dosage, dosageUnit, unitPrice, expiration, dosage);
	    		medicineAddedCallback.accept(med);
	    		uploadMedicineToDatabase(med);
	    		resetAllField();
	    		Message.showMessage("Thêm thuốc thành công", AlertType.INFORMATION);
	    		System.out.println("Adding the medicine successfully!! ");

	    	}
	    }
	    //Hàm tạo ra một id duy nhất
	    public String generateUniqueId() {
	    	UUID uniqueId;
	    	String shortId;
	    	do {
	    		// Tạo một UUID mới
		        uniqueId = UUID.randomUUID();

		        // Chuyển UUID thành chuỗi và lấy 5 ký tự đầu tiên
		        shortId = uniqueId.toString().substring(0, 5);
		        
	    	} while(isExistedId(shortId));

	        // In mã ID ngắn
	        System.out.println("Short ID: " + shortId);
	        
	        return shortId;
	    }
	    public void resetAllField() {
	    	medicineNameField.setText("");
	    	descriptionField.setText("");
	    	stockQuantityField.setText("");
	    	manufacturerField.setText("");
	    	dosageField.setText("");
	    	dosageUnitField.setText("");
	    	unitPriceField.setText("");
	    	expirationField.setValue(null);
	    }
	    //hàm kiểm tra trùng id, trả về true nếu trùng
	    public boolean isExistedId(String id) {
	    	String sql = "SELECT * FROM medicine WHERE medicine_id = ?";
	    	try(Connection con = Database.connectDB()) {
	    		PreparedStatement ps = con.prepareStatement(sql);
	    		ps.setString(1, id);
	    		ResultSet rs = ps.executeQuery();
	    		return rs.next();
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	return false;
	    }
	    //Thêm thuốc vào cơ sở dữ liệu
	    public void uploadMedicineToDatabase(Medicine medicine) {
	    	String sql = "INSERT INTO medicine VALUES(?,?,?,?,?,?,?,?,?,?)";
	    	try(Connection con = Database.connectDB()) {
	    		PreparedStatement ps = con.prepareStatement(sql);
	    		ps.setString(1, medicine.getMedicineIdValue());
	    		ps.setString(2, medicine.getMedicineNameValue());
	    		ps.setString(3, medicine.getDescriptionValue());
	    		ps.setString(4, medicine.getManufacturerValue());
	    		ps.setInt(5, medicine.getDosageValue());
	    		ps.setString(6, medicine.getDosageUnitValue());
	    		ps.setFloat(7, medicine.getUnitPriceValue());
	    		ps.setDate(8, java.sql.Date.valueOf(medicine.getExpirationDateValue()));
	    		ps.setInt(9,medicine.getQuantityValue());
	    		ps.setInt(10, medicine.getQuantityValue());
	    		int insert = ps.executeUpdate();
	    		if (insert != 0) System.out.println("insert new medicine successfully!!");
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    //Hàm thoát khỏi form
	    public void exit() {
	    	Stage currentStage = (Stage) dosageField.getScene().getWindow();
	    	currentStage.close();
	    }
	    
	    public void setMedicineForm(Medicine medicine, TableView<Medicine> currentTable, int selectedIndex) {
	    	this.selectedIndex = selectedIndex;
	    	asideBarTitle.setText("Chỉnh sửa thuốc");
	    	//Chuyển sang form cập nhật
	    	addingBtn.setVisible(false);
	    	updatingBtn.setVisible(true);
	    	removeAllBtn.setVisible(false);
	    	//Khởi tạo các giá trị có sẳn của thuốc đã chọn
	    	medicineNameField.setText(medicine.getMedicineNameValue());
	    	descriptionField.setText(medicine.getDescriptionValue());
	    	stockQuantityField.setText(String.valueOf(medicine.getQuantityValue()));
	    	manufacturerField.setText(medicine.getManufacturerValue());
	    	dosageField.setText(String.valueOf(medicine.getDosageValue()));
	    	dosageUnitField.setText(medicine.getDosageUnitValue());
	    	unitPriceField.setText(String.valueOf(medicine.getUnitPriceValue()));
	    	// Tạo một đối tượng DateTimeFormatter với định dạng ngày cũ
	    	DateTimeFormatter oldFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	    	String dateString = medicine.getExpirationDateValue();
	    	LocalDate date = LocalDate.parse(dateString,oldFormat);
	    	// Tạo một đối tượng DateTimeFormatter với định dạng ngày mới
	    	DateTimeFormatter newFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    	expirationField.setValue(LocalDate.parse(date.format(newFormat)));
	    	//Loại bỏ focus các trường
	    	medicineNameField.setFocusTraversable(false);
	    	descriptionField.setFocusTraversable(false);
	    	stockQuantityField.setFocusTraversable(false);
	    	manufacturerField.setFocusTraversable(false);
	    	dosageField.setFocusTraversable(false);
	    	dosageUnitField.setFocusTraversable(false);
	    	unitPriceField.setFocusTraversable(false);
	    	
	    	//Giữ tham chiếu của biến medicine cũ được truyền vào
	    	oldMedicine = medicine;
	    	this.currentTable = currentTable;
	    }
	    
	    public void updateMedicine() {
	    	if (isEmptyField()) {
	    		Message.showMessage("Vui lòng điền đầy đủ thông tin", AlertType.ERROR);
	    	} else if (isNotNumberFormat()) {
	    		Message.showMessage("Vui lòng nhập đúng định dạng", AlertType.ERROR);
	    	} else {
	    		int orderNumber = oldMedicine.getOrderNumberVaue();
		    	String medicineId = oldMedicine.getMedicineIdValue();
		    	String medicineName = medicineNameField.getText();
		    	String description = descriptionField.getText();
		    	int quantity = Integer.valueOf(stockQuantityField.getText());
		    	String manufacturer = manufacturerField.getText();
		    	int dosage = Integer.valueOf(dosageField.getText());
		    	String unitDosage = dosageUnitField.getText();
		    	float unitPrice = Float.valueOf(unitPriceField.getText());
		    	LocalDate date = expirationField.getValue();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		    	String expiration = date.toString();
		    	Medicine editedMedicine = new Medicine(medicineId, orderNumber, medicineName, description, manufacturer, dosage, unitDosage, unitPrice, expiration, quantity);
		    	updateMedicineToDatabase(editedMedicine);
		    	expiration = date.format(formatter);
		    	editedMedicine.setExpirationDate(expiration);
		    	medicineEditingCallback.accept(selectedIndex, editedMedicine);
		    	System.out.println("Edited");
		    	Message.showMessage("Cập nhật thuốc thành công", AlertType.INFORMATION);
	    	}
	    	
	    }
	    
	    public void updateMedicineToDatabase(Medicine medicine) {
	    	String sql = "UPDATE medicine SET medicine_name = ?, description = ?, "
	    			+ "manufacturer = ?, dosage = ?, dosage_unit = ?, unit_price = ?, "
	    			+ "expiration_date = ?, stock_quantity = ?, original_stock = ? WHERE medicine_id = ?";
	    	try(Connection con = Database.connectDB()) {
	    		PreparedStatement ps = con.prepareStatement(sql);
	    		ps.setString(1, medicine.getMedicineNameValue());
	    		ps.setString(2, medicine.getDescriptionValue());
	    		ps.setString(3, medicine.getManufacturerValue());
	    		ps.setInt(4, medicine.getDosageValue());
	    		ps.setString(5, medicine.getDosageUnitValue());
	    		ps.setFloat(6, medicine.getUnitPriceValue());
	    		ps.setDate(7, java.sql.Date.valueOf(medicine.getExpirationDateValue()));
	    		ps.setInt(8, medicine.getQuantityValue());
	    		ps.setInt(9, medicine.getQuantityValue());
	    		ps.setString(10, medicine.getMedicineIdValue());
	    		int update = ps.executeUpdate();
	    		if (update != 0) System.out.println("Update medicine successfull");
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
}
