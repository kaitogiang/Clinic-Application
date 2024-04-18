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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.AccentRemover;
import entity.ActiveStateUtils;
import entity.AnimationUtils;
import entity.DeepCopyUtil;
import entity.ImageUtils;
import entity.LogoutHandler;
import entity.Medicine;
import entity.MedicineButton;
import entity.Message;
import entity.Patient;
import entity.PdfGeneratorUtil;
import entity.Pharmacist;
import entity.Prescription;
import entity.PrescriptionDetail;
import entity.SelectionPrescriptionButton;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.BackgroundService;
import services.Database;

public class PharmacistController implements Initializable{

	@FXML
    private Label asideBarTitle;

    @FXML
    private Label date;
    
    @FXML
    private Label fullNameInAside;

    @FXML
    private Label fullNameInNav;
    
    @FXML
    private Label role;
	
    @FXML
    private Label time;
    
    @FXML
    private ImageView userAvatar;
	
    @FXML
    private Button medicineTab;
    
    @FXML
    private Button prescriptionTab;
    
    //Hiển thị danh sách thuốc và các thao tác
    @FXML
    private TableView<Medicine> medicineTable;
    
    @FXML
    private TableColumn<Medicine, Integer> orderNumberColumn;
    
    @FXML
    private TableColumn<Medicine, String> medicineNameColumn;

    @FXML
    private TableColumn<Medicine, String> descriptionColumn;
    
    @FXML
    private TableColumn<Medicine, String> manufacturerColumn;

    @FXML
    private TableColumn<Medicine, Integer> dosageColumn;
    
    @FXML
    private TableColumn<Medicine, String> unitDosageColumn;
    
    @FXML
    private TableColumn<Medicine, Integer> stockQuantityColumn;
    
    @FXML
    private TableColumn<Medicine, Float> priceColumn;
    
    @FXML
    private TableColumn<Medicine, String> expirationColumn;
    
    @FXML
    private TableColumn<Medicine, Void> medicineActionColumn;
    
    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;

    @FXML
    private ChoiceBox<String> sortByBox;
    
    @FXML
    private HBox sortByContainer;

    @FXML
    private Button addMedicineButton;

    @FXML
    private HBox searchBarContainer;
    
    //Chức năng quản lý đơn thuốc
    @FXML
    private AnchorPane prescriptionContainer;
    
    @FXML
    private TableView<PrescriptionDetail> prescriptionTable;
    
    @FXML
    private TableColumn<PrescriptionDetail, Integer> orderColumn;
    
    @FXML
    private TableColumn<PrescriptionDetail, String> nameColumn;

    @FXML
    private TableColumn<PrescriptionDetail, Integer> quantityColumn;
    
    @FXML
    private TableColumn<PrescriptionDetail, String> usageColumn;
    
    //--------------------------------
    @FXML
    private TableColumn<Prescription, String> prescriptionId;

    @FXML
    private TableView<Prescription> prescriptionListTable;

    @FXML
    private TableColumn<Prescription, Integer> prescriptionOrder;

    @FXML
    private TableColumn<Prescription, Void> prescriptionAction;
    
    @FXML
    private Label patientName;
    
    @FXML
    private Label patientAddress;

    @FXML
    private Label totalPrice;

    @FXML
    private Button searchPrescriptionButton;
    
    @FXML
    private HBox searchAndAddContainer;
    
    @FXML
    private HBox findPrescriptionContainer;
    
    //Thông tin cá nhân
    @FXML
    private AnchorPane personalContainer;
    
    @FXML
    private ImageView avartar;
    
    @FXML
    private Button editAvatartButton;

    @FXML
    private Button editInfoButton;
    
    @FXML
    private TextField personalNameField;

    @FXML
    private TextField personalEmailField;
    
    @FXML
    private TextField personalPhoneField;
    
    @FXML
    private TextField personalPositionField;
    
    @FXML
    private TextField personalExperienceField;
    
    @FXML
    private TextArea personalAddressField;

    @FXML
    private Button saveInfoButton;
    
	//user-defined variable
	private LocalDate currentDate;
    
	private Stage currentStage;
	
	private Scene currentScene;
	
	private Pharmacist pharamicst;
	
	private ObservableList<Medicine> medicineList;
	
	private ObservableList<Medicine> initialMedicineList;
	
	private String[] sortString = {"   Không có","Tên thuốc A-Z", "Tên thuốc Z-A", "Nhà sản xuất", "Ngày hết hạn", "Số lượng tăng", "Số lượng giảm"};
	
	private ObservableList<Prescription> prescriptionList;
	
	enum SortConditionName {
		NONE,
		MEDICINE_NAME_A_Z,
		MEDICINE_NAME_Z_A,
		MANUFACTURER,
		EXPIRATION,
		QUANTITY_INCREASE,
		QUANTITY_DESCREASE
	}
	
