package controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import entity.Message;
import entity.Patient;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.Database;

public class PatientFormController implements Initializable{

	@FXML
    private Label asideBarTitle;

    @FXML
    private Button clearAddressBtn;

    @FXML
    private Button clearAgeBtn;

    @FXML
    private Button clearDateBtn;

    @FXML
    private Button clearNameBtn;

    @FXML
    private Button clearPhoneBtn;

    @FXML
    private Button clearWeightBtn;

    @FXML
    private TextArea patientAddressField;

    @FXML
    private TextField patientAgeField;

    @FXML
    private DatePicker patientDateField;

    @FXML
    private TextField patientNameField;

    @FXML
    private TextField patientPhoneField;

    @FXML
    private TextField patientWeightField;

    @FXML
    private TextField orderNumber;
    
    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;
    
    //User-defined variable
    
    private ReceptionistController mainController;
    
    private Consumer<Patient> patientAddedCallback;
    
//    private LocalDate date;
    
    private int order;
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
//    	patientDateField.valueProperty().addListener(e->{
//    		date = patientDateField.getValue();
//    	});
    	patientDateField.setValue(LocalDate.now());
    	patientDateField.setEditable(false);
		Platform.runLater(()->{
			setAutoCompleteFill();
			order = generateOrderNumber();
			orderNumber.setText(String.valueOf(order));
		});
		
	}
    //Liên kết với ReceptionistController để liên kết với table view khi thêm phần tử mới
    //trong stage này thì bên table view sẽ tự động cập nhật
    //Bằng cách gửi một hàm callback tới PatientForm để có thể truyền tham số
    //sau
    public void setReceptionistController(ReceptionistController mainController) {
    	this.patientAddedCallback = mainController::addPatientToList;
    	this.mainController = mainController;
    }
    
    public void addPatient() {
    	if (checkUserInput()==1) {
    		Message.showMessage("Vui lòng điền đầy đủ thông tin", AlertType.ERROR);
    	} else if(checkUserInput()==2) {
    		Message.showMessage("Vui lòng nhập đúng định dạng", AlertType.ERROR);
    	} else {
    		LocalDateTime currentDateTime = LocalDateTime.now();
    		DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    		String date = currentDateTime.format(formattedDate);
        	String name = patientNameField.getText();
        	int age = Integer.parseInt(patientAgeField.getText());
        	float weight = Float.parseFloat(patientWeightField.getText());
        	String address = patientAddressField.getText();
        	String phone = patientPhoneField.getText();
    		String id = isExisted(name, phone)? getPatientId(name, phone):generateId();
        	System.out.println("Ngày khám là: "+formattedDate);
        	Patient newPatient = new Patient(id, name, age, weight, address, phone,order);
        	System.out.println(newPatient);
        	if (checkExistingPatientInTable(newPatient)) {
        		Message.showMessage("Đã thêm bệnh nhân này rồi", AlertType.ERROR);
        	} else {
        		patientAddedCallback.accept(newPatient);
            	mainController.setPatientCount();
            	//Thêm dữ liệu vào cơ sở dữ liệu, nếu bệnh nhân đã có thì chỉ cần cập nhật
            	if (isExisted(name, phone)) {
            		updatePatientData(newPatient, Timestamp.valueOf(date));
            	} else {
            		uploadPatientData(newPatient, Timestamp.valueOf(date));
            	}
            	Message.showMessage("Thêm bệnh nhân thành công", AlertType.INFORMATION);
            	clear();
            	order = generateOrderNumber();
            	orderNumber.setText(String.valueOf(order));
        	}
    	}
    }
    
    public void close() {
    	Stage stage = (Stage) patientAgeField.getScene().getWindow();
    	stage.close();
    }
    //Hàm tạo Id cho bệnh nhân
    public String generateId() {
    	//Tạo Id cho người mới hoàn toàn vì trên CSDL nếu dùng lệnh thông thường
    	//thì khi tạo tới a9 thì sau đó a10 không nằm ở dòng cuối cùng nên tạo ID bị sai
    	String sql = "SELECT * FROM patients ORDER BY LENGTH(patient_id), patient_id;";
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ResultSet rs = ps.executeQuery();
    		if (rs.last()) {
    			char text = rs.getString(1).charAt(0);
    			int number = Integer.parseInt(rs.getString(1).substring(1));
    			if (number == 9999) {
    				++text;
    				number = 1;
    			} else {
    				++number;
    			}
    			String id = String.valueOf(text).concat(String.valueOf(number));
    			return id;
    			
    		} else {
    			String id = "a1";
    			return id;
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    //Hàm lấy Id của bệnh nhân đã khám rồi
    public String getPatientId(String patientName, String phoneNumber) {
    	String sql = "SELECT patient_id FROM patients "
    			+ "WHERE patient_name = ? AND phone_number = ?";
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1,patientName);
    		ps.setString(2,phoneNumber);
    		ResultSet rs = ps.executeQuery();
    		if (rs.next()) {
    			return rs.getString(1);
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    //Hàm kiểm tra bệnh nhân đã tồn tại trên cơ sở dữ liệu chưa
    public boolean isExisted(String name, String phone) {
    	String sql = "SELECT * FROM patients WHERE patient_name = ? AND phone_number = ?";
    	try (Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1, name);
    		ps.setString(2, phone);
    		ResultSet rs = ps.executeQuery();
    		if (rs.next()) {
    			return true;
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return false;
    }
    //Hàm tạo số thứ tự dựa trên thông tin trên cơ sở dữ liệu
    //nếu mà trong ngày hiện tại mà truy xuất không có dòng nào thì chưa có ai đăng
    //ký khám bệnh nên tự động tạo số thứ tự 1 đầu tiên. Sau đó sẽ kiểm tra xem dòng
    //cuối cùng là số mấy và tăng lên một đơn vị. Qua ngày mới sẽ reset lại giá trị
    //số thứ tự
    public int generateOrderNumber() {
    	int orderNumber = 1;
    	String currentDate = mainController.getCurrentDate().toString();
    	String sql = "SELECT h.order_number FROM patients p "
    			+ "JOIN history h "
    			+ "ON p.patient_id = h.patient_id "
    			+ "WHERE DATE(h.date) = ? "
    			+ "ORDER BY h.date";
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1, currentDate);
    		ResultSet rs = ps.executeQuery();
    		//Nếu không có dòng cuối cùng có nghĩa là không có dòng nào được trả về
    		//thì lấy số thứ tự bên trên
    		if (rs.last()) {
    			orderNumber = rs.getInt(1)+1;
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return orderNumber;
    }
    public void clear() {
    	patientNameField.setText("");
    	patientAgeField.setText("");
    	patientAddressField.setText("");
    	patientPhoneField.setText("");
    	patientWeightField.setText("");
    	patientDateField.setValue(LocalDate.now());
    }
    public void clearSpecificField(ActionEvent e) {
    	if (e.getSource().equals(clearNameBtn)) {
    		patientNameField.setText("");
    	} else if (e.getSource().equals(clearAgeBtn)) {
    		patientAgeField.setText("");
    	} else if (e.getSource().equals(clearAddressBtn)) {
    		patientAddressField.setText("");
    	} else if (e.getSource().equals(clearPhoneBtn)) {
    		patientPhoneField.setText("");
    	} else if (e.getSource().equals(clearWeightBtn)) {
    		patientWeightField.setText("");
    	} else if (e.getSource().equals(clearDateBtn)) {
    		patientDateField.setValue(null);
    	}
    }
    //Hàm tự động gợi ý nhập dựa trên dữ liệu trên cơ sở dữ liệu
    public void setAutoCompleteFill() {
    	//Lấy danh sách các patient và sau đó tạo thành một mảng gồm tên của
    	//bệnh nhân. Khi người dùng nhập chọn vào tên thì sẽ tự động điền các
    	//Trường còn lại
    	ObservableList<Patient> listOfPatient = mainController.fetchPatientData(null);
		ArrayList<String> patientName = new ArrayList<String>();
		listOfPatient.forEach(e->{
			patientName.add(e.getPatientNameValue());
		});
		AutoCompletionBinding<String> autoCompletionBinding = TextFields.bindAutoCompletion(patientNameField, patientName);
		//Hàm setOnAutoCompleted dùng để thực hiện hành động sau khi chọn thông tin
		//gợi ý
		autoCompletionBinding.setOnAutoCompleted(e->{
			//Tìm kiếm đối tượng patient mà có tên bệnh nhân trùng với giá trị
			//mà người dùng chọn, sau đó setText() cho các trường còn lại.
			Patient person = findPatientByName(listOfPatient);
			patientAgeField.setText(String.valueOf(person.getPatientAgeValue()));
			patientAddressField.setText(person.getPatientAddressValue());
			patientPhoneField.setText(person.getPatientPhoneNumberValue());
			patientWeightField.setText(String.valueOf(person.getPatientWeightValue()));
			patientDateField.setValue(LocalDate.now());
		});
    }
    //hàm tìm kiếm đối tượng patient dựa trên thông tin gợi ý mà người dùng đã chọn
	public Patient findPatientByName(ObservableList<Patient> list) {
		Patient person = null;
		for(Patient item: list) {
			if (item.getPatientNameValue().equals(patientNameField.getText())) {
				person = item;
				break;
			}
		}
		return person;
	}
	//Hàm kiểm tra có thêm phần tử trùng trong ObservableList không
	//Nếu trùng thì trả về true, ngược lại không trùng là false
	public boolean checkExistingPatientInTable(Patient patient) {
		boolean isExisting = false;
		ObservableList<Patient> patientList = mainController.getPatientList();
		for(Patient e: patientList) {
			if (e.isSameAs(patient)) {
				isExisting = true;
			}
		}
		return isExisting;
	}
	//Hàm kiểm tra đầu vào nếu bằng 0 thì hoàn toàn không có lỗi
	//Nếu khác 0 là có lỗi
	public int checkUserInput() {
		Pattern stringReg = Pattern.compile("[\\p{L} \\p{Mn} \\p{Mc}]+");
		Pattern numberReg = Pattern.compile("\\d+");
		Pattern floatReg = Pattern.compile("[0-9]+[.]{0,1}[0-9]*");
		Pattern addressReg = Pattern.compile("[\\p{L} \\p{Mn} \\p{Mc}\\s-_]+");
		if (patientNameField.getText().isEmpty() || patientAgeField.getText().isEmpty()
			|| patientAddressField.getText().isEmpty() || patientPhoneField.getText().isEmpty()
			|| patientWeightField.getText().isEmpty() || patientDateField.getValue().equals(null)) {
			return 1;
		} else if (!stringReg.matcher(patientNameField.getText()).matches()
				|| !numberReg.matcher(patientAgeField.getText()).matches()
				|| !addressReg.matcher(patientAddressField.getText()).matches()
				|| !numberReg.matcher(patientPhoneField.getText()).matches()
				|| !floatReg.matcher(patientWeightField.getText()).matches()
				) {
			return 2;
		}
		return 0;
	}
	//Hàm thêm bệnh nhân cho người mới hoàn toàn
	public void uploadPatientData(Patient patient, Timestamp date) {
		String info = "INSERT INTO patients VALUES(?,?,?,?,?,?)";
		String datetime = "INSERT INTO history(patient_id, date, order_number, status)VALUES(?,?,?,0)";
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(info);
			ps.setString(1, patient.getPatientIdValue());
			ps.setString(2, patient.getPatientNameValue());
			ps.setInt(3, patient.getPatientAgeValue());
			ps.setFloat(4, patient.getPatientWeightValue());
			ps.setString(5, patient.getPatientAddressValue());
			ps.setString(6, patient.getPatientPhoneNumberValue());
			int upload = ps.executeUpdate();
			if (upload==1) {
				System.out.println("Đã thêm bệnh nhân: "+patient);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(datetime);
			ps.setString(1, patient.getPatientIdValue());
			ps.setTimestamp(2, date);
			ps.setInt(3,patient.getOrderNumberValue());
			int upload = ps.executeUpdate();
			if (upload == 1) {
				System.out.println("Đã thêm vào lịch sử khám");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void updatePatientData(Patient patient, Timestamp date) {
		String updateInfo = "UPDATE patients SET age = ?, weight = ? WHERE patient_id = ?";
		String updateDate = "INSERT INTO history(patient_id,date,order_number, status) VALUES(?,?,?,0)";
		//Cập nhật tuổi và cân nặng cho bệnh nhân đã từng khám
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(updateInfo);
			ps.setInt(1, patient.getPatientAgeValue());
			ps.setFloat(2, patient.getPatientWeightValue());
			ps.setString(3, patient.getPatientIdValue());
			int update = ps.executeUpdate();
			if (update == 1) {
				System.out.println("Đã cập nhập đối tượng: "+patient);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		//Thêm thời gian khám mới cho bệnh nhân đã khám rồi
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(updateDate);
			ps.setString(1, patient.getPatientIdValue());
			ps.setTimestamp(2, date);
			ps.setInt(3, patient.getOrderNumberValue());
			int update = ps.executeUpdate();
			if (update == 1) {
				System.out.println("Đã cập nhật đối tượng và thời gian");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
