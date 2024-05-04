package controllers;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.ActiveStateUtils;
import entity.Admin;
import entity.AnimationUtils;
import entity.Doctor;
import entity.Employee;
import entity.EmployeeButton;
import entity.ImageUtils;
import entity.Message;
import entity.Pharmacist;
import entity.Receptionist;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.Database;

public class AdminController implements Initializable{

    @FXML
    private AnchorPane accountContainer;

    @FXML
    private Button accountTab;

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
    private Label finishedQuantity;

    @FXML
    private Label fullNameInAside;

    @FXML
    private Label fullNameInNav;

    @FXML
    private Button logoutButton;

    @FXML
    private Button medicalSuppliesTab;

    @FXML
    private AnchorPane overviewContainer;

    @FXML
    private Button overviewTab;

    @FXML
    private ScrollPane overviewScrollPane;
    
    @FXML
    private TextArea personalAddressField;

    @FXML
    private AnchorPane personalContainer;

    @FXML
    private TextField personalEmailField;

    @FXML
    private TextField personalExperienceField;

    @FXML
    private TextField personalNameField;

    @FXML
    private TextField personalPhoneField;

    @FXML
    private TextField personalPositionField;

    @FXML
    private AnchorPane prescriptionContainer;

    @FXML
    private Button revenueTab;

    @FXML
    private Label role;

    @FXML
    private AnchorPane rootContainer;

    @FXML
    private Button saveInfoButton;

    @FXML
    private HBox statusNumberContainer;

    @FXML
    private Label time;

    @FXML
    private ImageView userAvatar;

    @FXML
    private Label waitingQuantity;
    
    //Giao diện quản lý tài khoản
    @FXML
    private TableView<Employee> employeeTable;
    
    @FXML
    private TableColumn<Employee, String> employeeNameColumn;

    @FXML
    private TableColumn<Employee, String> usernameColumn;
    
    @FXML
    private TableColumn<Employee, Integer> orderEmployeeColumn;
    
    @FXML
    private TableColumn<Employee, String> roleColumn;
    
    @FXML
    
    private TableColumn<Employee, Void> actionColumn;
    
    @FXML
    private HBox filterContainer;
    
    @FXML
    private ChoiceBox<String> filterByBox;
    
    @FXML
    private HBox ActionBar;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button accountCreationButton;
    
    @FXML
    private AnchorPane addingUserForm;
    
    @FXML
    private ChoiceBox<String> roleChoiceBox;

    @FXML
    private TextField addressField;
    
    @FXML
    private TextField fullNameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private TextField experienceYearField;
    
    @FXML
    private Button uploadImageButton;
    
    @FXML
    private Label imageFileName;
    
    @FXML
    private Button addingUserButton;
    
    @FXML
    private AnchorPane addingAccountForm;
    
    @FXML
    private ComboBox<String> choosingAvalableUserBox;
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private TextField passwordAuthenticationField;

    @FXML
    private TextField passwordField;
    
    @FXML
    private Button addingAccountButton;
    
    @FXML
    private Button closeAccountCreationButton;
    
    @FXML
    private Label roleLabel;
    
    
    
    //user-defined variable
    
    private Stage currentStage;
    
    private Scene currentScene;
    
    private LocalDate currentDate;
    
    private Admin admin;
    
    private ObservableList<Employee> employeeList;
    
    private String[] rolesString = {"Nhân viên lễ tân", "Bác sĩ", "Nhân viên bán thuốc"};
    
    private StringProperty selectedImagePath = new SimpleStringProperty("Chưa có ảnh");
    
    private String imageUriString = ""; //Dùng để tạo file ảnh
    
    private String absoluteImagePath = ""; //Lấy đường dẫn tuyệt đối của ảnh để upload
    
