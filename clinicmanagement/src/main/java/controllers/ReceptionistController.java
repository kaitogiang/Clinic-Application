package controllers;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.AccentRemover;
import entity.ActiveStateUtils;
import entity.AnimationUtils;
import entity.History;
import entity.ImageUtils;
import entity.LogoutHandler;
import entity.Message;
import entity.Patient;
import entity.Receptionist;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.Database;

public class ReceptionistController implements Initializable{

	@FXML
    private Button addPatientButton;

	@FXML
	private TableView<Patient> patientTable;
	  
    @FXML
    private TableColumn<Patient, String> addressColumn;

    @FXML
    private TableColumn<Patient, Integer> ageColumn;

    @FXML
    private TableColumn<Patient, Integer> orderNumberColumn;

    @FXML
    private TableColumn<Patient, String> patientIdColumn;
    
    @FXML
    private TableColumn<Patient, String> patientNameColumn;

    @FXML
    private TableColumn<Patient, String> phoneNumberColumn;
    
    @FXML
    private TableColumn<Patient, Float> weightColumn;
    
    @FXML
    private Label asideBarTitle;

    @FXML
    private Label date;

    @FXML
    private Label fullNameInAside;

    @FXML
    private Label fullNameInNav;

    @FXML
    private Button logoutButton;

    @FXML
    private Button patientListButton;

    @FXML
    private Button registerPatientButton;

    @FXML
    private Label role;

    @FXML
    private Label time;

    @FXML
    private AnchorPane rootContainer;
    
    @FXML
    private ImageView userAvatar;
    
    @FXML
    private TableView<Patient> medicalHistoryTable;
    
    @FXML
    private TableColumn<Patient, String> addressHistoryColumn;
    
    @FXML
    private TableColumn<Patient, Integer> ageHistoryColumn;
    
    @FXML
    private TableColumn<Patient, String> patientIdHistoryColumn;
    
    @FXML
    private TableColumn<Patient, String> patientNameHistoryColumn;

    @FXML
    private TableColumn<Patient, String> phoneNumberHistoryColumn;
    
    @FXML
    private TableColumn<Patient, Float> weightHistoryColumn;
    
    @FXML
    private HBox searchBarContainer;
    
    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private ChoiceBox<String> sortByBox;
    
    @FXML
    private HBox sortByContainer;

    @FXML
    private ImageView avartar;
    
    @FXML
    private Button editAvatartButton;

    @FXML
    private Button editInfoButton;
    
    @FXML
    private AnchorPane personalContainer;

    @FXML
    private TextField personalEmailField;

    @FXML
    private TextField personalNameField;

    @FXML
    private TextField personalPhoneField;

    @FXML
    private TextField personalPositionField;
    
    @FXML
    private TextArea personalAddressField;
    
    @FXML
    private Button saveInfoButton;
    
    //User-variable defined
    
    private Stage currentStage;
    
    private Scene currentScene;
    
    private ObservableList<Patient> patientList;
        
    private LocalDate currentDate;
    
    private String[] sortType = {"Tất cả","Tên A-Z","Tên Z-A", "Tuổi tăng", "Tuổi giảm"};
    
    private ObservableList<Patient> fullPatientList;
    