	private SortConditionName selectedCondition;
	
	private BackgroundService<Void> updatePrescriptionService;
	
	private ObservableList<PrescriptionDetail> prescriptionDetailList; 
	
	private List<Prescription> prescriptionQueue; //Chứa hàng đợi các mục đã chọn
	
	private List<SelectionPrescriptionButton> pressedButtonQueue; //Chứa tham chiếu của button được nhấn gần nhất
	
	private List<SelectionPrescriptionButton> prescriptionButtonList; //danh sách dùng để tùy chỉnh
																	//từng button nếu đã thanh toán
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		showTime();
		showDate();
		switchTab(null);
		sortByBox.getItems().addAll(sortString);
		medicineList = fetchMedicineData();
		initialMedicineList = FXCollections.observableArrayList();
		initialMedicineList.addAll(medicineList);
		Platform.runLater(()->{ 
			pharamicst = (Pharmacist)this.currentScene.getUserData();
			fullNameInNav.setText(pharamicst.getPharmacistName());
			fullNameInAside.setText(pharamicst.getPharmacistName());
			role.setText(pharamicst.getRole());
			Image userImage = ImageUtils.retrieveImage(pharamicst.getImageId());
			userAvatar.setImage(userImage);
			//Bo góc cho avatar
			Rectangle clip = new Rectangle(65, 66);
			clip.setArcWidth(20);
			clip.setArcHeight(20);
			userAvatar.setClip(clip);		
		});
		//Cài đặt xuống dòng khi hết hàng trong bảng
		medicineTable.setRowFactory(tv->{
			TableRow<Medicine> row = new TableRow<>();
			
			//Tạo một nút Text để tính toán chiều cao
			Text text = new Text();
			row.itemProperty().addListener((obs, oldItem, newItem) -> {
				if (newItem != null) {
					text.setText(newItem.getDescriptionValue());
					row.setPrefHeight(text.getBoundsInLocal().getHeight() + 10);
				}
			});
			return row;
		});
		
