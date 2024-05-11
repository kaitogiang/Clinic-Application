package controllers;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import entity.AccentRemover;
import entity.ActiveStateUtils;
import entity.AnimationUtils;
import entity.DeepCopyUtil;
import entity.Doctor;
import entity.ImageUtils;
import entity.LogoutHandler;
import entity.Medicine;
import entity.Message;
import entity.Patient;
import entity.PdfGeneratorUtil;
import entity.PrescriptionDetail;
import entity.RemoveButton;
import entity.SelectionButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.BackgroundService;
import services.Database;

public class DoctorController implements Initializable{

	@FXML
    private Button addPatientButton;

//    @FXML
//    private TableColumn<?, ?> addressColumn;
//
//    @FXML
//    private TableColumn<?, ?> addressHistoryColumn;
//
//    @FXML
//    private TableColumn<?, ?> ageColumn;
//
//    @FXML
//    private TableColumn<?, ?> ageHistoryColumn;

    @FXML
    private Label asideBarTitle;

    @FXML
    private ImageView avartar;

    @FXML
    private Label date;

    @FXML
    private Button editAvatartButton;

    @FXML
    private Button editInfoButton;

    @FXML
    private Label fullNameInAside;

    @FXML
    private Label fullNameInNav;

    @FXML
    private Button logoutButton;

//    @FXML
//    private Button patientHistoryTab;


    @FXML
    private Button patientListTab;

    @FXML
    private Button patientMedicineTab;

    @FXML
    private Button patientDiagnosticTab;

    //Bảng danh sách bệnh nhân trong chức năng Xem danh sách khám bệnh
    @FXML
    private TableView<Patient> patientTable;

    @FXML
    private TableColumn<Patient, Integer> orderNumberColumn;
    
    @FXML
    private TableColumn<Patient, String> patientNameColumn;

    @FXML
    private TableColumn<Patient, Integer> patientAgeColumn;
    
    @FXML
    private TableColumn<Patient, Float> patientWeightColumn;
    
    @FXML
    private TableColumn<Patient, String> patientPhoneColumn;
    
    @FXML
    private TableColumn<Patient, String> patientAddressColumn;

    @FXML
    private TableColumn<Patient, String> patientStatusColumn;

    @FXML
    private HBox statusNumberContainer; //container chứa đựng thông tin số lượng trạng thái chờ khám/đã khám
    
    @FXML
    private ChoiceBox<String> filterByBox; //Bộ lọc
    
    @FXML
    private HBox filterByContainer; //Container chứa bộ lọc

    @FXML
    private Label waitingQuantity; //Hiển thị số lượng đang chờ khám
    
    @FXML
    private Label finishedQuantity; //Hiển thị số lượng đã khám xong
    
    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;
    
    //Chức năng khám và chuẩn đoán bệnh
    @FXML
    private AnchorPane diagnosticContainer;
    
    @FXML
    private TableColumn<Patient, Void> patientSelectionAction;

    @FXML
    private TableColumn<Patient, String> patientSelectionName;

    @FXML
    private TableColumn<Patient, Integer> patientSelectionOrderNumber;

    @FXML
    private TableColumn<Patient, String> patientSelectionStatus;

    @FXML
    private TableView<Patient> patientSelectionTable;

    @FXML
    private HBox patientNameContainer;
    
    @FXML
    private Label patientNameLabel;
    
    @FXML
    private TextArea testResultField;
    
    @FXML
    private TextArea diagnosisField;
    
    @FXML
    private TextArea noteField;
    
    @FXML
    private Tab healthStatusTab;
    
    @FXML
    private AnchorPane healthContainer;

    //Chức năng tạo đơn thuốc

    @FXML
    private TableView<PrescriptionDetail> prescriptionTable;
    
    @FXML
    private TableColumn<PrescriptionDetail, String> medicineNameColumn;
    
    @FXML
    private TableColumn<PrescriptionDetail, Integer> medicineOrderColumn;
    
    @FXML
    private TableColumn<PrescriptionDetail, Integer> medicineQuantityColumn;

    @FXML
    private TableColumn<PrescriptionDetail, String> usageColumn;
    
    @FXML
    private TableColumn<PrescriptionDetail, Void> actionColumn;
    
    
    //Tab tìm kiếm thuốc
    @FXML
    private TextArea medicineNoteField;
    
    @FXML
    private TextField medicineQuantityField;

    @FXML
    private TextField medicineSearchField;

    @FXML
    private TextField medicineUsageField;

    @FXML
    private AnchorPane prescriptionContainer;
    
    
    //Thông tin cá nhân của bác sĩ
    
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
    private TextField personalExperienceField;
    
    @FXML
    private TextArea personalAddressField;
    
//    @FXML
//    private TableColumn<?, ?> phoneNumberColumn;
    
    @FXML
    private Label role;

    @FXML
    private AnchorPane rootContainer;

    @FXML
    private Button saveInfoButton;

    @FXML
    private HBox searchBarContainer;

    @FXML
    private Label time;

    @FXML
    private ImageView userAvatar;

//    @FXML
//    private TableColumn<?, ?> weightColumn;
//
//    @FXML
//    private TableColumn<?, ?> weightHistoryColumn;

    //user-defined variable
    private Stage currentStage;
    
    private LocalDate currentDate;
    
    private Doctor doctor;
    
    private Scene currentScene;
    
    private String[] filterContent = {"   Tất cả", " Chờ khám", "Đang khám", "  Đã khám"};
    
    private ObservableList<Patient> patientList;

    private ObservableList<Patient> initialPatientList;
    
	ObservableList<Patient> tempPatientList;

    private int numberOfPatient;
    
    private ScheduledExecutorService executorService;
    
    private int filterValue; //0 tất cả, 1 chờ khám, 2 đang khám, 3 đã khám. Giá trị được chọn
    
    private boolean isSearchButtonClicked = false;
    
    private boolean isEnterFromSearchTextField = false;
    
    List<String> nameLabelList; //Mảng lưu trữ lịch sử gán nhãn
    
    List<Patient> choosingPatientList; //Mảng chứa danh sách các bệnh nhân được chọn
    
    private boolean isUpdatedPatientList;

    private ObservableList<Medicine> medicineList;
    
    private ObservableList<PrescriptionDetail> prescription;
    
    BackgroundService<Void> updateSelectionTableService;
    
    BackgroundService<Void> setPatientListService;
    
    private Medicine choosedMedicine;
    
