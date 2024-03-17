package controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import entity.ActiveStateUtils;
import entity.AnimationUtils;
import entity.Diagnosis;
import entity.Patient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.Database;

public class ElectricRecordController implements Initializable{

    @FXML
    private ScrollPane historyContainer;
	
    @FXML
    private AnchorPane informationContainer;

    @FXML
    private AnchorPane initialContainer;

    @FXML
    private Button viewHistoryButton;

    @FXML
    private Button viewInformationButton;
    
    //Thông tin cá nhân
    @FXML
    private Label addressField;

    @FXML
    private Label ageField;
    
    @FXML
    private Label nameField;

    @FXML
    private Label phoneField;
    
    @FXML
    private Label weightField;
    
    //Hiện thị lịch sử khám bệnh
    @FXML
    private TableColumn<Diagnosis, String> dateColumn;

    @FXML
    private TableColumn<Diagnosis, String> diagnosisColumn;
    
    @FXML
    private TableView<Diagnosis> patientHistoryTable;
    
    
    //user-defined variables
    
    private Patient choosedPatient;

    private ObservableList<Diagnosis> diagnosisList; //Chuẩn đoán của bệnh nhân
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//khi chuyển sang tab tình trạng bệnh và in bệnh nhân thì sẽ ra null
		//Vì hàm setPatientList vẫn chưa được gọi. Hàm initialize được gọi trước mọi
		//Hàm. Nên để chạy đúng thì cho lệnh lấy dữ liệu chạy sau khi hàm khởi tạo chạy
		//xong
		Platform.runLater(()->{
			System.out.println("Benh nhan duoc chon la: "+choosedPatient);
			if (choosedPatient == null) {
				initialContainer.setVisible(true);
				informationContainer.setVisible(false);
				historyContainer.setVisible(false);
			} else {
				ActiveStateUtils.addActiveForTab(viewInformationButton);
				showPatientInformation();
				diagnosisList = fetchDiagnosisData(choosedPatient.getPatientIdValue());
				showPatientHistory(diagnosisList);
				initialContainer.setVisible(false);
				informationContainer.setVisible(true);
				historyContainer.setVisible(false);
			}
		});
		
		patientHistoryTable.setRowFactory(tv->{
			TableRow<Diagnosis> row = new TableRow<>();
			
			//Tạo một nút Text để tính toán chiều cao
			Text text = new Text();
			row.itemProperty().addListener((obs, oldItem, newItem) -> {
				if (newItem != null) {
					text.setText(newItem.getDiagnosisValue());
					row.setPrefHeight(text.getBoundsInLocal().getHeight() + 10);
				}
			});
			return row;
		});
		
		//Sử dụng WrapText để cho phép tự động xuống dòng
		diagnosisColumn.setCellFactory(tc -> {
			TableCell<Diagnosis, String> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			//Tự động xuống dòng nếu vượt quá chiều rộng của ô
			text.wrappingWidthProperty().bind(diagnosisColumn.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});
		
		Label initial = new Label("Chưa có dữ liệu");
		initial.setFont(new Font(17));
		initial.getStyleClass().add("diagnosis-content");
		patientHistoryTable.setPlaceholder(initial);
		
	}
    