		//Sử dụng WrapText để cho phép tự động xuống dòng
		descriptionColumn.setCellFactory(tc -> {
			TableCell<Medicine, String> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			//Tự động xuống dòng nếu vượt quá chiều rộng của ô
			text.wrappingWidthProperty().bind(descriptionColumn.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});
		
		manufacturerColumn.setCellFactory(tc -> {
			TableCell<Medicine, String> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			//Tự động xuống dòng nếu vượt quá chiều rộng của ô
			text.wrappingWidthProperty().bind(manufacturerColumn.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});
		
		medicineNameColumn.setCellFactory(tc -> {
			TableCell<Medicine, String> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			//Tự động xuống dòng nếu vượt quá chiều rộng của ô
			text.wrappingWidthProperty().bind(medicineNameColumn.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});
		
		displayMedicineTable();
		
		//Hàm theo dõi trạng thái của searchTextField có chữ không
		searchTextField.textProperty().addListener((observe, oldValue, newValue)->{
			if (newValue.isEmpty()) {
				medicineList = fetchMedicineData();
				medicineTable.setItems(medicineList); //Nếu nội dung đã xóa thì khôi phục lại list ban đầu
				System.out.println("Call text property of searchTextField");
				sortByCondition(selectedCondition);
			}
		});
		
		sortByBox.getSelectionModel().select(0);
		
		sortByBox.getSelectionModel().selectedItemProperty().addListener((obs,oldValue, newValue)->{
			int index = sortByBox.getSelectionModel().getSelectedIndex();
			SortConditionName choosedCondition = SortConditionName.MEDICINE_NAME_A_Z;
			if (index == 0) {
				choosedCondition = SortConditionName.NONE;
				selectedCondition = SortConditionName.NONE;
			} else if (index == 1) {
				choosedCondition = SortConditionName.MEDICINE_NAME_A_Z;
				selectedCondition = SortConditionName.MEDICINE_NAME_A_Z;
			} else if (index == 2) {
				choosedCondition = SortConditionName.MEDICINE_NAME_Z_A;
				selectedCondition = SortConditionName.MEDICINE_NAME_Z_A;
			} else if (index == 3) {
				choosedCondition = SortConditionName.MANUFACTURER;
				selectedCondition = SortConditionName.MANUFACTURER;
			} else if (index == 4) {
				choosedCondition = SortConditionName.EXPIRATION;
				selectedCondition = SortConditionName.EXPIRATION;
			} else if (index == 5) {
				choosedCondition = SortConditionName.QUANTITY_INCREASE;
				selectedCondition = SortConditionName.QUANTITY_INCREASE;
			} else if (index == 6) {
				choosedCondition = SortConditionName.QUANTITY_DESCREASE;
				selectedCondition = SortConditionName.QUANTITY_DESCREASE;
			}
			
			sortByCondition(choosedCondition);
		});
		
		Label initalPrescription = new Label("Chưa có đơn thuốc nào");
		initalPrescription.setFont(new Font(20));
		prescriptionTable.setPlaceholder(initalPrescription);
		
		Label initialPrescriptionList = new Label("Chưa có đơn thuốc nào");
		initialPrescriptionList.setFont(new Font(17));
		prescriptionListTable.setPlaceholder(initialPrescriptionList);
		
		//Gán giá trị đầu tiên cho prescriptionIdList;
		prescriptionList = fetchPrescriptionData();
		//Cập nhật lại prescriptonList theo thời gian thực mỗi 5 giây
		updatePrescriptionService = new BackgroundService<>(()->updatePrescriptionList(),5);
		updatePrescriptionService.start();
		//Hiển thị danh sách các đơn thuốc được bác sĩ chỉ định
		displayPrescriptionTable();
		//Hiển thị thông tin bệnh nhân của đơn thuốc được chon
		setInitialPatientInfo();
		//Khởi tạo danh sách hiển thị prescriptionDetailList
		prescriptionDetailList = FXCollections.observableArrayList();
		
		prescriptionQueue = new ArrayList<Prescription>();
		pressedButtonQueue = new ArrayList<>();
		prescriptionButtonList = new ArrayList<>();
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
	
	public void logout() {
		stopBackgroundService();
		Stage currentStage = (Stage) asideBarTitle.getScene().getWindow();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginScreen.fxml"));
		LogoutHandler.logout(currentStage, loader);
	}
	
	public void stopBackgroundService() {
		updatePrescriptionService.cancel();
	}
	public void setCurrentStage(Stage stage) {
		currentStage = stage;
	}
	
	public void setMyScene(Scene scene) {
		currentScene = scene;
	}
	
	public void switchTab(ActionEvent e) {
		if (e==null || e.getSource().equals(medicineTab)) {
			//Hiển thị tab chính
			medicineTable.setVisible(true);
  			asideBarTitle.setText("Quản lý thuốc");
  			sortByContainer.setVisible(true);
  			searchAndAddContainer.setVisible(true);
			//Ẩn các tab còn lại
			personalContainer.setVisible(false);
			prescriptionContainer.setVisible(false);
  			findPrescriptionContainer.setVisible(false);
			//Tạo animation chuyển động cho các nút
			AnimationUtils.createFadeTransition(medicineTable, 0.0, 10.0);
			AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
			AnimationUtils.createFadeTransition(sortByContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(searchBarContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(addMedicineButton, 0.0, 10.0);
			//Tạo trạng thái active cho tab
			ActiveStateUtils.addStyleClass(medicineTab);
			ActiveStateUtils.removeStyleClass(prescriptionTab);
		} else if (e.getSource().equals(prescriptionTab) && e!=null) {
			//Hiển thị tab chính
			prescriptionContainer.setVisible(true);
  			asideBarTitle.setText("Quản lý đơn thuốc");
  			findPrescriptionContainer.setVisible(true);
  			searchPrescriptionButton.setVisible(true);
  			displayPrescriptionDetail();
  			//Hàm hiển thị trạng thái đã thanh toán nếu kiểm tra id đơn hàng tồn tại trên bảng invoice
  			modifyPrescriptionButtonState();
			//Ẩn các tab còn lại
			medicineTable.setVisible(false);
			sortByContainer.setVisible(false);
			personalContainer.setVisible(false);
  			searchAndAddContainer.setVisible(false);
			//Tạo animation chuyển động cho các nút
			AnimationUtils.createFadeTransition(prescriptionContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
			AnimationUtils.createFadeTransition(searchPrescriptionButton, 0.0, 10.0);
			//Tạo trạng thái active cho tab
			ActiveStateUtils.addStyleClass(prescriptionTab);
			ActiveStateUtils.removeStyleClass(medicineTab);
		}
		
	}
	
	//Chuyển đến tab trang cá nhân của người dùng
  	public void goToPersonalInfo() {
  		//Ẩn hiện các mục cần thiết
  		asideBarTitle.setText("Thông tin cá nhân");
  		personalContainer.setVisible(true);
  		saveInfoButton.setVisible(false);
  		medicineTable.setVisible(false);
  		prescriptionContainer.setVisible(false);
  		sortByContainer.setVisible(false);
  		searchAndAddContainer.setVisible(false);
		findPrescriptionContainer.setVisible(false);

  		//Loại bỏ chọn tab
  		ActiveStateUtils.removeStyleClass(medicineTab);
  		ActiveStateUtils.removeStyleClass(prescriptionTab);
  		
  		AnimationUtils.createFadeTransition(personalContainer, 0.0, 10.0);
  		AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
  		//Khởi tạo dữ liệu cho các trường trong thông tin cá nhân
  		personalNameField.setText(pharamicst.getPharmacistName());
  		personalEmailField.setText(pharamicst.getPharmacistEmail());
  		personalPhoneField.setText(pharamicst.getPhoneNumber());
  		personalPositionField.setText(pharamicst.getRole());
  		personalAddressField.setText(pharamicst.getAddress());
  		personalExperienceField.setText(String.valueOf(pharamicst.getExperienceYear()));
  		Image userImg = ImageUtils.retrieveImage(pharamicst.getImageId());
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
  	
  	public void updateAvatar() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image File", "*.png", "*.jpg"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile!=null) {
			Image image = new Image(selectedFile.toURI().toString());
			avartar.setImage(image);
			userAvatar.setImage(image);
			ImageUtils.updateImage(selectedFile.getAbsolutePath(), pharamicst.getImageId());
			Message.showMessage("Cập nhật ảnh đại diện thành công", AlertType.INFORMATION);
		}
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
  	  		String sql = "UPDATE pharmacists SET pharmacist_name = ?, pharmacist_email = ?, address = ?, "
				+ "phone_number = ? WHERE pharmacist_id = ?";
	  	  	try(Connection con = Database.connectDB()) {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, personalNameField.getText());
				ps.setString(2, personalEmailField.getText());
				ps.setString(3, personalAddressField.getText());
				ps.setString(4, personalPhoneField.getText());
				ps.setString(5, pharamicst.getPharmacistId());
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
	
  	public ObservableList<Medicine> fetchMedicineData() {
  		ObservableList<Medicine> list = FXCollections.observableArrayList();
  		String sql = "SELECT * FROM medicine";;
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ResultSet rs = ps.executeQuery();
  			int orderNumber = 1;
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
    			String expirationDate = String.valueOf(date.format(formatter));
    			int stockQuantity = rs.getInt("stock_quantity");
    			Medicine med = new Medicine(medicineId, orderNumber, medicineName, description, manufacturer, dosage, dosageUnit, unitPrice, expirationDate, stockQuantity);
    			orderNumber++;
    			list.add(med);
  			}
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		
  		return list;
  	}
  	
  	public void displayMedicineTable() {
  		orderNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
  		medicineNameColumn.setCellValueFactory(cellData -> cellData.getValue().getMedicineName());
  		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().getDescription());
  		manufacturerColumn.setCellValueFactory(cellData -> cellData.getValue().getManufacturer());
  		dosageColumn.setCellValueFactory(cellData -> cellData.getValue().getDosage().asObject());
  		unitDosageColumn.setCellValueFactory(cellData -> cellData.getValue().getDosage_unit());
  		stockQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().getQuantity().asObject());
  		priceColumn.setCellValueFactory(cellData -> cellData.getValue().getUnit_price().asObject());
  		expirationColumn.setCellValueFactory(cellData -> cellData.getValue().getExpiration_date());
  		medicineActionColumn.setCellFactory(params -> new MedicineButton(this));
  		medicineTable.setItems(medicineList);
  	}
  	
  	public int getMedicineListSize() {
  		return medicineList.size();
  	}
  	
  	public void addMedicineToList(Medicine e) {
  		medicineList.add(e);
  		System.out.println("Adding new medicine successfully");
  	}
  	//Hàm callback sẽ được sử dụng trong MedicineButton truyền vào MedicineForm
  	public void updateMedicineList(int index, Medicine newItem) {
  		medicineList.set(index, newItem);
  	}
  	
  	
  	public void openAddingMedicineForm() {
  		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MedicineForm.fxml"));
		String css = this.getClass().getResource("/css/style.css").toExternalForm();
		try {
			AnchorPane root = loader.load();
			MedicineFormController controller = loader.getController();
			controller.setParentController(this);
			//tương dương với (item) -> patientList.add(item);
			Stage newStage = new Stage();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(css);
			//Tạo icon cho stage
			Image image = new Image(getClass().getResourceAsStream("/images/healthcare.png"));
			newStage.getIcons().add(image);
			newStage.setScene(scene);
			newStage.setTitle("Adding medicine form");
			newStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
  	}
  	
  	public void findPatient() {
  		String searchText = searchTextField.getText().trim().toLowerCase();
		if (searchText.isEmpty()) {
			System.out.println("Empty Search Text");
		} else {
			ObservableList<Medicine> filteredList = DeepCopyUtil.createDeepMedicineListCopy(medicineList);
			filteredList = 
					filteredList.filtered(e->(e.toString().toLowerCase().
							contains(searchText)) || AccentRemover.removeDiacritics(e.toString().toLowerCase()).
							contains(searchText));
			System.out.println(filteredList);
			//Cho phép nó tìm trước rồi sau đó mới
			medicineList.clear();
			System.out.println(filteredList);
			medicineList.addAll(filteredList);
//			medicineTable.setItems(filteredList);
			System.out.println("Call find patient");
		}
  	}
  	
  	public void sortByCondition(SortConditionName conditionIndex) {
  		if (conditionIndex == SortConditionName.NONE) {
			medicineList.clear();
			initialMedicineList = fetchMedicineData();
			medicineList.addAll(initialMedicineList);
  			findPatient();
  		}
  		if (conditionIndex == SortConditionName.MEDICINE_NAME_A_Z) {
  			quickSort(medicineList, 0, medicineList.size()-1, conditionIndex);
  			int i = 1;
  			for(Medicine m : medicineList) {
  				m.setOrderNumber(i++);
  			}
  		} else if (conditionIndex == SortConditionName.MEDICINE_NAME_Z_A) {
  			rQuickSort(medicineList, 0, medicineList.size()-1, conditionIndex);
  			int i = 1;
  			for(Medicine m : medicineList) {
  				m.setOrderNumber(i++);
  			}
  		} else if (conditionIndex == SortConditionName.MANUFACTURER) {
  			quickSort(medicineList, 0, medicineList.size()-1, conditionIndex);
  			int i = 1;
  			for(Medicine m : medicineList) {
  				m.setOrderNumber(i++);
  			}
  		} else if (conditionIndex == SortConditionName.EXPIRATION) {
  			quickSort(medicineList, 0, medicineList.size()-1, conditionIndex);
  			int i = 1;
  			for(Medicine m : medicineList) {
  				m.setOrderNumber(i++);
  			}
  		} else if (conditionIndex == SortConditionName.QUANTITY_INCREASE) {
  			quickSort(medicineList, 0, medicineList.size()-1, conditionIndex);
  			int i = 1;
  			for(Medicine m : medicineList) {
  				m.setOrderNumber(i++);
  			}
  		} else if (conditionIndex == SortConditionName.QUANTITY_DESCREASE) {
  			rQuickSort(medicineList, 0, medicineList.size()-1, conditionIndex);
  			int i = 1;
  			for(Medicine m : medicineList) {
  				m.setOrderNumber(i++);
  			}
  		}
  	}
  	//Giải thuật sắp xếp tăng dần theo tên thuốc
  	public void quickSort(ObservableList<Medicine> arr, int low, int high, SortConditionName index) {
  		if (low < high) {
  			int pivot = partition(arr, low, high, index);
  			quickSort(arr, low, pivot - 1, index);
  			quickSort(arr, pivot + 1, high, index);
  		}
  	}
  	//Giải thuật sắp xếp tăng dần theo tên thuốc
  	public int partition(ObservableList<Medicine> arr, int low, int high, SortConditionName index) {
  		Medicine pivot = arr.get(high);
  		int i = low - 1;
  		int j;
  		for(j = low; j < high; j++) {
  			if (index == SortConditionName.MEDICINE_NAME_A_Z 
  			&& arr.get(j).getMedicineNameValue().compareTo(pivot.getMedicineNameValue())<0) {
  				i++;
  				//Swap arr[i] and arr[j]
  				Medicine temp = arr.get(i);
  				arr.set(i, arr.get(j));
  				arr.set(j, temp);
  			} else if (index == SortConditionName.MANUFACTURER 
  					&& arr.get(j).getManufacturerValue().compareTo(pivot.getManufacturerValue())<0) {
  				i++;
  				//Swap arr[i] and arr[j]
  				Medicine temp = arr.get(i);
  				arr.set(i, arr.get(j));
  				arr.set(j, temp);
  			} else if (index == SortConditionName.EXPIRATION) {
  		        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
  		        try {
  		        	Date date1 = sdf.parse(arr.get(j).getExpirationDateValue());
  		        	Date date2 = sdf.parse(pivot.getExpirationDateValue());
  		        	if (date1.compareTo(date2)<0) {
  		        		i++;
  		  				//Swap arr[i] and arr[j]
  		  				Medicine temp = arr.get(i);
  		  				arr.set(i, arr.get(j));
  		  				arr.set(j, temp);
  		        	}
  		        } catch(Exception e) {
  		        	e.printStackTrace();
  		        }
  			} else if (index == SortConditionName.QUANTITY_INCREASE
  					&& arr.get(j).getQuantityValue()<pivot.getQuantityValue()) {
  					i++;
	  				//Swap arr[i] and arr[j]
	  				Medicine temp = arr.get(i);
	  				arr.set(i, arr.get(j));
	  				arr.set(j, temp);
  			}
  		}
  		
  		//Swap arr[i+1] and arr[high] pivot
  		Medicine temp = arr.get(i+1);
  		arr.set(i+1, arr.get(high));
  		arr.set(high, temp);
  		
  		return i + 1;
  	}
  	
  	//Giải thuật sắp xếp giảm dần theo tên thuốc
  	public void rQuickSort(ObservableList<Medicine> arr, int low, int high, SortConditionName index) {
  		if (low < high) {
  			int pivot = rPartition(arr, low, high, index);
  			rQuickSort(arr, low, pivot - 1, index);
  			rQuickSort(arr, pivot + 1, high, index);
  		}
  	}
  	//Giải thuật sắp xếp giảm dần theo tên thuốc
  	public int rPartition(ObservableList<Medicine> arr, int low, int high, SortConditionName index) {
  		Medicine pivot = arr.get(high);
  		int i = low - 1;
  		int j;
  		for(j = low; j < high; j++) {
  			if (index == SortConditionName.MEDICINE_NAME_Z_A
  				&& arr.get(j).getMedicineNameValue().compareTo(pivot.getMedicineNameValue())>=0) {
  				i++;
  				
  				//Swap arr[i] and arr[j]
  				Medicine temp = arr.get(i);
  				arr.set(i, arr.get(j));
  				arr.set(j, temp);
  			} else if (index == SortConditionName.QUANTITY_DESCREASE
  					&& arr.get(j).getQuantityValue() >= pivot.getQuantityValue()) {
  				i++;
  				//Swap arr[i] and arr[j]
  				Medicine temp = arr.get(i);
  				arr.set(i, arr.get(j));
  				arr.set(j, temp);
  			}
  		}
  		
  		//Swap arr[i+1] and arr[high] pivot
  		Medicine temp = arr.get(i+1);
  		arr.set(i+1, arr.get(high));
  		arr.set(high, temp);
  		
  		return i + 1;
  	}
  	
  	public ObservableList<Prescription> fetchPrescriptionData() {
  		ObservableList<Prescription> list = FXCollections.observableArrayList();
  		String sql = "SELECT pp.*, p.prescription_id, "
  				+ "p.notes  FROM prescription p JOIN diagnosis d "
  				+ "ON p.prescription_id = d.prescription_id "
  				+ "JOIN patients pp ON d.patient_id = pp.patient_id "
  				+ "WHERE DATE(creation_date) = DATE(NOW()) ORDER BY creation_date";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ResultSet rs = ps.executeQuery();
  			con.setAutoCommit(false);
  			int orderNumber = 1;
  			while(rs.next()) {
  				String patientId = rs.getString("patient_id");
  				String patientName = rs.getString("patient_name");
  				int age = rs.getInt("age");
  				float weight = rs.getFloat("weight");
  				String address = rs.getString("address");
  				String phone = rs.getString("phone_number");
  				Patient patient = new Patient(patientId, patientName, age, weight, address, phone, 0);
  				String prescriptionId = rs.getString("prescription_id");
  				String notes = rs.getString("notes");
  				Prescription pres = new Prescription(orderNumber++, prescriptionId, patient, notes);
  				list.add(pres);
  			}
  			
  			//Thực hiện gán chi tiết đon thuốc (danh sách thuốc) vào mỗi đơn thuốc
  			for(Prescription e: list) {
  				String prescriptionId = e.getPrescriptionIdvalue();
  				String detailSql = "SELECT m.medicine_id, "
  								+ "m.medicine_name, "
  								+ "p.medicine_amount, "
  								+ "p.medicine_usage "
  								+ "FROM prescriptiondetail p "
  								+ "JOIN medicine m ON p.medicine_id = m.medicine_id "
  								+ "WHERE prescription_id = ?";
  				try(Connection detailCon = Database.connectDB()) {
  					PreparedStatement detailPs = con.prepareStatement(detailSql);
  					detailPs.setString(1, prescriptionId);
  					ResultSet resultSet = detailPs.executeQuery();
  					ObservableList<PrescriptionDetail> prescriptionDetailList = FXCollections.observableArrayList();
  					int order = 1;
  					while(resultSet.next()) {
  						String medicineId = resultSet.getString(1);
  						String medicineName = resultSet.getString(2);
  						int medicineAmount = resultSet.getInt(3);
  						String medicineUsage = resultSet.getString(4);
  						Medicine medicine = getSpecificMedicine(medicineId);
  						PrescriptionDetail item = new PrescriptionDetail(order++, medicineName, medicineAmount, medicineUsage, medicine);
  						prescriptionDetailList.add(item);
  					}
  					e.setMedicineList(prescriptionDetailList);
  				} catch(Exception ex) {
  					ex.printStackTrace();
  				}
  			}
  			return list;
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		return null;
  	}
  	
  	public Medicine getSpecificMedicine(String medicineId) {
  		String sql = "SELECT * FROM medicine WHERE medicine_id = ?";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ps.setString(1, medicineId);
  			ResultSet rs = ps.executeQuery();
  			Medicine medicine = new Medicine();
  			if (rs.next()) {
  				String medicineName = rs.getString("medicine_name");
  				float unitPirce = rs.getFloat("unit_price");
  				medicine = new Medicine(medicineId, medicineName, unitPirce);
  			}
  			return medicine;
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		return null;
  	}
  	
  	//Hàm dùng để cập nhật lại prescriptionList nếu có đơn thuốc mới được thêm vào
  	public void updatePrescriptionList() {
  		ObservableList<Prescription> newList = fetchPrescriptionData();
  		if (prescriptionList.size() != newList.size()) {
  			System.out.println("The prescription has been changed!!!");
  			int i;
  			for(i=prescriptionList.size(); i< newList.size(); i++) {
  				prescriptionList.add(newList.get(i));
  			}
  		}
  	}
  	public void displayPrescriptionTable() {
  		prescriptionOrder.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
  		prescriptionId.setCellValueFactory(cellData -> cellData.getValue().getPrescriptionId());
  		prescriptionAction.setCellFactory(param -> {
  			SelectionPrescriptionButton button = new SelectionPrescriptionButton(this);
            return button;
  		});
  		
  		prescriptionListTable.setItems(prescriptionList);
  	}
  	//Hàm reset lại pane hiển thị thông tin bệnh nhân khi chọn một đơn thuốc
  	public void setInitialPatientInfo() {
  		patientName.setText("Chưa chọn");
		patientAddress.setText("Không rõ");
		totalPrice.setText("0 VND");
  	}
  	//Hàm thiết lập hiển thị thông tin bệnh nhân
  	public void setInitialPatientInfo(String name, String address, String money) {
  		patientName.setText(name);
		patientAddress.setText(address);
		totalPrice.setText(money+" VNĐ");
  	}
  	
  	//Hàm hiển thị prescription detail cho một đơn thuốc
  	public void displayPrescriptionDetail() {
  		orderColumn.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
  		nameColumn.setCellValueFactory(cellData -> cellData.getValue().getMedicineName());
  		quantityColumn.setCellValueFactory(cellData -> cellData.getValue().getMedicineQuantity().asObject());
  		usageColumn.setCellValueFactory(cellData -> cellData.getValue().getUsage());
  		prescriptionTable.setItems(prescriptionDetailList);
  	}
  	//Hàm chỉnh sửa prescription detail 
  	public void setPrescriptionDetailList(ObservableList<PrescriptionDetail> newList) {
  		prescriptionDetailList.clear();
  		prescriptionDetailList.addAll(newList);
  		System.out.println(prescriptionDetailList.size()+" phan tu");
  	}
  	//Reset lại prescription detail list
  	public void resetPrescriptionDetailList() {
  		prescriptionDetailList.clear();
  	}
  	//Các hàm thao tác trên hàng đợi
  	public void addPrescriptionQueue(Prescription e) {
  		prescriptionQueue.add(e);
  	}
  	//Hàm thêm chỉ số đã chọn
  	public void addSelectedButtonToQueue(SelectionPrescriptionButton e) {
  		pressedButtonQueue.add(e);
  	}
  	//Hàm xóa khỏi hàng đợi
  	public void removeSelectedButtonFromQueue(SelectionPrescriptionButton e) {
  		pressedButtonQueue.remove(e);
  	}
  	
  	public void removePrescriptionQueue(Prescription e) {
  		prescriptionQueue.remove(e);
  	}
  	//Lấy prescription detail hiện tại
  	public List<Prescription> getPrescriptionQueue() {
  		return prescriptionQueue;
  	}
  	public List<SelectionPrescriptionButton> getPressedButtonQueue() {
  		return pressedButtonQueue;
  	}
  	//Chức năng thanh toán khi chọn đơn thuốc
  	public void payPrescription() {
  		if (prescriptionQueue.size() == 0) {
  			Message.showMessage("Vui lòng chọn đơn thuốc để thanh toán", AlertType.ERROR);
  			return;
  		}
  		Prescription selectedItem = prescriptionQueue.getLast();
  		//Trích xuất các giá trị
  		Patient patient = selectedItem.getPatientValue();
  		LocalDate date = LocalDate.now();
  		String uniqueId = "";
  		do {
  			uniqueId = generateUniqueId();
  		} while(isExistInvoiceId(uniqueId));
  		ObservableList<PrescriptionDetail> medicineList = selectedItem.getMedicineList();
  		
  		Map<String, Object> map = new HashMap<>();
  		map.put("patient", patient);
  		map.put("date", date);
  		map.put("invoiceId", uniqueId);
  		map.put("medicineList", medicineList);
  		map.put("pharmacist", pharamicst);
  		map.put("prescriptionId", selectedItem.getPrescriptionIdvalue());
  		
		String css = this.getClass().getResource("/css/style.css").toExternalForm();
  		try {
  			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/InvoiceFormScreen.fxml"));
  			AnchorPane mainInvoice = loader.load();
  			InvoiceController controller = loader.getController();
  			controller.setData(map);
  			controller.setparentController(this);
  			Scene newScene = new Scene(mainInvoice);
  			newScene.getStylesheets().add(css);
  			Stage newStage = new Stage();
  			newStage.setScene(newScene);
  			newStage.show();
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  	}
  	
  	public void exportPrescription() {
  		if (prescriptionQueue.size() == 0) {
  			Message.showMessage("Vui lòng chọn đơn thuốc trước khi in", AlertType.ERROR);
  		} else {
  			//truy xuất dữ liệu cần thiết
  			Prescription selectedItem = prescriptionQueue.getLast();
  			//Lấy thông tin bệnh nhân
  			Patient patient = selectedItem.getPatientValue();
  			//Lấy Id đơn thuốc
  			String prescriptionId = selectedItem.getPrescriptionIdvalue();
  			//Lấy danh sách thuốc của đơn thuốc
  			ObservableList<PrescriptionDetail> medicineList = selectedItem.getMedicineList();
  			//Lấy thông tin bác sĩ và chuẩn đoán
  			String doctorId = "";
  			String doctorName = "";
  			String diagnosis = "";
  			String sql = "SELECT d.doctor_id, "
  					+ "d.doctor_name, di.diagnosis "
  					+ "FROM diagnosis di JOIN doctors d "
  					+ "ON di.doctor_id = d.doctor_id WHERE prescription_id = ? "
  					+ "AND DATE(diagnosis_date) = DATE(NOW())";
  			try(Connection con = Database.connectDB()) {
  				PreparedStatement ps = con.prepareStatement(sql);
  				ps.setString(1, prescriptionId);
  				ResultSet rs = ps.executeQuery();
  				if (rs.next()) {
  					doctorId = rs.getString("doctor_id");
  					doctorName = rs.getString("doctor_name");
  					diagnosis = rs.getString("diagnosis");
  				}
  			} catch(Exception e) {
  				e.printStackTrace();
  			}
  			
  			//Thực hiện việc in đơn thuốc
  			FileChooser file = new FileChooser();
  			file.setTitle("Open Save File");
  			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)","*.pdf");
  			file.getExtensionFilters().add(extFilter);
  			File path = file.showSaveDialog(currentStage);
  			if (path!=null) {
  				PdfGeneratorUtil.exportPrescriptionPDF(path.getAbsolutePath(), patient, prescriptionId, doctorId, doctorName, diagnosis, medicineList);
  				Message.showMessage("Xuất đơn thuốc thành công", AlertType.INFORMATION);
  			}
  		}
  	}
  	//Hàm tạo id duy nhất
  	public String generateUniqueId() {
 		 // Tạo một UUID mới
       UUID uniqueId = UUID.randomUUID();

       // Chuyển UUID thành chuỗi và lấy 5 ký tự đầu tiên
       String shortId = uniqueId.toString().substring(0, 5);

       // In mã ID ngắn
       System.out.println("Short ID: " + shortId);
       
       return shortId;
 	}
  	
  	public boolean isExistInvoiceId(String id) {
  		String sql = "SELECT * FROM invoice WHERE invoice_id = ?";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ps.setString(1, id);
  			ResultSet rs = ps.executeQuery();
  			if (rs.next()) {
  				return true;
  			}
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		return false;
  	}
  	
  	//Hàm lấy đơn thuốc
  	public TableView<Prescription> getPrescriptionTable() {
  		return prescriptionListTable;
  	}
  	//Tạo ra một hàm dùng để cài lại giá trị của SelectionPrescriptionButton thành nhãn đã thanh toán
  	//Trong trường hợp người này đã thanh toán
  	public void modifyPrescriptionButtonState() {
  		System.out.println("Goi modify");
  		int i;
  		for(i=0; i<prescriptionListTable.getItems().size(); i++) {
  			Prescription item = prescriptionListTable.getItems().get(i);
  			if (isExistInvoiceId(item.getPrescriptionIdvalue())) {
	  			SelectionPrescriptionButton button = prescriptionButtonList.get(i);
	  			button.setSuccessLabel();
  			}
  		}
  	}
  	
  	public List<SelectionPrescriptionButton> getPrescriptionButtonList() {
  		return prescriptionButtonList;
  	}
  	
  	//Hàm kiểm tra xem đã thanh toán cho đơnthuốc chưa
  	public boolean isPaiedPrescription(String prescriptinId) {
		String sql = "SELECT * FROM invoice WHERE prescription_id = ?";
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, prescriptinId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
  	
  	//Hàm mở tab tìm kiếm
  	public void seachPrescription() {
  		try {
  	  		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SearchScreen.fxml"));
  			String css = this.getClass().getResource("/css/style.css").toExternalForm();
  	  		AnchorPane container = loader.load();
  	  		Scene newScene = new Scene(container);
  	  		newScene.getStylesheets().add(css);
  	  		Stage newStage = new Stage();
  	  		newStage.setScene(newScene);
  	  		newStage.show();
  		} catch(Exception e) {
  			System.out.println(e);
  		}
  	}
}