    private Map<String, String[]> roleMap; //Map để chứa các mô tả và quyên hạn của các vai trò
    
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		showDate();
		showTime();
    	Platform.runLater(()->{ 
			admin = (Admin)this.currentScene.getUserData();
			fullNameInNav.setText(admin.getAdminName());
			fullNameInAside.setText(admin.getAdminName());
			role.setText(admin.getRole());
			Image userImage = ImageUtils.retrieveImage(admin.getImageId());
			userAvatar.setImage(userImage);
			//Bo góc cho avatar
			Rectangle clip = new Rectangle(65, 66);
			clip.setArcWidth(20);
			clip.setArcHeight(20);
			userAvatar.setClip(clip);		
		});
    	showOverviewScreen();
    	switchTab(null);
    	employeeList = fetchEmployeeData();
    	displayEmployeeTable();
    	//Gán giá trị cho choice box chọn vai trò người dùng khi tạo người dùng
    	roleChoiceBox.getItems().addAll(rolesString);
    	//Quan sát lựa chọn vai trò của admin
    	roleChoiceBox.getSelectionModel().select(0);
    	experienceYearField.setVisible(false);
    	roleChoiceBox.valueProperty().addListener((observe, oldValue, newValue)->{
    		if (newValue.equals(rolesString[0])) {
    			System.out.println("Đã chọn nhân viên lễ tân");
    			experienceYearField.setVisible(false); //Ẩn trường kinh nghiệm đi vì nhân viên lễ tân không cần Kn
    		} else if (newValue.equals(rolesString[1])) {
    			System.out.println("Đã chọn bác sĩ");
    			experienceYearField.setVisible(true);

    		} else {
    			System.out.println("Đã chọn nhân viên bán thuốc");
    			experienceYearField.setVisible(true);
    		}
    	});
    	//Khởi tạo các danh sách
    	roleMap = getRoleMap(); //Lấy danh sách các role ở dạng map
    	
    	
    	//Gắn kết khi chọn file thì hiển thị ngay tên file
    	imageFileName.textProperty().bind(selectedImagePath);
    	//Popup dùng để hiện thị ảnh lớn hơn khi người dùng rê chuột vào
    	Popup popup = new Popup();
    	