    private String uniquePrescriptionId;
        
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		showTime();
		showDate();
		//Khởi tạo list đầu tiên
  		initialPatientList = fetchPatientData();
//		tempPatientList = FXCollections.observableArrayList(initialPatientList); 
  		//Nguy hiểm
  		//Tạo bản sao sâu của initialPatientList
  		tempPatientList = DeepCopyUtil.createDeepObservableListCopy(initialPatientList);
  		//Khởi tạo danh sách đơn thuốc
  		prescription = FXCollections.observableArrayList();
		Platform.runLater(()->{
			doctor = (Doctor)this.currentScene.getUserData();
			fullNameInNav.setText(doctor.getDoctorName());
			fullNameInAside.setText(doctor.getDoctorName());
			role.setText(doctor.getRole());
			Image userImage = ImageUtils.retrieveImage(doctor.getImageId());
			userAvatar.setImage(userImage);
			//Bo góc cho avatar
			Rectangle clip = new Rectangle(65, 66);
			clip.setArcWidth(20);
			clip.setArcHeight(20);
			userAvatar.setClip(clip);
			
			//Nạp danh sách thuốc
	  		medicineList = fetchMedicineData();
	  		showMedicineNameHint();
			
		});
		//Mặc định chọn tab đầu tiên để hiển thị, giá trị null để chọn tab đầu tiên
		switchTab(null);
		filterByBox.getItems().addAll(filterContent);
		filterByBox.getSelectionModel().select(0);
		filterByBox.valueProperty().addListener((observe, oldValue, newValue)->{
			switch(newValue.trim()) {
			case "Tất cả": filterValue = 0; break;
			case "Chờ khám": filterValue = 1; break;
			case "Đang khám": filterValue = 2; break;
			case "Đã khám": filterValue = 3; break;
			}
			setPatientList();
		});
		//Quan sát trạng thái của searchButton theo thời gian thực
		//Nếu button được nhấn thì biến isSearchButtonClicked sẽ true
		searchButton.armedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				isSearchButtonClicked = true;
			}
		});
		//Hàm nhận biết searchTextField có nhấn enter không
		
		//Hàm theo dõi trạng thái của searchTextField có chữ không
		searchTextField.textProperty().addListener((observe, oldValue, newValue)->{
			if (newValue.isEmpty()) {
				patientList = filterByCondition(filterValue, initialPatientList);
				isEnterFromSearchTextField = false;
				isSearchButtonClicked = false;
				patientTable.setItems(patientList); //Nếu nội dung đã xóa thì khôi phục lại list ban đầu
				System.out.println("Call text property of searchTextField");
			}
		});
		//Đặt nội dung mặc định khi table không có nội dung
		Label content = new Label("Chưa có bệnh nhân nào");
		content.setFont(new Font(20));
		patientTable.setPlaceholder(content);
		Label selectionContent = new Label("Chưa có bệnh nhân nào");
		selectionContent.setFont(new Font(17));
		patientSelectionTable.setPlaceholder(selectionContent);
//		patientList = fetchPatientData();
//		executorService = Executors.newScheduledThreadPool(1);
//		//Lên lịch một công việc, cập nhật trong 10 giây
//		executorService.scheduleAtFixedRate(this::setPatientList, 0, 5, TimeUnit.SECONDS);

		
		//Luồng chạy ngầm cho hàm setPatientList
		setPatientListService = new BackgroundService<Void>(() -> setPatientList(), 5);
		//Bắt đầu chạy nè hàm setPatientList
		setPatientListService.start();