    public void setPatientList(Patient e) {
    	choosedPatient = e;
    }
    public void chooseRow() {
    	Diagnosis item = patientHistoryTable.getSelectionModel().getSelectedItem();
    	if (item!=null) {
        	showDiagnosisDetail(item);
    	} else {
    		System.out.println("Nguoi nay kham lan dau tai day");
    	}
    }
    //Chuyển tab
    public void switchTab(ActionEvent e) {
    	if (e.getSource().equals(viewInformationButton) && choosedPatient!=null) {
    		//Hiển thị các pane cần thiết
    		informationContainer.setVisible(true);
    		//Ẩn các pane cần thiết
    		historyContainer.setVisible(false);
    		initialContainer.setVisible(false);
    		//Thêm hiệu ứng chuyển màn hình
    		AnimationUtils.createFadeTransition(informationContainer, 0.0, 10.0);
    		//Thêm trạng thái active cho tab
    		ActiveStateUtils.addActiveForTab(viewInformationButton);
    		//Xóa các trạng thái của tab khác
    		ActiveStateUtils.removeActiveForTab(viewHistoryButton);
    		
    	} else if (e.getSource().equals(viewHistoryButton) && choosedPatient!=null) {
    		//Hiển thị các pane cần thiết
    		historyContainer.setVisible(true);
    		//Ẩn các pane cần thiết
    		informationContainer.setVisible(false);
    		initialContainer.setVisible(false);
    		//Thêm hiệu ứng chuyển màn hình
    		AnimationUtils.createFadeTransition(historyContainer, 0.0, 10.0);
    		//Thêm trạng thái active cho tab
    		ActiveStateUtils.addActiveForTab(viewHistoryButton);
    		//Xóa các trạng thái của tab khác
    		ActiveStateUtils.removeActiveForTab(viewInformationButton);
    	}
    }
    //Hiển thị các thông tin cá nhân của bệnh nhân
    public void showPatientInformation() {
    	nameField.setText(choosedPatient.getPatientNameValue());
    	ageField.setText(String.valueOf(choosedPatient.getPatientAgeValue())+" Tuổi");
    	weightField.setText(String.valueOf(choosedPatient.getPatientWeightValue())+" KG");
    	phoneField.setText(choosedPatient.getPatientPhoneNumberValue());
    	addressField.setText(choosedPatient.getPatientAddressValue());
    }
    //Hiển thị bảng lịch sử khám bệnh gồm hai trường chuẩn đoán và ngày khám
    public void showPatientHistory(ObservableList<Diagnosis> list) {
    	diagnosisColumn.setCellValueFactory(cellData->cellData.getValue().getDiagnosis());
    	dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDiagnosisDate());
    	patientHistoryTable.setItems(list);
    }
    
    //Nạp dữ liệu về các lần khám trước đó của bệnh nhân
    public ObservableList<Diagnosis> fetchDiagnosisData(String patientId) {
//    	String sql = "SELECT * FROM diagnosis WHERE patient_id = ? ORDER BY diagnosis_date DESC";
    	String sql = "SELECT dig.*, doc.doctor_name "
    			+ "FROM diagnosis dig JOIN doctors doc "
    			+ "ON dig.doctor_id = doc.doctor_id "
    			+ "WHERE patient_id = ? ORDER BY diagnosis_date DESC";
    	ObservableList<Diagnosis> diagnosisList = FXCollections.observableArrayList();
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1, patientId);
    		ResultSet rs = ps.executeQuery();
    		
    		int diagnosisId;
    		String patientName;
    		String doctorName;
    		String diagnosis;
    		String testResult;
    		String notes;
    		String diagnosisDate;
    		String prescriptionId;
    		
    		while(rs.next()) {
    			diagnosisId = rs.getInt("diagnosis_id");
    			diagnosis = rs.getString("diagnosis");
    			testResult = rs.getString("test_result");
    			notes = rs.getString("notes");
    			patientName = choosedPatient.getPatientNameValue();
    			doctorName = rs.getString("doctor_name");
    			prescriptionId = rs.getString("prescription_id");
    			// Lấy giá trị kiểu java.sql.Timestamp từ cơ sở dữ liệu
                java.sql.Timestamp timestamp = rs.getTimestamp("diagnosis_date");
                // Chuyển đổi thành kiểu java.time.LocalDate để chỉ lấy ngày
                LocalDate date = timestamp.toLocalDateTime().toLocalDate();
                //Chuyển đổi định dạng ngày sang dd-MM-yyyy
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String formattedDate = date.format(formatter);
                diagnosisDate = String.valueOf(formattedDate);
                Diagnosis diag = new Diagnosis(diagnosisId,patientName, doctorName, diagnosis, testResult, notes, diagnosisDate, prescriptionId);
                //Danh sách chứa các lần chuẩn đoán, mỗi lần chuẩn đoán là một lần khám bệnh
                diagnosisList.add(diag);
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return diagnosisList;
    }
    public void showDiagnosisDetail(Diagnosis data) {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DiagnosisDetailScreen.fxml"));
		String css = this.getClass().getResource("/css/style.css").toExternalForm();
		try {
			AnchorPane detailPane = loader.load();
			DiagnosisDetailController controller = loader.getController();
			controller.setDiagnosisData(data);
			Stage newStage = new Stage();
			Scene scene = new Scene(detailPane);
			scene.getStylesheets().add(css);
			newStage.setScene(scene);
			newStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
}