    private ObservableList<Patient> sortedPatientList; //Biến chứa đựng danh sách bệnh nhân chưa
    												//sắp xếp và đã sắp xếp
    private String sortCondition; //Loại điều kiện sắp xếp
    Receptionist receptionist;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//Nạp dữ liệu từ cơ sở dữ liệu
		patientList = fetchPatientData(LocalDate.now());
		fullPatientList = fetchPatientData(null);
		sortedPatientList = fetchPatientData(null); //Khởi tạo là toàn bộ danh sách khám bệnh
		System.out.println("Ngay hien tai la: "+LocalDate.now());
		//Gọi hàm hiển thị ngày hiện tại
		showDate();
		//Gọi hàm hiển thị thời gian theo thời gian thực
		showTime();
		Platform.runLater(()->{
			receptionist = (Receptionist)this.currentScene.getUserData();
			fullNameInNav.setText(receptionist.getReceptionistName());
			fullNameInAside.setText(receptionist.getReceptionistName());
			role.setText(receptionist.getRole());
			Image userImage = ImageUtils.retrieveImage(receptionist.getImageId());
			userAvatar.setImage(userImage);
			//Bo góc cho avatar
			Rectangle clip = new Rectangle(65, 66);
			clip.setArcWidth(20);
			clip.setArcHeight(20);
			userAvatar.setClip(clip);
		});
		//nếu giá trị null thì sẽ tự động đánh dấu tab đầu tiên
		switchTab(null);
		//Hiển thị danh sách khám bệnh hôm nay
		displayPatientTable();
		//Hiển thị toàn bộ danh sách khám bệnh từ trước đến giờ và lịch sử khám từng người
		displayFullPatientTable();
		//Khởi tạo các biến cần thiết
		sortByBox.getItems().addAll(sortType);
		//Quan sát sự thay đổi của searchTex, nếu rỗng thì khởi tạo lại danh sách
		searchTextField.textProperty().addListener((observe, oldValue, newValue)->{
			if (newValue.isEmpty()) {
//				fullPatientList = fetchPatientData(null);
				sortedPatientList = fullPatientList;
				sortByCondition(sortCondition); //Gọi hàm này lại để khởi tạo list đúng với giá trị sắp xếp đã chọn
				
				medicalHistoryTable.getItems().clear();
				medicalHistoryTable.getItems().addAll(sortedPatientList);
			}
		});
		
		sortByBox.valueProperty().addListener((observe, oldValue, newValue)->{
			sortCondition = newValue;
			System.out.println(newValue);
			sortByCondition(newValue);
		});
		