//		Thực hiện việc cập nhật danh sách chọn khám chạy nền trong 5 giây
		//Luồng chạy ngầm cho hàm displaySelectionTable
		updateSelectionTableService = new BackgroundService<>(() -> displaySelectionTable(), 5);
		//Bắt đầu chạy nền hàm displayPatientTable()
		updateSelectionTableService.start();
		
		//Quan sát các giá trị nhãn để lưu trữ lịch sử các giá trị đã gán
		//Khi người dùng nhấn hủy chọn thì hệ thống sẽ biết là lần chọn gần nhất là cái nào
		//Và tiến hành gán nhãn cho bệnh nhân trong lần chọn đó
		//Mảng này dùng để lưu trữ lịch sử gán cho nhãn, khi nút chọn được nhấn thì giá trị tên
		//Của bệnh nhân tương ứng được lưu trữ vào đây
		nameLabelList = new ArrayList<>();
		choosingPatientList = new ArrayList<>();
		
		nameLabelList.add("Chưa chọn");
		initialPatientList.forEach(e->{
			if (e.getStatusValue().equals("Đang khám")) {
				nameLabelList.add(e.getPatientNameValue());
			}
		});
		
		tempPatientList.forEach(e->{
			if (e.getStatusValue().equals("Đang khám")) {
				choosingPatientList.add(e);
			}
		});
		//Gán bệnh nhân được chọn gần nhất
		patientNameLabel.setText(nameLabelList.getLast());
		
		//Hàm hiển thị lỗi khi chưa chọn bệnh nhân mà bắt đầu nhập
		testResultField.setOnMouseClicked(e->showTextAreaErrorIfEmptyChoosingList());
		diagnosisField.setOnMouseClicked(e->showTextAreaErrorIfEmptyChoosingList());
		noteField.setOnMouseClicked(e->showTextAreaErrorIfEmptyChoosingList());
		
		//các hàm quan sát sự tập trung của các text area, nếu mất tập trung thì sẽ bắt đầu
		//lưu dữ liệu đã nhập vào cơ sở dữ liệu
		testResultField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && !choosingPatientList.isEmpty()) {
				String text = testResultField.getText();
				if (text != null && !text.isEmpty()) {
					autoSave(choosingPatientList.getLast().getPatientIdValue(), 1);
				}
				//Gọi hàm auto save lên cơ sở dữ liệu
			}
		});
		diagnosisField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && !choosingPatientList.isEmpty()) {
				String text = diagnosisField.getText();
				if (text != null && !text.isEmpty()) {
					autoSave(choosingPatientList.getLast().getPatientIdValue(), 2);
				}
			}
		});
		noteField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && !choosingPatientList.isEmpty()) {
				String text = noteField.getText();
				if (text != null && !text.isEmpty()) {
					autoSave(choosingPatientList.getLast().getPatientIdValue(), 3);
				}
			}
		});
		
		Label medicineLabel = new Label("Chưa có thuốc nào");
		medicineLabel.setFont(new Font(17));
		prescriptionTable.setPlaceholder(medicineLabel);
		
		//Thay đổiký tự xuống dòng mặc định cho textarea trong chức năng
		//Tạo đơn thuốc
		medicineNoteField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
				medicineNoteField.appendText("\n");
				event.consume();
			}
		});
		//Thay đổi hành vi mặc định của phím enter trong text area
		//Phím Enter lúc này dùng để thêm đơn thuốc
		medicineNoteField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				addMedicine();
				event.consume();
			}
		});
		
		//Thay đổi thứ tự của các phần tử trong prescription nếu một phần tử nào đó
		//Bị xóa khỏi danh sách
		prescription.addListener(new ListChangeListener<PrescriptionDetail>() {
			@Override
			public void onChanged(Change<? extends PrescriptionDetail> c) {
				while(c.next()) {
					if (c.wasRemoved()) {
						//Thay đổi số thứ tự của các phần tử còn lại
						int i = 1;
						for(PrescriptionDetail e: prescription) {
							e.setOrderNumber(i);
							i++;
						}
					}
				}
			}
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
    //Gán Scene hiện tại
    public void setMyScene(Scene scene) {
		this.currentScene = scene;
	}
    //Đặt stage hiện tại
    public void setCurrentStage(Stage stage) {
		this.currentStage = stage;
	}
    public void stopExecutorService() {
    	executorService.shutdown();
    }
    public void stopBackgroundService() {
    	updateSelectionTableService.cancel();
    	setPatientListService.cancel();
    	
    }
    //Hàm đăng xuất khỏi hệ thống
    public void logout(ActionEvent e) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginScreen.fxml"));
		LogoutHandler.logout(currentStage, loader);
//		executorService.shutdown();
		stopBackgroundService();
	}
    //Chuyển đổi giữa các tab
    public void switchTab(ActionEvent e) {
		if (e==null || e.getSource().equals(patientListTab)) {
			//Hiển thị tab chính
			patientTable.setVisible(true);
			statusNumberContainer.setVisible(true);
	  		searchBarContainer.setVisible(true);
	  		filterByContainer.setVisible(true);
  			asideBarTitle.setText("Danh sách bệnh nhân ("+ numberOfPatient +")");
  			statusNumberContainer.setTranslateX(0);
	  		displayPatientTable();
			//Ẩn các tab còn lại
			personalContainer.setVisible(false);
			diagnosticContainer.setVisible(false);
			patientNameContainer.setVisible(false);
			prescriptionContainer.setVisible(false);
			//Tạo animation chuyển động cho các nút
			AnimationUtils.createFadeTransition(patientTable, 0.0, 10.0);
			AnimationUtils.createFadeTransition(statusNumberContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(filterByContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(searchBarContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
			//Tạo trạng thái active cho tab
			ActiveStateUtils.addStyleClass(patientListTab);
			ActiveStateUtils.removeStyleClass(patientDiagnosticTab);
			ActiveStateUtils.removeStyleClass(patientMedicineTab);
//			ActiveStateUtils.removeStyleClass(patientHistoryTab);
			
		} else if(e.getSource().equals(patientDiagnosticTab) && e!=null) {
			//Khởi tạo các chức năng
			displaySelectionTable();
			asideBarTitle.setText("Khám và chuẩn đoán");
			displayDiagnosisContentForCurrentPatient();
			statusNumberContainer.setTranslateX(-34);
			//Hiển thị tab chính
			diagnosticContainer.setVisible(true);
			patientNameContainer.setVisible(true);
			//Ẩn các tab còn lại
			personalContainer.setVisible(false);
			patientTable.setVisible(false);
			searchBarContainer.setVisible(false);
			filterByContainer.setVisible(false);
			personalContainer.setVisible(false);
			prescriptionContainer.setVisible(false);
			//Tạo animation chuyển động cho các nút
			AnimationUtils.createFadeTransition(diagnosticContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(patientNameContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
			AnimationUtils.createFadeTransition(statusNumberContainer, 0.0, 10.0);
			//Tạo trạng thái active cho tab
			ActiveStateUtils.removeStyleClass(patientListTab);
			ActiveStateUtils.addStyleClass(patientDiagnosticTab);
			ActiveStateUtils.removeStyleClass(patientMedicineTab);
//			ActiveStateUtils.removeStyleClass(patientHistoryTab);
		} else if (e.getSource().equals(patientMedicineTab) && e!=null) {
			//Thiết lập các xử lý liên quan
			asideBarTitle.setText("Tạo đơn thuốc");
			statusNumberContainer.setTranslateX(-93);
			showPrescription();
			//Hiển thị tab chính
			prescriptionContainer.setVisible(true);
			statusNumberContainer.setVisible(true);
			patientNameContainer.setVisible(true);
			//Ẩn các tab còn lại
			searchBarContainer.setVisible(false);
			filterByContainer.setVisible(false);
			personalContainer.setVisible(false);
			patientTable.setVisible(false);
			personalContainer.setVisible(false);
			diagnosticContainer.setVisible(false);
			//Tạo animation chuyển động cho các nút
			AnimationUtils.createFadeTransition(prescriptionContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(statusNumberContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(patientNameContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
			//Tạo trạng thái active cho tab
			ActiveStateUtils.removeStyleClass(patientListTab);
			ActiveStateUtils.removeStyleClass(patientDiagnosticTab);
			ActiveStateUtils.addStyleClass(patientMedicineTab);
//			ActiveStateUtils.removeStyleClass(patientHistoryTab);
			if (choosingPatientList.isEmpty()) {
				Message.showMessage("Vui lòng chọn bệnh nhân trước khi tạo đơn thuốc", AlertType.ERROR);
				
			}
		} 
		
	}
  //Chuyển đến tab trang cá nhân của người dùng
  	public void goToPersonalInfo() {
  		//Ẩn hiện các mục cần thiết
  		asideBarTitle.setText("Thông tin cá nhân");
  		personalContainer.setVisible(true);
  		saveInfoButton.setVisible(false);
  		patientTable.setVisible(false);
  		statusNumberContainer.setVisible(false);
  		searchBarContainer.setVisible(false);
  		filterByContainer.setVisible(false);
  		prescriptionContainer.setVisible(false);
		patientNameContainer.setVisible(false);
  		//Loại bỏ chọn tab
  		ActiveStateUtils.removeStyleClass(patientListTab);
		ActiveStateUtils.removeStyleClass(patientDiagnosticTab);
		ActiveStateUtils.removeStyleClass(patientMedicineTab);
//		ActiveStateUtils.removeStyleClass(patientHistoryTab);
  		
  		AnimationUtils.createFadeTransition(personalContainer, 0.0, 10.0);
  		AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
  		//Khởi tạo dữ liệu cho các trường trong thông tin cá nhân
  		personalNameField.setText(doctor.getDoctorName());
  		personalEmailField.setText(doctor.getDoctorEmail());
  		personalPhoneField.setText(doctor.getPhoneNumber());
  		personalPositionField.setText(doctor.getRole());
  		personalAddressField.setText(doctor.getAddress());
  		personalExperienceField.setText(String.valueOf(doctor.getExperienceYear()));
  		Image userImg = ImageUtils.retrieveImage(doctor.getImageId());
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
  			//Xử lý trên cơ sở dữ liệu
  	  		String sql = "UPDATE doctors SET doctor_name = ?, doctor_email = ?, address = ?, "
				+ "phone_number = ? WHERE doctor_id = ?";
	  	  	try(Connection con = Database.connectDB()) {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, personalNameField.getText());
				ps.setString(2, personalEmailField.getText());
				ps.setString(3, personalAddressField.getText());
				ps.setString(4, personalPhoneField.getText());
				ps.setString(5, doctor.getDoctorId());
				int update = ps.executeUpdate();
				if (update==1) {
					System.out.println("Updated personal information successfully!!");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
  			//Ẩn chế độ chỉnh sửa cho các trường thông tin
  			removeEditTextFieldAndTexArea(personalAddressField, personalNameField, personalEmailField, personalPhoneField);
  			saveInfoButton.setVisible(false);
  			Message.showMessage("Cập nhật thông tin thành công", AlertType.INFORMATION);
  		}
  		
  	}
  	
  	public void updateAvatar() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image File", "*.png", "*.jpg"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile!=null) {
			Image image = new Image(selectedFile.toURI().toString());
			avartar.setImage(image);
			userAvatar.setImage(image);
			ImageUtils.updateImage(selectedFile.getAbsolutePath(), doctor.getImageId());
			Message.showMessage("Cập nhật ảnh đại diện thành công", AlertType.INFORMATION);
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
  	
  	public void setFocusTextArea(TextArea textArea) {
  		if (!textArea.getStyleClass().contains("text-field-effect")) {
  	  		textArea.getStyleClass().add("text-field-effect");
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
  	
  	//Hàm nạp dữ liệu từ CSDL
  	public ObservableList<Patient> fetchPatientData() {
  		System.out.println("Fetch data from database");
  		ObservableList<Patient> list = FXCollections.observableArrayList();
  		String sql = "SELECT p.*, h.* FROM `patients` p JOIN history h ON p.patient_id = h.patient_id WHERE DATE(h.date) = ?";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
  			ResultSet rs = ps.executeQuery();
  			while(rs.next()) {
  				String id = rs.getString("patient_id");
  				int order = rs.getInt("order_number");
  				String name = rs.getString("patient_name");
  				int age = rs.getInt("age");
  				float weight = rs.getFloat("weight");
  				String phone = rs.getString("phone_number");
  				String address = rs.getString("address");
  				int statusValue = rs.getInt("status");
  				String status = (statusValue == 0) ? "Chờ khám" :
						(statusValue == 1) ? "Đang khám" : "Đã khám";
  				list.add(new Patient(id, order, name, age, weight, phone, address, status));
  			}
  			return list;
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		return null;
  	}
  	//Hàm này dùng để thực hiện việc nạp dữ liệu mỗi 5 giây từ cơ sở dữ liệu và cập nhật bảng
  	public void setPatientList() {
  		System.out.println("===================Goi lai ham setPatientList sau 5 giay=====================");
  		System.out.println(isSearchButtonClicked);
  		//Khởi tạo list ban đầu để thực hiện gán số lượng bệnh nhân
  		//Test thử lệnh này
//  		ObservableList<Patient> tempPatientList = fetchPatientData();
  		ObservableList<Patient> tempPatientList = DeepCopyUtil.createDeepObservableListCopy(fetchPatientData());
  		if (isTwoDifferenceList(initialPatientList, tempPatientList)) {
  			isUpdatedPatientList = true; //true có nghĩa là list đã thay đổi mới
  		} else {
  			isUpdatedPatientList = false; //false có nghĩa là list không thay đổi
  		}
  		if (tempPatientList.size() > initialPatientList.size() || isTwoDifferenceList(initialPatientList, tempPatientList)) {
  			initialPatientList = fetchPatientData();
  			this.tempPatientList = DeepCopyUtil.createDeepObservableListCopy(initialPatientList); //List này dùng cho bảng chọn bệnh nhân
  			System.out.println("initialPatientList have been updated because the data in databse have been changed");
  		} else {
  			System.out.println("initialPatientList is still not changed");
  		}
  		nameLabelList.forEach(e->{
  			System.out.println(e);
  		});
  		//Lấy giá trị của bộ lọc để lựa chọn cách lọc phù hợp với người dùng yêu cầu
  		//Cập nhật danh sách bệnh nhân thời gian thực
  		if (!searchTextField.getText().isEmpty() && (isEnterFromSearchTextField || isSearchButtonClicked)) {
  			patientList = findPatientByText();
  			ObservableList<Patient> tempList = FXCollections.observableArrayList(patientList);
  			tempList = filterByCondition(filterValue, patientList);
  			patientTable.setItems(tempList);
  		} else {
  	  		patientList = filterByCondition(filterValue, initialPatientList);
  	  		patientTable.setItems(patientList);
  		}
  		//Gắn kết danh sách vào tableview
//  		patientTable.setItems(patientList);
  		//Cập nhật danh sách số lượng bệnh nhân hôm nay
  		numberOfPatient = initialPatientList.size();
  		//Cập nhật số lượng trạng thái chờ khám, đã khám
  		int numberOfWaiting = initialPatientList.filtered(t -> t.getStatusValue().equals("Chờ khám")).size();
  		int numberOfDone = initialPatientList.filtered(t -> t.getStatusValue().equals("Đã khám")).size();
  		//Thực hiện cập nhật các thành phần UI trong luồng ứng dụng JavaFX
  		//Bởi vì nó chỉ được cập nhật trong đây
  		Platform.runLater(() -> {
  			HBox hbox = (HBox) patientListTab.getGraphic();
  			if (hbox.getStyleClass().contains("active-selection-background")) {
  				asideBarTitle.setText("Danh sách bệnh nhân ("+ numberOfPatient +")");
  			}
  			waitingQuantity.setText(String.valueOf(numberOfWaiting));
  	  		finishedQuantity.setText(String.valueOf(numberOfDone));
  		});
  		
  	}
  	//Hàm lọc danh sách dựa trên điều kiện, 0 tất cả, 1 chờ khám, 2 đang khám, 3 đã khám
  	public ObservableList<Patient> filterByCondition(int condition, ObservableList<Patient> currentList) {
  		ObservableList<Patient> initialList = FXCollections.observableArrayList(currentList);
  		switch(condition) {
  		case 0: return initialList;
  		case 1: return initialList.filtered(e->e.getStatusValue().equals("Chờ khám"));
  		case 2: return initialList.filtered(e->e.getStatusValue().equals("Đang khám"));
  		case 3: return initialList.filtered(e->e.getStatusValue().equals("Đã khám"));
  		}
  		return null;
  	}
  	//Hàm kiểm tra hai list có khác nhau không
  	//Các list này là danh sách khám bệnh theo thứ tự, nếu list sau có sự thay đổi trạng thái
  	//Của một phần tử bất kỳ thì sẽ trở thành hai list khác nhau
  	public boolean isTwoDifferenceList(ObservableList<Patient> oldList, ObservableList<Patient> newList) {
  		boolean check = false;
  		int i;
  		for (i=0; i<oldList.size(); i++) {
//  			System.out.println("Trong ham isTwoDifferenceList "+ oldList.get(i).getPatientNameValue() +" - " + oldList.get(i).getStatusValue() + " " + newList.get(i).getPatientNameValue()+" - "+newList.get(i).getStatusValue());
  			if (!oldList.get(i).isEqual(newList.get(i))) {
  				check = true;
  			}
  		}
  		return check;
  	}
  	//Hàm hiển thị danh sách bệnh nhân
  	public void displayPatientTable() {
  		orderNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
  		patientNameColumn.setCellValueFactory(cellData -> cellData.getValue().getPatientName());
  		patientAgeColumn.setCellValueFactory(cellData -> cellData.getValue().getAge().asObject());
  		patientWeightColumn.setCellValueFactory(cellData -> cellData.getValue().getWeight().asObject());
  		patientPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().getPhoneNumber());
  		patientAddressColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress());
  		patientStatusColumn.setCellValueFactory(cellData -> cellData.getValue().getStatus());
		patientTable.setItems(patientList); //setItem dùng để liên kết observablelist và tableview, nếu list thay đổi thì table sẽ thay đổi theo

  	}
  	//Hàm tìm kiếm bệnh nhân
  	public ObservableList<Patient> findPatientByText() {
		String searchText = searchTextField.getText().trim().toLowerCase();
		if (searchText.isEmpty()) {
			System.out.println("Empty Search Text");
		} else {
			ObservableList<Patient> filteredList = 
					initialPatientList.filtered(patient->(patient.toString().toLowerCase().
							contains(searchText)) || AccentRemover.removeDiacritics(patient.toString().toLowerCase()).
							contains(searchText));
			return filteredList;
		}
		return null;
  	}
  	
  	public void findPatient() {
  		String searchText = searchTextField.getText().trim().toLowerCase();
  		isEnterFromSearchTextField = true;
		if (searchText.isEmpty()) {
			System.out.println("Empty Search Text");
		} else {
			ObservableList<Patient> filteredList = 
					patientList.filtered(patient->(patient.toString().toLowerCase().
							contains(searchText)) || AccentRemover.removeDiacritics(patient.toString().toLowerCase()).
							contains(searchText));
			patientTable.setItems(filteredList);
			System.out.println("Call find patient");
		}
  	}
  	//Hàm hiển thị danh sách chọn bệnh nhân
  	public void displaySelectionTable() {
  		System.out.println("-----------Goi ham displaySelectionTable sau 5 giay--------------");
  		patientSelectionOrderNumber.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
  		patientSelectionName.setCellValueFactory(cellData -> cellData.getValue().getPatientName());
  		patientSelectionStatus.setCellValueFactory(cellData -> cellData.getValue().getStatus());
  		patientSelectionAction.setCellFactory(param -> new SelectionButton(nameLabelList, choosingPatientList, isUpdatedPatientList, patientNameLabel, this));
  		Platform.runLater(() -> {
  	  		//Hàm này được chạy trong một luồng khác nhưng nếu có sự thay đổi về giao diện
  			//Thì chỉ được phép thay đổi trong luồng của javafx nên phải đặt mọi lệnh thay
  			//đổi giao diện vào trong Platform.runLater
//  			tempPatientList = fetchPatientData();
  			patientSelectionTable.setItems(tempPatientList);
  	  		//Tiến hành gán nhãn cho bệnh nhân được chọn gần nhất
//  	  		patientNameLabel.setText(nameLabelList.getLast());
//  			System.out.println("Danh sach cua choosingPatientList");
//  			
  		});
//  		System.out.println("Call the displaySelectionTable");
//  		System.out.println("So luong bien la: "+nameLabelList.size());
//  		System.out.println("So luong bien trong nameLabelList la: "+nameLabelList.getLast());
  			choosingPatientList.forEach(e->{
  				System.out.println(e + " - hashCode = " + e.hashCode());
  				
  			});
  			
  	}
  	
  	//Hàm tự động lưu của các text area, 1 là trường test result, 2 là diagnosis, 3 là note
  	public void autoSave(String patientId, int fieldNumber) {
  		//Lấy ngày giờ hiện tại và format nó
  		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String date = currentDateTime.format(formattedDate);
		
  		if (fieldNumber == 1) {
  			String sql = "";
  			if (!isExistInDatabase(patientId)) {
  				sql = "INSERT INTO diagnosis(patient_id, doctor_id, test_result, diagnosis_date) "
  						+ "VALUES(?,?,?,?)";
  				try(Connection con = Database.connectDB()) {
  					PreparedStatement ps = con.prepareStatement(sql);
  					ps.setString(1, patientId);
  					ps.setString(2, doctor.getDoctorId());
  					ps.setString(3, testResultField.getText());
  					ps.setTimestamp(4, Timestamp.valueOf(date));
  					int result = ps.executeUpdate();
  					if (result!=0) System.out.println("Saving test result to database....");
  					else System.out.println("Fail to save test result!");
  				} catch(Exception e) {
  					e.printStackTrace();
  				}
  			} else {
  				sql = "UPDATE diagnosis SET test_result = ? WHERE patient_id = ? AND DATE(diagnosis_date) = DATE(NOW())";
  				try(Connection con = Database.connectDB()) {
  					PreparedStatement ps = con.prepareStatement(sql);
  					ps.setString(1, testResultField.getText());
  					ps.setString(2, patientId);
  					int result = ps.executeUpdate();
  					if (result!=0) System.out.println("Saving test result to existing table");
  				} catch(Exception e) {
  					e.printStackTrace();
  				}
  			}
  		} else if (fieldNumber == 2) {
  			String sql = "";
  			if (!isExistInDatabase(patientId)) {
  				sql = "INSERT INTO diagnosis(patient_id, doctor_id, diagnosis, diagnosis_date) "
  						+ "VALUES(?,?,?,?)";
  				try(Connection con = Database.connectDB()) {
  					PreparedStatement ps = con.prepareStatement(sql);
  					ps.setString(1, patientId);
  					ps.setString(2, doctor.getDoctorId());
  					ps.setString(3, diagnosisField.getText());
  					ps.setTimestamp(4, Timestamp.valueOf(date));
  					int result = ps.executeUpdate();
  					if (result!=0) System.out.println("Saving diagnosis to database");
  				} catch(Exception e) {
  					e.printStackTrace();
  				}
  			} else {
  				sql = "UPDATE diagnosis SET diagnosis = ? WHERE patient_id = ? AND DATE(diagnosis_date) = DATE(NOW())";
  				try(Connection con = Database.connectDB()) {
  					PreparedStatement ps = con.prepareStatement(sql);
  					ps.setString(1, diagnosisField.getText());
  					ps.setString(2, patientId);
  					int result = ps.executeUpdate();
  					if (result!=0) System.out.println("Saving diagnosis to existing table");
  				} catch(Exception e) {
  					e.printStackTrace();
  				}
  			}
  		} else if (fieldNumber == 3) {
  			String sql = "";
  			if (!isExistInDatabase(patientId)) {
	  			sql = "INSERT INTO diagnosis (patient_id, doctor_id, notes, diagnosis_date) VALUES(?,?,?,?)";
  				try(Connection con = Database.connectDB()) {
  	  				PreparedStatement ps = con.prepareStatement(sql);
  	  				ps.setString(1, patientId);
  	  				ps.setString(2, doctor.getDoctorId());
  	  				ps.setString(3, noteField.getText());
  	  				ps.setTimestamp(4, Timestamp.valueOf(date));
  	  				int result = ps.executeUpdate();
  	  				if (result!=0) System.out.println("saving note to databse");
  	  			} catch(Exception e) {
  	  				e.printStackTrace();
  	  			}
  			} else {
  				sql = "UPDATE diagnosis SET notes = ? WHERE patient_id = ? AND DATE(diagnosis_date) = DATE(NOW())";
  				try(Connection con = Database.connectDB()) {
  					PreparedStatement ps = con.prepareStatement(sql);
  					ps.setString(1, noteField.getText());
  					ps.setString(2, patientId);
  					int result = ps.executeUpdate();
  					if (result!=0) System.out.println("Saving note to existing table");
  				} catch(Exception e) {
  					e.printStackTrace();
  				}
  			}
  		}
  	}
  	public boolean isExistInDatabase(String patientId) {
  		String sql = "SELECT * FROM diagnosis WHERE patient_id = ? AND DATE(diagnosis_date) = DATE(NOW())";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ps.setString(1, patientId);
  			ResultSet rs = ps.executeQuery();
  			if (rs.next()) {
  				return true;
  			}
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		
  		return false;
  	}
  	public void displayDiagnosisContentForCurrentPatient() {
  		if (choosingPatientList.isEmpty() || !isExistInDatabase(choosingPatientList.getLast().getPatientIdValue())) {
  			testResultField.setText("");
  			diagnosisField.setText("");
  			noteField.setText("");
  		} else {
  			getDiagnosisContentForCurrentPatient();
  		}
  	}
  	//Hàm nạp thông tin đã nhập của bệnh nhân cụ thể trong ngày hôm nay
  	public void getDiagnosisContentForCurrentPatient() {
  		String sql = "SELECT test_result, diagnosis, notes FROM diagnosis WHERE patient_id = ?";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ps.setString(1, choosingPatientList.getLast().getPatientIdValue());
  			ResultSet rs = ps.executeQuery();
  			while (rs.next()) {
  				testResultField.setText(rs.getString(1));
  				diagnosisField.setText(rs.getString(2));
  				noteField.setText(rs.getString(3));
  			}
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  	}
  	
  	public void enableEditTextArea(boolean emptyList) {
  		if (emptyList) {
  			testResultField.setEditable(false);
  			diagnosisField.setEditable(false);
  			noteField.setEditable(false);
  		} else {
  			testResultField.setEditable(true);
  			diagnosisField.setEditable(true);
  			noteField.setEditable(true);
  		}
  	}
  	
  	public void showTextAreaErrorIfEmptyChoosingList() {
  		if (choosingPatientList.isEmpty()) {
  			Message.showMessage("Vui lòng chọn bệnh nhân trước khi nhập", AlertType.ERROR);
  		}
  	}
  	//Hàm xử lý dành cho tab tình trạng bệnh
  	public void showElectricRecord() {
  		//Nạp dữ liệu của bệnh nhân tương ứng trong danh sách choosingPatientList
  		if (healthStatusTab.isSelected()) {
  			System.out.println("Call show ElectricRecord");
  			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ElectronicHealthRecordScreen.fxml"));
  			String css = this.getClass().getResource("/css/style.css").toExternalForm();
  			try {
  				//Nạp giao diện vào
  				healthContainer = loader.load();
  				healthContainer.getStylesheets().add(css); //Them CSS vào file FXML ngoài
  				healthStatusTab.setContent(healthContainer); //Đặt nội dung cho tab đã chọn
  				//Gửi dữ liệu vào cho ElectricRecordController
  				ElectricRecordController controller = loader.getController();
  				//Nếu có bệnh nhân thì truyền vào, nếu chưa chọn thì truyền null
  				//Để cho controller biết mà hiển thị initialContainer yêu cầu người
  				//dùng chọn bệnh nhân để xem các thông tin còn lại
  				if (!choosingPatientList.isEmpty()) {
  	  				controller.setPatientList(choosingPatientList.getLast());
  				} else {
  	  				controller.setPatientList(null);
  				}
  			} catch(Exception e) {
  				e.printStackTrace();
  			}
  		}
  	}
  	//Thiết lập phần chức năng tạo đơn thuốc
  	//Hàm hiển thị gợi ý thuốc khi bác sĩ nhập tên thuốc
  	public void showMedicineNameHint() {
  		Stage currentStage = (Stage) date.getScene().getWindow();
  		// Thiết lập gợi ý cho TextField với kiểu dữ liệu Medicine
  		AutoCompletionBinding<Medicine> autoCompletionBinding = TextFields.bindAutoCompletion(medicineSearchField, medicineList);

        autoCompletionBinding.setPrefWidth(344);;
        
        // Customizing the style of the auto-completion popodocup
        autoCompletionBinding.getAutoCompletionPopup().getStyleClass().add("custom-popup-style");

  		
        
      //  Phần xử lý khi nhấn ctrl + space thì hiển thị tất cả gợi ý
        medicineSearchField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
        	if (event.isControlDown() && event.getCode() == KeyCode.SPACE) {
        		autoCompletionBinding.getAutoCompletionPopup().show(medicineSearchField);
        		event.consume();
        		
        	}
        });
        //Sử dụng BooleanProperty để có thể quan sát giá trị của nó
        final BooleanProperty selected = new SimpleBooleanProperty(false);
        
        // Xử lý sự kiện khi người dùng chọn một gợi ý
        //Listener này luôn luôn được gọi mỗi khi người dùng chọn một gợi ý
        //Khi chọn xong một gợi ý thì lập tức biến selected sẽ đặt thành true
        autoCompletionBinding.setOnAutoCompleted(event -> {
            Medicine selectedMedicine = event.getCompletion();
            // Tại đây, bạn có thể thực hiện các thao tác khác với thuốc đã được chọn
            System.out.println("Id cua thuoc la: "+ selectedMedicine.getMedicineIdValue());
            selected.set(true);
            choosedMedicine = selectedMedicine;
        });
        
        
        //Đặt một listener cho sự kiện focus của TextField
        medicineSearchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
        	//Nếu TextField mất foucs và người dùng chưa chọn gợi ý
        	//Thì thực hiện gọi lại hàm focus
        	if (!newValue && !selected.get()) {
        		//Yêu cầu focus lại textField
        		medicineSearchField.requestFocus();
        	} else {
        		//Nếu mà người dùng mất focus và đã chọn xong thì thực hiện gán lại
        		//false cho selected
        		selected.set(false);
        	}
        });
  	}
  	
  	//Hàm nạp danh sách thuốc trong phòng khám
  	public ObservableList<Medicine> fetchMedicineData() {
  		String sql = "SELECT * FROM medicine";
  		ObservableList<Medicine> list = FXCollections.observableArrayList();
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ResultSet rs = ps.executeQuery();
  			while (rs.next()) {
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
    			
    			Medicine med = new Medicine(medicineId, 0, medicineName, description, manufacturer, dosage, dosageUnit, unitPrice, expirationDate, 0);
    			list.add(med);
  			}
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		return list;
  	}
  	
  	public void addMedicine() {
  		//Nếu chưa chọn bệnh nhân thì không thể tạo đơn thuốc
  		if (choosingPatientList.isEmpty()) {
  			Message.showMessage("Không thể tạo đơn thuốc khi chưa chọn bệnh nhân", AlertType.ERROR);
  			return;
  		}
  		
  		if (medicineSearchField.getText().isEmpty() || medicineQuantityField.getText().isEmpty()
  			|| medicineUsageField.getText().isEmpty() || medicineNoteField.getText().isEmpty()) {
  			Message.showMessage("Vui lòng nhập đầy đủ thông tin", AlertType.ERROR);
  		} else if (!isInteger(medicineQuantityField.getText())) {
  			Message.showMessage("Vui lòng nhập đúng định dạng", AlertType.ERROR);
  		} else {
  			//Lấy dữ liệu đã nhập
  			int medicineQuantity = Integer.valueOf(medicineQuantityField.getText());
  			String usage = medicineUsageField.getText();
  			String notes = medicineNoteField.getText();
  			String medicineName = choosedMedicine.getMedicineNameValue();
  			int orderNumber = prescription.size() + 1;
  			for(PrescriptionDetail e: prescription) {
  				if (medicineName.equals(e.getMedicineNameValue())) {
  					Message.showMessage("Thuốc này đã được thêm trước đó", AlertType.ERROR);
  					return;
  				}
  			}
  			//Tạo một dòng trong chi tiết đơn thuốc
  			PrescriptionDetail medicineItem = new PrescriptionDetail(orderNumber, medicineName , medicineQuantity, usage, notes, choosedMedicine);
  			prescription.add(medicineItem);
  			//Đặt các trường về ban đầu
  			medicineSearchField.setText("");
  			medicineQuantityField.setText("");
  			medicineUsageField.setText("");
  		}
  		for(PrescriptionDetail e: prescription) {
  			System.out.println(e);
  		}
  	}
  	
  	public void showPrescription() {
  		medicineOrderColumn.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
  		medicineNameColumn.setCellValueFactory(cellData -> cellData.getValue().getMedicineName());
  		medicineQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().getMedicineQuantity().asObject());
  		usageColumn.setCellValueFactory(cellData -> cellData.getValue().getUsage());
  		actionColumn.setCellFactory(param -> new RemoveButton());
  		prescriptionTable.setItems(prescription);
  	}
  	
  	public boolean isInteger(String text) {
  		Pattern p = Pattern.compile("\\d+");
  		Matcher matcher = p.matcher(text);
  		return matcher.matches();
  	}
  	//Hàm tạo đơn thuốc
  	public void createPrescription() {
  		//Nếu chưa chọn bệnh nhân thì không thể tạo đơn thuốc
  		if (choosingPatientList.isEmpty()) {
  			Message.showMessage("Không thể tạo đơn thuốc khi chưa chọn bệnh nhân", AlertType.ERROR);
  			return;
  		} else if (prescription.isEmpty()) {
  			Message.showMessage("Hãy thêm thuốc vào trước khi tạo đơn thuốc", AlertType.ERROR);
  			return;
  		} else if (testResultField.getText().isEmpty() && diagnosisField.getText().isEmpty() && noteField.getText().isEmpty()) {
  			Message.showMessage("Vui lòng ghi nhận kết quả khám trước", AlertType.ERROR);
  			return;
  		}
  		
  		System.out.println("DA TAO DON THUOC THANH CONG");
  		String prescriptionId;
  		String prescriptionNote = medicineNoteField.getText();
  		String currendPatientId = choosingPatientList.getLast().getPatientIdValue();
  		if (uniquePrescriptionId != null) {
  			while(isExistedPrescription(uniquePrescriptionId)) {
  				uniquePrescriptionId = generateUniqueId();
  			}
  			prescriptionId = uniquePrescriptionId;
  		} else {
  			do {
  	  			prescriptionId = generateUniqueId();
  	  		} while(isExistedPrescription(prescriptionId));
  		}

  		//Thêm đơn thuốc vào bảng prescription
  		String prescriptionSql = "INSERT INTO prescription VALUES(?,NOW(),?)";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(prescriptionSql);
  			ps.setString(1, prescriptionId);
  			ps.setString(2, prescriptionNote);
  			int insert = ps.executeUpdate();
  			if (insert != 0) System.out.println("THEM DON THUOC THANH CONG");
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		//Cập nhật prescriptionId vào trong bảng diagnosis
  		String diagnosisSql = "UPDATE diagnosis SET prescription_id = ? "
  				+ "WHERE patient_id = ? AND DATE(diagnosis_date) = DATE(NOW())";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(diagnosisSql);
  			ps.setString(1, prescriptionId);
  			ps.setString(2, currendPatientId);
  			int update = ps.executeUpdate();
  			if (update != 0) System.out.println("CAP NHAT DIAGNOSIS DON THUOC THANH CONG");
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		
  		//Thêm chi tiết đơn thuốc
  		String prescriptionDetailId;
  		String detailSql = "INSERT INTO prescriptiondetail VALUES(?, ?, ?, ?, ?)";
  		try(Connection con = Database.connectDB()) {
  			con.setAutoCommit(false);
  			PreparedStatement ps = con.prepareStatement(detailSql);
  			for (PrescriptionDetail e: prescription) {
  				//Tạo id mới cho mỗi prescription detail

  				do {
  		  			prescriptionDetailId = generateUniqueId();
  		  		} while(isExistedPrescriptionDetail(prescriptionDetailId));
  				Medicine medicine = e.getMedicineValue();
  				String medicineId = medicine.getMedicineIdValue();
  				int medicineAmount = e.getMedicineQuantityValue();
  				ps.setString(1, prescriptionDetailId);
  				ps.setString(2,prescriptionId);
  				ps.setString(3, medicineId);
  				ps.setInt(4, medicineAmount);
  				ps.setString(5, e.getUsageValue());
  				ps.addBatch();
  			}
  			ps.executeBatch(); //Thực hiện tất cả lệnh prepare trong một lần
  			con.commit(); //Commit giao dịch
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		//Hiển thị thông báo tạo thành công
  		Message.showMessage("Tạo đơn thuốc thành công", AlertType.INFORMATION);
  		clearPrescription();
  		setCompleteForPatient(currendPatientId);
  		if (choosingPatientList.size() == 1) {
  			patientNameLabel.setText("Chưa chọn");
  			choosingPatientList.removeLast();
  		} else if (choosingPatientList.size() > 1) {
  			choosingPatientList.removeLast();
  			patientNameLabel.setText(choosingPatientList.getLast().getPatientNameValue());
  		}
  	}
  	
  	public String generateUniqueId() {
  		 // Tạo một UUID mới
        UUID uniqueId = UUID.randomUUID();

        // Chuyển UUID thành chuỗi và lấy 5 ký tự đầu tiên
        String shortId = uniqueId.toString().substring(0, 5);

        // In mã ID ngắn
        System.out.println("Short ID: " + shortId);
        
        return shortId;
  	}
  	public boolean isExistedPrescription(String id) {
  		String sql = "SELECT * FROM prescription WHERE prescription_id = ?";
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
  	
  	public boolean isExistedPrescriptionDetail(String id) {
  		String sql = "SELECT * FROM prescriptiondetail WHERE prescription_detail_id = ?";
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
  	//Hàm xóa tất cả thuốc trong đơn thuốc
  	public void clearPrescription() {
  		prescription.clear();
  	}
  	//Thay đổi trạng thái khám bệnh của bệnh nhân sang đã khám
  	public void setCompleteForPatient(String patientId) {
  		String sql = "UPDATE history SET status = 2 WHERE patient_id = ? AND DATE(date) = DATE(NOW())";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ps.setString(1, patientId);
  			int update = ps.executeUpdate();
  			if (update != 0) System.out.println("CAP NHAT TRANG THAI BENH NHAN THANH CONG");
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  	}
  	
  	public void exportPrescriptionPdf() {
  		if (prescription.isEmpty()) {
  			Message.showMessage("Chưa có thuốc nào để in", AlertType.ERROR);
  			return;
  		}
  		FileChooser file = new FileChooser();
		file.setTitle("Open Save File");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)","*.pdf");
		file.getExtensionFilters().add(extFilter);
		File path = file.showSaveDialog(currentStage);
		if (path!=null) {
			System.out.println(path.getAbsolutePath());
			String savedPath = path.getAbsolutePath();
			Patient patient = choosingPatientList.getLast();
			String diagnosis = getPatientDiagnosis(patient.getPatientIdValue());
			do {
	  			uniquePrescriptionId = generateUniqueId();
	  		} while(isExistedPrescription(uniquePrescriptionId));
			PdfGeneratorUtil.exportPrescriptionPDF(savedPath, patient, uniquePrescriptionId, doctor.getDoctorId(), doctor.getDoctorName(), diagnosis, prescription);
			Message.showMessage("Xuất đơn thuốc thành công", AlertType.INFORMATION);
		} else {
			return;
		}
  		
  	}
  	
  	public String getPatientDiagnosis(String patientId) {
  		String sql = "SELECT diagnosis FROM diagnosis WHERE DATE(diagnosis_date) = DATE(NOW) AND "
  				+ "patient_id = ?";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ps.setString(1, patientId);
  			ResultSet rs = ps.executeQuery();
  			if (rs.next()) {
  				return rs.getString(1);
  			}
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		return null;
  	}
}