    	imageFileName.setOnMouseEntered(e->{
    		if (imageUriString.isEmpty()) return;
    		Image img = new Image(imageUriString);
    		ImageView image = new ImageView(img);
    		image.setFitWidth(249);
    		image.setFitHeight(249);
    		popup.getContent().add(image);
    		popup.show(currentStage, 1292-50, 618-249);
    	});
    	imageFileName.setOnMouseExited(e->{
    		popup.getContent().clear();
    		popup.hide();
    	});
	}
    
    public void setCurrentStage(Stage stage) {
    	this.currentStage = stage;
    }
    
    public void setMyScene(Scene scene) {
    	this.currentScene = scene;
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
    
    public void showOverviewScreen() {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminHome.fxml"));
    	try {
    		AnchorPane main = loader.load();
    		overviewScrollPane.setContent(main);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
   
    public void switchTab(ActionEvent e) {
		if (e==null || e.getSource().equals(overviewTab)) {
			//Hiển thị tab chính
  			asideBarTitle.setText("Tổng quan");
  			overviewContainer.setVisible(true);
			//Ẩn các tab còn lại
			personalContainer.setVisible(false);
			accountContainer.setVisible(false);
			filterContainer.setVisible(false);
			ActionBar.setVisible(false);
			
			//Tạo animation chuyển động cho các nút
			AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
			AnimationUtils.createFadeTransition(overviewContainer, 0.0, 10.0);
			//Tạo trạng thái active cho tab
			ActiveStateUtils.addStyleClass(overviewTab);
			ActiveStateUtils.removeStyleClass(accountTab);
			ActiveStateUtils.removeStyleClass(revenueTab);
			ActiveStateUtils.removeStyleClass(medicalSuppliesTab);

		} else if (e.getSource().equals(accountTab) && e!=null) {
			//Hiển thị tab chính
  			asideBarTitle.setText("Quản lý tài khoản người dùng");
			accountContainer.setVisible(true);
			filterContainer.setVisible(true);
			ActionBar.setVisible(true);
			addingAccountForm.setVisible(false);
			addingUserForm.setVisible(true);
			//Ẩn các tab còn lại
  			overviewContainer.setVisible(false);
			personalContainer.setVisible(false);
			//Tạo animation chuyển động cho các nút
			AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
			AnimationUtils.createFadeTransition(accountContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(filterContainer, 0.0, 10.0);
			AnimationUtils.createFadeTransition(ActionBar, 0.0, 10.0);

			//Tạo trạng thái active cho tab
			ActiveStateUtils.addStyleClass(accountTab);
			ActiveStateUtils.removeStyleClass(overviewTab);
			ActiveStateUtils.removeStyleClass(revenueTab);
			ActiveStateUtils.removeStyleClass(medicalSuppliesTab);
			
			//Thực hiện các tác vụ khi chuyển đến giao diện Quản lý tài khoản
			resetUserCreationForm();
		} else if (e.getSource().equals(revenueTab) && e!=null) {
			
		} else if (e.getSource().equals(medicalSuppliesTab) && e!=null) {
			
		}
		
	}
    
    
  //Chuyển đến tab trang cá nhân của người dùng
  	public void goToPersonalInfo() {
  		//Ẩn hiện các mục cần thiết
  		asideBarTitle.setText("Thông tin cá nhân");
  		personalContainer.setVisible(true);
  		saveInfoButton.setVisible(false);
  		overviewContainer.setVisible(false);
  		accountContainer.setVisible(false);
  		filterContainer.setVisible(false);
		ActionBar.setVisible(false);
  		
  		//Loại bỏ chọn tab
  		ActiveStateUtils.removeStyleClass(overviewTab);
  		ActiveStateUtils.removeStyleClass(accountTab);
		ActiveStateUtils.removeStyleClass(revenueTab);
		ActiveStateUtils.removeStyleClass(medicalSuppliesTab);

  		AnimationUtils.createFadeTransition(personalContainer, 0.0, 10.0);
  		AnimationUtils.createFadeTransition(asideBarTitle, 0.0, 10.0);
  		//Khởi tạo dữ liệu cho các trường trong thông tin cá nhân
  		personalNameField.setText(admin.getAdminName());
  		personalEmailField.setText(admin.getAdminEmail());
  		personalPhoneField.setText(admin.getPhoneNumber());
  		personalPositionField.setText(admin.getRole());
  		personalAddressField.setText(admin.getAddress());
  		personalExperienceField.setText(String.valueOf(admin.getExperience_year()));
  		Image userImg = ImageUtils.retrieveImage(admin.getImageId());
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
			ImageUtils.updateImage(selectedFile.getAbsolutePath(), admin.getImageId());
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
  	  		String sql = "UPDATE admin SET admin_name = ?, admin_email = ?, admin_address = ?, "
				+ "admin_phone = ? WHERE admin_id = ?";
	  	  	try(Connection con = Database.connectDB()) {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, personalNameField.getText());
				ps.setString(2, personalEmailField.getText());
				ps.setString(3, personalAddressField.getText());
				ps.setString(4, personalPhoneField.getText());
				ps.setString(5, admin.getAdminId());
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
  	
  	public ObservableList<Employee> fetchEmployeeData() {
  		ObservableList<Employee> list = FXCollections.observableArrayList();
  		//Lấy danh sách tất cả bác sĩ trước
  		String sql = "SELECT d.doctor_id, "
  				+ "d.doctor_name, "
  				+ "d.doctor_email, "
  				+ "d.phone_number, "
  				+ "d.address, "
  				+ "d.experience_year, "
  				+ "d.image_id, "
  				+ "ro.role_name, "
  				+ "ro.description, "
  				+ "ro.permissions, "
  				+ "acc.account_id, "
  				+ "acc.username, "
  				+ "acc.CreationDate account_creation_date  "
  				+ "FROM doctors d JOIN accounts acc "
  				+ "ON d.account_id = acc.account_id JOIN roles ro "
  				+ "ON acc.role_id = ro.role_id";
  		//Lấy danh sách tất cả dược sĩ
  		String sql2 = "SELECT p.pharmacist_id, "
  				+ "p.pharmacist_name, "
  				+ "p.pharmacist_email, "
  				+ "p.phone_number, "
  				+ "p.address, "
  				+ "p.experience_year, "
  				+ "p.image_id, "
  				+ "ro.role_name, "
  				+ "ro.description, "
  				+ "ro.permissions, "
  				+ "acc.account_id, "
  				+ "acc.username, "
  				+ "acc.CreationDate account_creation_date "
  				+ "FROM pharmacists p JOIN accounts acc ON p.account_id = acc.account_id "
  				+ "JOIN roles ro ON acc.role_id = ro.role_id";
  		//Lấy tất cả lễ tân
  		String sql3 = "SELECT re.receptionist_id, "
  				+ "re.receptionist_name, "
  				+ "re.receptionist_email, "
  				+ "re.phone_number, "
  				+ "re.address, re.image_id, "
  				+ "ro.role_name, ro.description, "
  				+ "ro.permissions, acc.account_id, "
  				+ "acc.username, acc.CreationDate account_creation_date "
  				+ "FROM receptionists re JOIN accounts acc "
  				+ "ON re.account_id = acc.account_id JOIN roles ro "
  				+ "ON acc.role_id = ro.role_id";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps1 = con.prepareStatement(sql);
  			PreparedStatement ps2 = con.prepareStatement(sql2);
  			PreparedStatement ps3 = con.prepareStatement(sql3);
  			
  			ResultSet rs1 = ps1.executeQuery();
  			ResultSet rs2 = ps2.executeQuery();
  			ResultSet rs3 = ps3.executeQuery();
  			
  			//Lấy dữ liệu của từng nhóm người dùng
  			
  			//1. Lấy danh sách của tất cả bác sĩ
  			int orderNumber = 1;
  			while(rs1.next()) {
  				String doctorId = rs1.getString("doctor_id");
  				String doctorName = rs1.getString("doctor_name");
  				String doctorEmail = rs1.getString("doctor_email");
  				String phoneNumber = rs1.getString("phone_number");
  				String address = rs1.getString("address");
  				float experienceYear = rs1.getFloat("experience_year");
  				int imageId = rs1.getInt("image_id");
  				String roleNam = rs1.getString("role_name");
  				String description = rs1.getString("description");
  				String permission = rs1.getString("permissions");
  				Doctor doctor = new Doctor(doctorId, doctorName, doctorEmail, phoneNumber, address, experienceYear, roleNam, description, permission, imageId);
  				int accountId = rs1.getInt("account_id");
  				String username = rs1.getString("username");
  				String accountCreationDate = rs1.getString("account_creation_date");
  				Employee<Doctor> emp = new Employee<Doctor>(orderNumber++, doctorName, username, roleNam, description, permission, doctor, accountId, accountCreationDate);
  				list.add(emp);
  			}
  			
  			//2. Lấy danh sách tất cả dược sĩ
  			while(rs2.next()) {
  				String pharmacistId = rs2.getString("pharmacist_id");
  				String pharmacistName = rs2.getString("pharmacist_name");
  				String pharmacistEmail = rs2.getString("pharmacist_email");
  				String phoneNumber = rs2.getString("phone_number");
  				String address = rs2.getString("address");
  				float experienceYear = rs2.getFloat("experience_year");
  				int imageId = rs2.getInt("image_id");
  				String roleName = rs2.getString("role_name");
  				String roleDescription = rs2.getString("description");
  				String permission = rs2.getString("permissions");
  				Pharmacist phar = new Pharmacist(pharmacistId, pharmacistName, pharmacistEmail, phoneNumber, address, experienceYear, roleName, roleDescription, permission, imageId);
  				int accountId = rs2.getInt("account_id");
  				String username = rs2.getString("username");
  				String accountCreationDate = rs2.getString("account_creation_date");
  				Employee<Pharmacist> emp = new Employee<Pharmacist>(orderNumber++, pharmacistName, username, roleName, roleDescription, permission, phar, accountId, accountCreationDate);
  				list.add(emp);
  			}
  			
  			//3.Lấy danh sách tất cả lễ tân
  			while(rs3.next()) {
  				String receptionistId = rs3.getString("receptionist_id");
  				String receptionistName = rs3.getString("receptionist_name");
  				String email = rs3.getString("receptionist_email");
  				String phoneNumber = rs3.getString("phone_number");
  				String address = rs3.getString("address");
  				int imageId = rs3.getInt("image_id");
  				String roleName = rs3.getString("role_name");
  				String roleDescription = rs3.getString("description");
  				String permission = rs3.getString("permissions");
  				Receptionist rep = new Receptionist(receptionistId, receptionistName, email, phoneNumber, address, roleName, roleDescription, permission, imageId);
  				int accountId = rs3.getInt("account_id");
  				String username = rs3.getString("username");
  				String accountCreationDate = rs3.getString("account_creation_date");
  				Employee<Receptionist> emp = new Employee<Receptionist>(orderNumber++, receptionistName, username, roleName, roleDescription, permission, rep, accountId, accountCreationDate);
  				list.add(emp);
  			}
  			
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		
  		return list;
  	}
  	
  	//Hàm hiển thị danh sách nhân viên (bác sĩ, dược sĩ, lễ tân) trong phòng khám
  	public void displayEmployeeTable() {
  		orderEmployeeColumn.setCellValueFactory(cellData -> cellData.getValue().getOrderNumber().asObject());
  		employeeNameColumn.setCellValueFactory(cellData -> cellData.getValue().getEmployeeName());
  		usernameColumn.setCellValueFactory(cellData -> cellData.getValue().getUsername());
  		roleColumn.setCellValueFactory(cellData -> cellData.getValue().getRole());
  		actionColumn.setCellFactory(params -> new EmployeeButton(this));
  		
  		employeeTable.setItems(employeeList);
  	}
  	
  	//Hàm chuyển sang form tạo tài khoản người dùng
  	public void goToAccountCreationForm() {
  		//Ẩn hiện các form
  		addingAccountForm.setVisible(true);
  		addingUserForm.setVisible(false);
  		//Đặt các giá trị
  		roleLabel.setText("Chưa chọn");
  	}
  	
  	//Hàm chuyển về form tạo người dùng mới
  	public void goToUserCreationForm() {
  		//Ẩn hiện các form
  		addingAccountForm.setVisible(false);
  		addingUserForm.setVisible(true);
  		resetUserCreationForm();
  	}
  	
  	//Hàm tạo người dùng mới
  	public void addingNewUser() {
  		TextField[] otherUserList = {fullNameField, emailField, phoneField, addressField, experienceYearField};
  		TextField[] repUserList = {fullNameField, emailField, phoneField, addressField};
  		
  		if (roleChoiceBox.getValue() == null) {
  			Message.showMessage("Vui lòng chọn vai trò cho nhân viên", AlertType.ERROR);
  		} else {
  			if (!roleChoiceBox.getValue().equals(rolesString[0])) {
  				if (isEmptyField(otherUserList)) {
  		  			Message.showMessage("Vui lòng điền đầy đủ thông tin", AlertType.ERROR);
  				} else if (!isCorrectEmailSyntax(emailField.getText())) {
  		  			Message.showMessage("Vui lòng nhập đúng định dạng email", AlertType.ERROR);
  		  		} else if (!isCorrectPhoneSyntax(phoneField.getText())) {
  		  			Message.showMessage("Vui lòng nhập đúng định dạng cho số điện thoại", AlertType.ERROR);
  		  		} else if (!isNumberFormat(experienceYearField.getText())) {
  		  			Message.showMessage("Vui lòng nhập đúng định dạng năm kinh nghiệm", AlertType.ERROR);
  		  		} else if (selectedImagePath.get().equals("Chưa có ảnh")) {
  		  			Message.showMessage("Vui lòng chọn ảnh đại diện", AlertType.ERROR);
  		  		} else {
  		  			//Thực thi tác vụ tại đây
  		  			String userId = "";
  		  			int roleIndex = roleChoiceBox.getSelectionModel().getSelectedIndex()+1;
  		  			do {
  		  				userId = generateUniqueId();
  		  			} while(isExistedId(userId, roleIndex));
  		  			
  		  			String name = fullNameField.getText();
  		  			String email = emailField.getText();
  		  			String phone = phoneField.getText();
  		  			String address = addressField.getText();
  		  			float experienceYear = Float.parseFloat(experienceYearField.getText());
  		  			int imageId = ImageUtils.storeImage(absoluteImagePath);
  		  			//Thêm vào cơ sở dữ liệu
  		  			int accountId = addingUserToDatabase(roleIndex, userId, name, email, phone, experienceYear, imageId, address);
  		  			int orderNumber = employeeList.size()+1;
  		  			String selectedRole = roleChoiceBox.getValue();
  		  			String roleDescription = roleMap.get(selectedRole)[0];
  		  			String permission = roleMap.get(selectedRole)[1];
  		  			//Tạo đối tượng doctor hoặc pharmacist
  		  			Doctor doc = null;
  		  			Pharmacist phar = null;
  		  			if (roleIndex == 2) {
  		  				//Doctor
  		  				doc = new Doctor(userId, name, email, phone, address, experienceYear, selectedRole, roleDescription, permission, imageId);
  		  			} else {
  		  				//Pharmacist
  		  				phar = new Pharmacist(userId, name, email, phone, address, experienceYear, selectedRole, roleDescription, permission, imageId);
  		  			}
  		  			
  		  			Employee emp = new Employee(orderNumber, name,"Chưa có", selectedRole, roleDescription, permission, (roleIndex == 2) ? doc : phar, accountId, "");
  		  			employeeList.add(emp);
  		  			System.out.println("Đã thêm thành công người dùng");
  		  			Message.showMessage("Đã thêm người dùng thành công", AlertType.INFORMATION);
  		  			resetUserCreationForm();
  		  		}
  			} else {
  				if (isEmptyField(repUserList)) {
  		  			Message.showMessage("Vui lòng điền đầy đủ thông tin", AlertType.ERROR);
  				} else if (!isCorrectEmailSyntax(emailField.getText())) {
  		  			Message.showMessage("Vui lòng nhập đúng định dạng email", AlertType.ERROR);
  		  		} else if (!isCorrectPhoneSyntax(phoneField.getText())) {
  		  			Message.showMessage("Vui lòng nhập đúng định dạng cho số điện thoại", AlertType.ERROR);
  		  		} else if (selectedImagePath.get().equals("Chưa có ảnh")) {
  		  			Message.showMessage("Vui lòng chọn ảnh đại diện", AlertType.ERROR);
  		  		} else {
  		  			//Thực thi tác vụ tại đây
  		  		String userId = "";
		  			int roleIndex = roleChoiceBox.getSelectionModel().getSelectedIndex()+1;
		  			do {
		  				userId = generateUniqueId();
		  			} while(isExistedId(userId, roleIndex));
		  			
		  			String name = fullNameField.getText();
		  			String email = emailField.getText();
		  			String phone = phoneField.getText();
		  			String address = addressField.getText();
		  			int imageId = ImageUtils.storeImage(absoluteImagePath);
		  			//Thêm vào cơ sở dữ liệu
		  			int accountId = addingUserToDatabase(roleIndex, userId, name, email, phone, 0, imageId, address);
		  			int orderNumber = employeeList.size()+1;
		  			String selectedRole = roleChoiceBox.getValue();
		  			String roleDescription = roleMap.get(selectedRole)[0];
		  			String permission = roleMap.get(selectedRole)[1];
		  			//Tạo đối tượng doctor hoặc pharmacist
		  			Receptionist rep = new Receptionist(userId, name, email, phone, address, selectedRole, roleDescription, permission, imageId);

		  			
		  			Employee emp = new Employee(orderNumber, name,"Chưa có", selectedRole, roleDescription, permission, rep, accountId, "");
		  			employeeList.add(emp);
		  			System.out.println("Đã thêm thành công người dùng");
		  			Message.showMessage("Đã thêm người dùng thành công", AlertType.INFORMATION);
		  			resetUserCreationForm();
  		  			
  		  			System.out.println("Đã thêm thành công người dùng");
  		  		}
  			}
  		}
  	}
  	
  	//Hàm thêm người dùng lên cơ sở dữ liệu, sẽ trả về id của tài khoản tạm
  	public int addingUserToDatabase(int roleIndex, String id, String name, 
  			String email, String phone,  float experienceYear, int imageId, String address) {
  		String doctorSql = "INSERT INTO doctors VALUES(?,?,?,?,?,?,?,?)";
  		String pharmacistSql = "INSERT INTO pharmacists VALUES(?,?,?,?,?,?,?,?)";
  		String receptionistSql = "INSERT INTO receptionists VALUES(?,?,?,?,?,?,?)";
  		
  		try(Connection con = Database.connectDB()) {
  			//Nếu là nhân viên lễ tân thì thêm vào chỉ bốn trường
  			int accountId = 0;
  			if (roleIndex == 1) {
  				PreparedStatement ps = con.prepareStatement(receptionistSql);
  				accountId = generateAccountId();
  				ps.setString(1, id);
  				ps.setString(2, name);
  				ps.setString(3, email);
  				ps.setString(4, phone);
  				ps.setInt(5, accountId);
  				ps.setInt(6, imageId);
  				ps.setString(7, address);
  				//Thực hiện insert vào
  				int result = ps.executeUpdate();
  				if (result!=0) {
  					System.out.println("Đã thêm người dùng mới vào CSDL");
  				}
  			} else if (roleIndex == 2) {
  				PreparedStatement ps = con.prepareStatement(doctorSql);
  				accountId = generateAccountId();
  				ps.setString(1, id);
  				ps.setString(2, name);
  				ps.setString(3, email);
  				ps.setString(4, phone);
  				ps.setString(5, address);
  				ps.setFloat(6, experienceYear);
  				ps.setInt(7, accountId);
  				ps.setInt(8, imageId);
  				
  				//Thực thi lệnh
  				int result = ps.executeUpdate();
  				if (result!=0) {
  					System.out.println("Đã thêm người dùng mới vào CSDL");
  				}
  			} else {
  				PreparedStatement ps = con.prepareStatement(pharmacistSql);
  				accountId = generateAccountId();
  				ps.setString(1, id);
  				ps.setString(2, name);
  				ps.setString(3, email);
  				ps.setString(4, phone);
  				ps.setString(5, address);
  				ps.setFloat(6, experienceYear);
  				ps.setInt(7, accountId);
  				ps.setInt(8, imageId);
  				
  				//Thực thi lệnh
  				int result = ps.executeUpdate();
  				if (result!=0) {
  					System.out.println("Đã thêm người dùng mới vào CSDL");
  				}
  			}
  			
  			//Tạo một tài khoản giả để biểu thị cho quá trình là đang thêm vào
  			String sql = "INSERT INTO accounts VALUES(?,?,'',NOW(),?)"; 
  			PreparedStatement ps = con.prepareStatement(sql);
  			ps.setInt(1, accountId);
  			ps.setString(2, "Chưa tạo");
  			ps.setInt(3, roleIndex);
  			int result = ps.executeUpdate();
  			return accountId;
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		return -1;
  	}
  	
  	
  	//Hàm kiểm tra các trường của form không rỗng
  	public boolean isEmptyField(TextField...fields) {
  		for(TextField e: fields) {
  			if (e.getText().isEmpty()) {
  				return true;
  			}
  		}
  		return false;
  	}
  	//Hàm kiểm tra xem một chuỗi có phải là số hay không
  	public boolean isNumberFormat(String text) {
  		Pattern p = Pattern.compile("^\\d+(.\\d+)?$");
        Matcher m = p.matcher(text);
       	return m.matches();
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
  	//Hàm trả về một account id tiếp theo
  	public int generateAccountId() {
  		String sql = "SELECT account_id+1 new_account_id FROM accounts ORDER BY account_id DESC LIMIT 1";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ResultSet rs = ps.executeQuery();
  			if (rs.next()) {
  				return rs.getInt("new_account_id");
  			}
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		return 0;
  	}
  	
  	//Hàm trả về một imageId tiếp theo
  	public int generateImageId() {
  		String sql = "SELECT image_id+1 new_image_id FROM images ORDER BY image_id DESC LIMIT 1";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ResultSet rs = ps.executeQuery();
  			if (rs.next()) {
  				return rs.getInt("new_image_id");
  			}
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		return 0;
  	}
  	//Hàm upload ảnh lên database
  	public void uploadUserImage() {
  		System.out.println("Upload");
  		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image File", "*.png", "*.jpg"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile!=null) {
			Image image = new Image(selectedFile.toURI().toString());
			absoluteImagePath = selectedFile.getAbsolutePath();
			selectedImagePath.set(selectedFile.getName());
			imageUriString = selectedFile.toURI().toString();
			ImageView imageview = new ImageView(image);
			imageview.setFitWidth(20);
			imageview.setFitHeight(20);
			imageFileName.setGraphic(imageview);
		}
  	}
  	
  	//Hàm đặt lại tất cả trường trong form thêm người dùng mới
  	public void resetUserCreationForm() {
  		roleChoiceBox.getSelectionModel().select(0);
  		fullNameField.setText("");
  		emailField.setText("");
  		phoneField.setText("");
  		addressField.setText("");
  		experienceYearField.setText("");
  		imageFileName.setGraphic(null);
  		selectedImagePath.set("Chưa có ảnh");
  		imageUriString = "";
  		absoluteImagePath = "";
  	}
  	
  	//Hàm lấy id của một role name
  	public int getRoleId(String roleName) {
  		if (roleName.equals(rolesString[0])) {
  			return 1;
  		} else if (roleName.equals(rolesString[1])) {
  			return 2;
  		} else if (roleName.equals(rolesString[2])) {
  			return 3;
  		}
  		return -1;
  	}
  	
  	public boolean isExistedId(String id, int roleIndex) {
  		//1 là nhân viên lễ tân, 2 là bác sĩ, 3 là nhân viên bán thuốc
  		if (roleIndex == 1) {
  			String sql = "SELECT * FROM receptionists WHERE receptionist_id = ?";
  			try(Connection con = Database.connectDB()) {
  				PreparedStatement ps = con.prepareStatement(sql);
  				ps.setString(1, id);
  				ResultSet rs = ps.executeQuery();
  				return rs.next();
  			} catch(Exception e) {
  				e.printStackTrace();
  			}
  		} else if (roleIndex == 2) {
  			String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
  			try(Connection con = Database.connectDB()) {
  				PreparedStatement ps = con.prepareStatement(sql);
  				ps.setString(1, id);
  				ResultSet rs = ps.executeQuery();
  				return rs.next();
  			} catch(Exception e) {
  				e.printStackTrace();
  			}
  		} else {
  			String sql = "SELECT * FROM `pharmacists` WHERE pharmacist_id = ?";
  			try (Connection con = Database.connectDB()) {
  				PreparedStatement ps = con.prepareStatement(sql);
  				ps.setString(1, id);
  				ResultSet rs = ps.executeQuery();
  				return rs.next();
  			} catch(Exception e) {
  				e.printStackTrace();
  			}
  		}
  		return false;
  	}
  	
  	//Hàm lấy danh sách các role và mô tả cùng với quyền hạn
  	public Map<String, String[]> getRoleMap() {
  		Map<String, String[]> map = new HashMap<String, String[]>();
  		String sql = "SELECT * FROM roles";
  		try(Connection con = Database.connectDB()) {
  			PreparedStatement ps = con.prepareStatement(sql);
  			ResultSet rs = ps.executeQuery();
  			while(rs.next()) {
  				String roleName = rs.getString("role_name");
  				String description = rs.getString("description");
  				String permission = rs.getString("permissions");
  				String[] content = {description, permission};
  				map.put(roleName, content);
  			}
  		} catch(Exception e) {
  			e.printStackTrace();
  		}
  		return map;
  	}
}