		//Đặt giá trị mặc định cho các bảng
		Label content = new Label("Chưa có bệnh nhân nào");
		content.setFont(new Font(20));
		patientTable.setPlaceholder(content);
		medicalHistoryTable.setPlaceholder(content);
		
	}
	
	public ObservableList<Patient> getPatientList() {
		return this.patientList;
	}
	
	public LocalDate getCurrentDate() {
		return this.currentDate;
	}
	public void setPatientList(ObservableList<Patient> list) {
		this.fullPatientList = list;
	}
	public void addToPatientList(Patient e) {
		patientList.add(e);
		System.out.println("Call addToPatientList");
		patientList.forEach(ex->{
			System.out.println(ex.getPatientNameValue());
		});
	}
	
	//Hàm hiển thị chạy giờ
    public void showTime() {
    	Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event->{
    		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    		String formattedTime = sdf.format(new Date());
    		time.setText(formattedTime);
    	}));
    	timeline.setCycleCount(Animation.INDEFINITE);
    	timeline.play();
		
    }
    //Hàm hiển thị thông tin ngày hiện tại
    public void showDate() {
    	LocalDate currentDate = LocalDate.now();
    	this.currentDate = currentDate;
    	String dayOfWeek = currentDate.getDayOfWeek().name();
    	switch(dayOfWeek.toLowerCase()) {
	    	case "monday": dayOfWeek = "Thứ hai"; break;
	    	case "tuesday": dayOfWeek = "Thứ ba"; break;
	    	case "wednesday": dayOfWeek = "Thứ tư"; break;
	    	case "thursday": dayOfWeek = "Thứ năm"; break;
	    	case "friday": dayOfWeek = "Thứ sáu"; break;
	    	case "saturday": dayOfWeek = "Thứ bảy"; break;
	    	case "sunday": dayOfWeek = "Chủ nhật"; break;
    	}
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    	String formattedDate = currentDate.format(formatter);
    	date.setText(dayOfWeek+", "+formattedDate);
    }
	public void setCurrentStage(Stage stage) {
		this.currentStage = stage;
	}
	public Stage getCurrentStage() {
		return this.currentStage;
	}
	public void getValueFromStage() {
		System.out.println(this.currentStage);
	}
	public void setMyScene(Scene scene) {
		this.currentScene = scene;
	}
	public void switchTab(ActionEvent e) {
		if (e==null || e.getSource().equals(registerPatientButton)) {
			int patientCount = patientList.size();
			asideBarTitle.setText("Danh sách đăng ký ("+patientCount+")");
			addPatientButton.setVisible(true);
			sortByContainer.setVisible(false);
			searchBarContainer.setVisible(false);
			patientTable.setVisible(true);
			medicalHistoryTable.setVisible(false);
			personalContainer.setVisible(false);
			//Tạo animation chuyển động cho các nút
			AnimationUtils.createFadeTransition(patientTable, 0.0, 10.0);
			AnimationUtils.createFadeTransition(addPatientButton, 0.0, 10.0);
			ActiveStateUtils.addStyleClass(registerPatientButton);
			ActiveStateUtils.removeStyleClass(patientListButton);
			AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
		} else if(e.getSource().equals(patientListButton) && e!=null) {
			asideBarTitle.setText("Danh sách bệnh nhân");
			addPatientButton.setVisible(false);
			sortByContainer.setVisible(true);
			searchBarContainer.setVisible(true);
			patientTable.setVisible(false);
			medicalHistoryTable.setVisible(true);
			personalContainer.setVisible(false);
			//Tạo animation chuyển động cho các nút
			AnimationUtils.createFadeTransition(medicalHistoryTable, 0.0, 10.0);
			AnimationUtils.createFadeTransition(searchBarContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(sortByContainer, 0.0, 10.0);
			ActiveStateUtils.removeStyleClass(registerPatientButton);
			ActiveStateUtils.addStyleClass(patientListButton);
			AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
			//Cập nhật lại danh sách toàn bộ bệnh nhân
			fullPatientList = fetchPatientData(null);
			medicalHistoryTable.getItems().clear();
			medicalHistoryTable.getItems().addAll(fullPatientList);
			//Khởi tạo lại sortByChoiceBox
			sortByBox.getSelectionModel().select(0);
		}
		
	}
	
	//Chuyển đến tab trang cá nhân của người dùng
	public void goToPersonalInfo() {
		//Ẩn hiện các mục cần thiết
		asideBarTitle.setText("Thông tin cá nhân");
		addPatientButton.setVisible(false);
		sortByContainer.setVisible(false);
		searchBarContainer.setVisible(false);
		patientTable.setVisible(false);
		medicalHistoryTable.setVisible(false);
		saveInfoButton.setVisible(false);
		personalContainer.setVisible(true);
		//Loại bỏ chọn tab
		ActiveStateUtils.removeStyleClass(registerPatientButton);
		ActiveStateUtils.removeStyleClass(patientListButton);
		AnimationUtils.createFadeTransition(personalContainer, 0.0, 10.0);
		//Khởi tạo dữ liệu cho các trường trong thông tin cá nhân
		personalNameField.setText(receptionist.getReceptionistName());
		personalEmailField.setText(receptionist.getReceptionistEmail());
		personalPhoneField.setText(receptionist.getPhone_number());
		personalPositionField.setText(receptionist.getRole());
		personalAddressField.setText(receptionist.getAdress());
		Image userImg = ImageUtils.retrieveImage(receptionist.getImageId());
		avartar.setImage(userImg);
		Rectangle clip = new Rectangle(240, 240);
		clip.setArcWidth(20);
		clip.setArcHeight(20);
		avartar.setClip(clip);
		//Loại bỏ active field cho trường chỉnh sửa
		removeFocusTextField(personalNameField, personalEmailField, personalPhoneField);
		removeFocusTextArea(personalAddressField);
		removeEditTextFieldAndTexArea(personalAddressField, personalNameField, personalEmailField, personalPhoneField);
		
	}
	
	public void logout(ActionEvent e) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginScreen.fxml"));
		LogoutHandler.logout(currentStage, loader);
	}
	
	public ObservableList<Patient> fetchPatientData(LocalDate currentDate) {
		ObservableList<Patient> list = FXCollections.observableArrayList();
		String sql = "";
		if (currentDate==null) {
			sql = "SELECT DISTINCT p.*  FROM `patients` p JOIN history h ON p.patient_id = h.patient_id";
		} else {
			sql = "SELECT p.*, h.order_number FROM `patients` p JOIN history h ON p.patient_id = h.patient_id WHERE DATE(h.date) = ?";
		}
		 
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			if (currentDate != null ) {
				ps.setDate(1, java.sql.Date.valueOf(currentDate));
			}
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String id = rs.getString(1);
				String name = rs.getString(2);
				int age = rs.getInt(3);
				float weight = rs.getFloat(4);
				String address = rs.getString(5);
				String phone = rs.getString(6);
				int order = 0;
				if (currentDate!=null) {
					order = rs.getInt(7);
				}
				list.add(new Patient(id, name, age, weight, address, phone, order));
			}
			return list;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void displayPatientTable() {
		patientIdColumn.setCellValueFactory(cellData -> cellData.getValue().getPatientId());
		orderNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
		patientNameColumn.setCellValueFactory(cellData -> cellData.getValue().getPatientName());
		ageColumn.setCellValueFactory(cellData -> cellData.getValue().getAge().asObject());
		weightColumn.setCellValueFactory(cellData -> cellData.getValue().getWeight().asObject());
		addressColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress());
		phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getPhoneNumber());
		patientTable.setItems(patientList); //setItem dùng để liên kết observablelist và tableview, nếu list thay đổi thì table sẽ thay đổi theo
	}
	public void displayFullPatientTable() {
		patientIdHistoryColumn.setCellValueFactory(cellData -> cellData.getValue().getPatientId());
		patientNameHistoryColumn.setCellValueFactory(cellData -> cellData.getValue().getPatientName());
		ageHistoryColumn.setCellValueFactory(cellData -> cellData.getValue().getAge().asObject());
		weightHistoryColumn.setCellValueFactory(cellValue -> cellValue.getValue().getWeight().asObject());
		addressHistoryColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress());
		phoneNumberHistoryColumn.setCellValueFactory(cellData -> cellData.getValue().getPhoneNumber());
		medicalHistoryTable.setItems(fullPatientList);
	}
	public void setPatientCount() {
		int patientCount = patientList.size();
		asideBarTitle.setText("Danh sách đăng ký ("+patientCount+")");
	}
	
	public void addPatient() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PatientFormScreen.fxml"));
		String css = this.getClass().getResource("/css/style.css").toExternalForm();
		try {
			AnchorPane root = loader.load();
			PatientFormController controller = loader.getController();
			controller.setReceptionistController(this);
			//tương dương với (item) -> patientList.add(item);
			Stage newStage = new Stage();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(css);
			//Tạo icon cho stage
			Image image = new Image(getClass().getResourceAsStream("/images/healthcare.png"));
			newStage.getIcons().add(image);
			newStage.setScene(scene);
			newStage.setTitle("Adding patient form");
			newStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addPatientToList(Patient e) {
		patientList.add(e);
		System.out.println("Call at Recpetionist"+patientList.size());
	}
	//Hàm thực hiện hiển thị thông tin chi tiết của bệnh nhân khi nhấn vào 1 dòng
	public void chooseRow() {
		System.out.println("Click for a row");
		Patient selectedPatient = medicalHistoryTable.getSelectionModel().getSelectedItem();
		System.out.println(selectedPatient);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PatientDetailScreen.fxml"));
		String css = this.getClass().getResource("/css/style.css").toExternalForm();
		List<History> historyList = getHistory(selectedPatient.getPatientIdValue());
		try {
			AnchorPane detailScreen = loader.load();
			PatientDetailController controller = loader.getController();
			controller.setData(selectedPatient, historyList);
			Stage detailStage = new Stage();
			Scene scene = new Scene(detailScreen);
			scene.getStylesheets().add(css);
			Image image = new Image(getClass().getResourceAsStream("/images/healthcare.png"));
			detailStage.getIcons().add(image);
			detailStage.setScene(scene);
			detailStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Hàm tìm kiếm một bệnh nhân nào đó trong danh sách
	public void findPatients(ActionEvent e) {
		String searchText = searchTextField.getText().trim().toLowerCase();
		if (searchText.isEmpty()) {
			System.out.println("Empty Search Text");
		} else {
			
			ObservableList<Patient> filteredList = 
			sortedPatientList.filtered(patient->(patient.toString().toLowerCase().
					contains(searchText)) || AccentRemover.removeDiacritics(patient.toString().toLowerCase()).
					contains(searchText)); //
			sortedPatientList = filteredList; //cập nhật lại sortedPatientList để nếu có sử dụng
												//chức năng sắp xếp khi thực hiện chức năng tìm
			medicalHistoryTable.getItems().clear();
			medicalHistoryTable.getItems().addAll(filteredList);
		}
	}
//	//Hàm loại bỏ dấu của tiếng Việt
//	public String removeDiacritics(String text) {
//		//Normalize the texxt to Unicode normalization form NFD
//		String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
//		return normalizedText.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
//	}
	//Hàm sắp xếp danh sách theo điều kiện
	public void sortByCondition(String condition) {
//		fullPatientList = fetchPatientData(null);
		ObservableList<Patient> filteredList = FXCollections.observableArrayList();
		switch(condition) {
		case "Tất cả": 
			{
				filteredList = fullPatientList;
			}; 
			break;
		case "Tên A-Z":
			{
				filteredList = FXCollections.observableArrayList(sortedPatientList);
				Patient tempPatient = null;
				int i,j;
				for (i=0; i<filteredList.size()-1; i++) {
					for(j=i+1; j<filteredList.size(); j++) {
						int index1 = filteredList.get(i).getPatientNameValue().lastIndexOf(" ");
						int index2 = filteredList.get(j).getPatientNameValue().lastIndexOf(" ");
						String name1 = AccentRemover.removeDiacritics(filteredList.get(i).getPatientNameValue()).substring(index1+1);
						String name2 = AccentRemover.removeDiacritics(filteredList.get(j).getPatientNameValue()).substring(index2+1);
						if (name2.compareTo(name1)<=0) {
							tempPatient = filteredList.get(i);
							filteredList.set(i, filteredList.get(j));
							filteredList.set(j, tempPatient);
						}
					}
				}
			};
			break;
		case "Tên Z-A": 
			{
				filteredList = FXCollections.observableArrayList(sortedPatientList);
				Patient tempPatient = null;
				int i,j;
				for (i=0; i<filteredList.size()-1; i++) {
					for (j=i+1; j<filteredList.size(); j++) {
						int index1 = filteredList.get(i).getPatientNameValue().lastIndexOf(" ");
						int index2 = filteredList.get(j).getPatientNameValue().lastIndexOf(" ");
						String name1 = AccentRemover.removeDiacritics(filteredList.get(i).getPatientNameValue()).substring(index1+1);
						String name2 = AccentRemover.removeDiacritics(filteredList.get(j).getPatientNameValue()).substring(index2+1);
						if (name2.compareTo(name1)>=0) {
							tempPatient = filteredList.get(i);
							filteredList.set(i, filteredList.get(j));
							filteredList.set(j, tempPatient);
						}
					}
				}
			}
			break;
		case "Tuổi tăng": 
			{
				filteredList = FXCollections.observableArrayList(sortedPatientList);
				Patient tempPatient = null;
				int i,j;
				for (i=0; i<filteredList.size()-1; i++) {
					for(j=i+1; j<filteredList.size(); j++) {
						int age1 = filteredList.get(i).getPatientAgeValue();
						int age2 = filteredList.get(j).getPatientAgeValue();
						if (age2 <= age1) {
							tempPatient = filteredList.get(i);
							filteredList.set(i, filteredList.get(j));
							filteredList.set(j, tempPatient);
						}
					}
				}
			}
			break;
		case "Tuổi giảm":
			{
				filteredList = FXCollections.observableArrayList(sortedPatientList);
				Patient tempPatient = null;
				int i,j;
				for (i=0; i<filteredList.size()-1; i++) {
					for(j=i+1; j<filteredList.size(); j++) {
						int age1 = filteredList.get(i).getPatientAgeValue();
						int age2 = filteredList.get(j).getPatientAgeValue();
						if (age2 >= age1) {
							tempPatient = filteredList.get(i);
							filteredList.set(i, filteredList.get(j));
							filteredList.set(j, tempPatient);
						}
					}
				}
			}
			break;
		}
		//Cập nhật lại danh sách hiện tại là danh sách được chọn theo kiểu sắp xếp
		sortedPatientList = filteredList;
		medicalHistoryTable.getItems().clear();
		medicalHistoryTable.getItems().addAll(filteredList);
	}
	//Hàm nạp dữ liệu về lịch sử khám bệnh của tất cả bệnh nhân từ cơ sở dữ liệu
	public List<History> getHistory(String patientId) {
		String sql = "SELECT * FROM history WHERE patient_id = ?";
		List<History> historyList = new ArrayList<History>();
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, patientId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				History history = new History(rs.getInt(1), rs.getString(2), rs.getTimestamp(3), rs.getInt(4));
				historyList.add(history);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return historyList;
	}
	//Hàm chỉnh sửa thông tin cá nhân
	public void editPersonalInfo() {
		personalNameField.setEditable(true);
		personalEmailField.setEditable(true);
		personalPhoneField.setEditable(true);
		personalAddressField.setEditable(true);
		setFocusTextField(personalNameField, personalEmailField, personalPhoneField);
		setFocusTextArea(personalAddressField);
		saveInfoButton.setVisible(true);
	}
	//Hàm lưu thông tin lên cơ sở dữ liệu khi người dùng nhập xong
	public void savePersonalInfo() {
		if (isEmptyField(personalAddressField,personalNameField, personalEmailField, personalPhoneField)) {
			Message.showMessage("Vui lòng nhập đầy đủ các thông tin", AlertType.ERROR);
		} else if (!isCorrectEmailSyntax(personalEmailField.getText())) {
			Message.showMessage("Vui lòng nhập đúng định dạng email", AlertType.ERROR);
		} else if(!isCorrectPhoneSyntax(personalPhoneField.getText())) {
			Message.showMessage("Vui lòng nhập đúng dịnh dạng điện thoại", AlertType.ERROR);
		} else {
			removeFocusTextField(personalNameField, personalEmailField, personalPhoneField);
			removeFocusTextArea(personalAddressField);
			String sql = "UPDATE receptionists SET receptionist_name = ?, receptionist_email = ?, address = ?, "
					+ "phone_number = ? WHERE receptionist_id = ?";
			try(Connection con = Database.connectDB()) {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, personalNameField.getText());
				ps.setString(2, personalEmailField.getText());
				ps.setString(3, personalAddressField.getText());
				ps.setString(4, personalPhoneField.getText());
				ps.setString(5, receptionist.getReceptionistId());
				int update = ps.executeUpdate();
				if (update==1) {
					System.out.println("Updated personal information successfully!!");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			personalNameField.setEditable(false);
			personalEmailField.setEditable(false);
			personalPhoneField.setEditable(false);
			saveInfoButton.setVisible(false);
			Message.showMessage("Cập nhật thông tin thành công", AlertType.INFORMATION);
		}
		
	}
	//Hàm thêm hiệu ứng focus khi bấm vào nút chỉnh sửa
	//Ký hiệu ... để cho biết là có thể truyền nhiều tham số cùng một lúc cho một kiểu
	public void setFocusTextField(TextField... textFields) {
		for(TextField textField : textFields) {
			if (!textField.getStyleClass().contains("text-field-effect")) {
				textField.getStyleClass().add("text-field-effect");
			}
		}
	}
	//Hàm thêm hiệu ứng focus cho textArea
	public void setFocusTextArea(TextArea textArea) {
		if (!textArea.getStyleClass().contains("text-field-effect")) {
	  		textArea.getStyleClass().add("text-field-effect");
		}
  	}
	
	//Hàm xóa hiệu ứng focus khi bấm nút lưu
	public void removeFocusTextField(TextField... textFields) {
		for (TextField textField : textFields) {
			textField.getStyleClass().remove("text-field-effect");
		}
	}

	//Hàm xóa hiệu ứng focus cho textArea
  	public void removeFocusTextArea(TextArea textArea) {
  		textArea.getStyleClass().remove("text-field-effect");
	  	textArea.setFocusTraversable(false);
  	}
  	//Hàm xóa quyền chỉnh sửa cho textField và textArea
  	public void removeEditTextFieldAndTexArea(TextArea textArea, TextField...textFields) {
  		textArea.setEditable(false);
  		for(TextField textField : textFields) {
  			textField.setEditable(false);
  		}
  	}
	
	//Hàm kiểm tra cú pháp viết email có đúng với quy ước không, trả về true nuế đúng, ngược lại false
	public boolean isCorrectEmailSyntax(String email) {
		Pattern emailPattern = Pattern.compile("[a-z]{1}[a-z\\d\\.]+@[a-z]+[.]{1}com");
		Matcher matcher = emailPattern.matcher(email);
		return matcher.matches();
	}
	//Hàm kiểm tra các trường có rỗng không
	public boolean isEmptyField(TextArea textArea, TextField...fields) {
		if (textArea.getText().isEmpty()) {
  			return true;
  		}
		for(TextField field : fields) {
			if (field.getText().isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isCorrectPhoneSyntax(String phone) {
		Pattern phonePattern = Pattern.compile("\\d{10}");
		Matcher matcher = phonePattern.matcher(phone);
		return matcher.matches();
	}
	public void updateAvatar() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image File", "*.png", "*.jpg"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile!=null) {
			Image image = new Image(selectedFile.toURI().toString());
			avartar.setImage(image);
			userAvatar.setImage(image);
			ImageUtils.updateImage(selectedFile.getAbsolutePath(), receptionist.getImageId());
			Message.showMessage("Cập nhật ảnh đại diện thành công", AlertType.INFORMATION);
		}
	}
}
