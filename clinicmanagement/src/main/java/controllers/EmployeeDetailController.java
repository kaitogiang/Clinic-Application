package controllers;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import entity.Doctor;
import entity.Employee;
import entity.ImageUtils;
import entity.Message;
import entity.PasswordEncryptor;
import entity.Pharmacist;
import entity.Receptionist;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import services.Database;

public class EmployeeDetailController implements Initializable{

	@FXML
    private Label accountCreationDateLabel;

    @FXML
    private HBox actionContainer;

    @FXML
    private TextArea addressField;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Button changePasswordButton;

    @FXML
    private TextField emailField;

    @FXML
    private Label employeeAddress;

    @FXML
    private Label employeeEmail;

    @FXML
    private Label employeeExperienceYear;

    @FXML
    private Label employeeName;

    @FXML
    private Label employeePhone;

    @FXML
    private Label employeePosition;

    @FXML
    private TextField experienceYearField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField newPasswordField;

    @FXML
    private PasswordField newPasswordHideField;
    
    @FXML
    private HBox passwordAlternationContainer;

    @FXML
    private VBox personalInformationContainer;

    @FXML
    private VBox EditablePersonalInformationContainer;

    @FXML
    private TextField phoneField;

    @FXML
    private Label roleDescription;

    @FXML
    private TextArea roleDescriptionField;

//    @FXML
//    private TextField roleField;

    @FXML
    private VBox roleInformationContainer;

    @FXML
    private VBox editableRoleInformationContainer;

    @FXML
    private TextArea rolePermissionField;
    
    @FXML
    private Label rolePermission;

    @FXML
    private Button saveButton;

    @FXML
    private Button uploadImageBtn;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameField;
    
    @FXML
    private VBox accountContainer;
    
    @FXML
    private HBox roleNameRadio;
    
    @FXML
    private RadioButton receptionistRadioButton;
    
    @FXML
    private RadioButton doctorRadioButton;
    
    @FXML
    private RadioButton pharmacistRadioButton;
    
    //user-defined variable
    private Employee emp; //Tham chiếu tới employee
    
    private String employeeId;
    
    private int imageId; 
    
    enum RoleName{
    	RECEPTIONIST,
    	DOCTOR,
    	PHARMACIST
    }
    
    private RoleName currentRole;
    
    private String imagePath = null; //Đường dẫn khi admin chọn ảnh mới
    
    private boolean isRegisteredAccount = true; //Hàm kiểm tra người dùng có tài khoản chưa
    
    private SimpleStringProperty passwordProperty = new SimpleStringProperty("");
    
    boolean isShowPassword = false;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		receptionistRadioButton.setMouseTransparent(true);
		doctorRadioButton.setMouseTransparent(true);
		pharmacistRadioButton.setMouseTransparent(true);

		//Liên kết password
		newPasswordField.textProperty().bindBidirectional(passwordProperty);
		newPasswordHideField.textProperty().bindBidirectional(passwordProperty);
	}
		
	//True: mở giao diện chỉnh sửa, false: mở giao diện chỉ xem
	public void setData(Employee employee, boolean isEditable) {
		if (!isEditable) {
			System.out.println("Gọi scene chi tiết");
			showDetailScreen();
			emp = employee;
			//Lấy đối tượng chi tiết
			Object data = employee.getData();
			if (data instanceof Receptionist) {
				Receptionist rep = (Receptionist) data;
				//Đặt avatar cho lễ tân
				Image userImage = ImageUtils.retrieveImage(rep.getImageId());
				avatarImageView.setImage(userImage);
				//Đặt thông tin cá nhân
				employeeName.setText(rep.getReceptionistName());
				employeeEmail.setText(rep.getReceptionistEmail());
				employeePhone.setText(rep.getPhone_number());
				employeeAddress.setText(rep.getAdress());
				//Đặt thông tin vai trò
				employeePosition.setText(rep.getRole());
				employeeExperienceYear.setText("Không có");
				roleDescription.setText(rep.getRoleDescription());
				rolePermission.setText(rep.getPermissions());
				
			} else if (data instanceof Doctor) {
				Doctor doc = (Doctor) data;
				
				//Đặt avatar cho lễ tân
				Image userImage = ImageUtils.retrieveImage(doc.getImageId());
				avatarImageView.setImage(userImage);
				//Đặt thông tin cá nhân
				employeeName.setText(doc.getDoctorName());
				employeeEmail.setText(doc.getDoctorEmail());
				employeePhone.setText(doc.getPhoneNumber());
				employeeAddress.setText(doc.getAddress());
				//Đặt thông tin vai trò
				employeePosition.setText(doc.getRole());
				employeeExperienceYear.setText(String.valueOf(doc.getExperienceYear()));
				roleDescription.setText(doc.getRoleDescription());
				rolePermission.setText(doc.getPermissions());
				
			} else {
				Pharmacist phar = (Pharmacist) data;
				//Đặt avatar cho lễ tân
				Image userImage = ImageUtils.retrieveImage(phar.getImageId());
				avatarImageView.setImage(userImage);
				//Đặt thông tin cá nhân
				employeeName.setText(phar.getPharmacistName());
				employeeEmail.setText(phar.getPharmacistEmail());
				employeePhone.setText(phar.getPhoneNumber());
				employeeAddress.setText(phar.getAddress());
				//Đặt thông tin vai trò
				employeePosition.setText(phar.getRole());
				employeeExperienceYear.setText(String.valueOf(phar.getExperienceYear()));
				roleDescription.setText(phar.getRoleDescription());
				rolePermission.setText(phar.getPermissions());
			}
			
			//Thiết lập avatar
			//Bo góc cho avatar
			Rectangle clip = new Rectangle(240, 240);
	  		clip.setArcWidth(20);
	  		clip.setArcHeight(20);
	  		avatarImageView.setClip(clip);
			//Thiết lập thông tin tài khoản
			int accountId = employee.getAccountId();
			List<String> accountInfo = getAccountInfo(accountId);
			usernameLabel.setText(accountInfo.get(0));
			accountCreationDateLabel.setText(accountInfo.get(1));
		} else {
			//Thêm trường nhập tên đăng nhập trong trường hợp là người đó có tài khoản
			usernameField = new TextField();
			usernameField.setFont(new Font(17));
			//Lấy thông tin tài khoản để kiểm tra xem người này có tài khoản chưa
			int accountId = employee.getAccountId();
			List<String> accountInfo = getAccountInfo(accountId);
			//Thiết lập thông tin tài khoản
			if (accountInfo.get(0).equals("Chưa tạo")) {
				usernameLabel.setVisible(true);
				usernameLabel.setText(accountInfo.get(0));
				changePasswordButton.setVisible(false);
				isRegisteredAccount = false;
			} else {
				usernameLabel.setVisible(false);
				accountContainer.getChildren().add(1, usernameField);
				usernameField.setText(accountInfo.get(0));
				changePasswordButton.setVisible(true);
			}
			accountCreationDateLabel.setText(accountInfo.get(1));
			
			showEdtiScreen();
			emp = employee;
			//Lấy đối tượng chi tiết
			Object data = employee.getData();
			if (data instanceof Receptionist) {
				//Lưu trữ vai trò hiện tại
				currentRole = RoleName.RECEPTIONIST;
				Receptionist rep = (Receptionist) data;
				employeeId = rep.getReceptionistId();
				imageId = rep.getImageId();
				//Đặt avatar cho lễ tân
				Image userImage = ImageUtils.retrieveImage(rep.getImageId());
				avatarImageView.setImage(userImage);
				//Đặt thông tin cá nhân
				nameField.setText(rep.getReceptionistName());
				emailField.setText(rep.getReceptionistEmail());
				phoneField.setText(rep.getPhone_number());
				addressField.setText(rep.getAdress());
				//Đặt thông tin vai trò
				receptionistRadioButton.setSelected(true);
				experienceYearField.setText("Không có");
				roleDescriptionField.setText(rep.getRoleDescription());
				rolePermissionField.setText(rep.getPermissions());
				
			} else if (data instanceof Doctor) {
				Doctor doc = (Doctor) data;
				employeeId = doc.getDoctorId();
				imageId = doc.getImageId();
				//Lưu trữ vai trò hiện tại
				currentRole = RoleName.DOCTOR;
				//Đặt avatar cho lễ tân
				Image userImage = ImageUtils.retrieveImage(doc.getImageId());
				avatarImageView.setImage(userImage);
				//Đặt thông tin cá nhân
				nameField.setText(doc.getDoctorName());
				emailField.setText(doc.getDoctorEmail());
				phoneField.setText(doc.getPhoneNumber());
				addressField.setText(doc.getAddress());
				//Đặt thông tin vai trò
				doctorRadioButton.setSelected(true);
				experienceYearField.setText(String.valueOf(doc.getExperienceYear()));
				roleDescriptionField.setText(doc.getRoleDescription());
				rolePermissionField.setText(doc.getPermissions());
				
			} else {
				Pharmacist phar = (Pharmacist) data;
				employeeId = phar.getPharmacistId();
				imageId = phar.getImageId();
				//Lưu trữ vai trò hiện tại
				currentRole = RoleName.PHARMACIST;
				//Đặt avatar cho lễ tân
				Image userImage = ImageUtils.retrieveImage(phar.getImageId());
				avatarImageView.setImage(userImage);
				//Đặt thông tin cá nhân
				nameField.setText(phar.getPharmacistName());
				emailField.setText(phar.getPharmacistEmail());
				phoneField.setText(phar.getPhoneNumber());
				addressField.setText(phar.getAddress());
				//Đặt thông tin vai trò
				pharmacistRadioButton.setSelected(true);
				experienceYearField.setText(String.valueOf(phar.getExperienceYear()));
				roleDescriptionField.setText(phar.getRoleDescription());
				rolePermissionField.setText(phar.getPermissions());
			}
			
			//Thiết lập avatar
			//Bo góc cho avatar
			Rectangle clip = new Rectangle(240, 240);
	  		clip.setArcWidth(20);
	  		clip.setArcHeight(20);
	  		avatarImageView.setClip(clip);
		}
	}
	
	
	public void showDetailScreen() {
		//Hiện
		personalInformationContainer.setVisible(true);
		roleInformationContainer.setVisible(true);
		//Ẩn
		uploadImageBtn.setVisible(false);
		EditablePersonalInformationContainer.setVisible(false);
		changePasswordButton.setVisible(false);
		passwordAlternationContainer.setVisible(false);
		editableRoleInformationContainer.setVisible(false);
		saveButton.setVisible(false);
	}
	
	public void showEdtiScreen() {
		//ẩn
		personalInformationContainer.setVisible(false);
		roleInformationContainer.setVisible(false);
		passwordAlternationContainer.setVisible(false);
		//Hiện
		uploadImageBtn.setVisible(true);
		EditablePersonalInformationContainer.setVisible(true);
		editableRoleInformationContainer.setVisible(true);
		saveButton.setVisible(true);
	}
	
	public List<String> getAccountInfo(int accountId) {
		List<String> list = new ArrayList<>();
		String sql = "SELECT account_id, username, "
				+ "CASE WHEN password = '' "
				+ "THEN 'Chưa tạo' ELSE CreationDate "
				+ "END creation_date FROM accounts WHERE account_id = ?";
		try(Connection con = Database.connectDB()) {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, accountId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				list.add(rs.getString("username"));
				list.add(rs.getString("creation_date"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
    
	public void close() {
		Stage stage = (Stage)nameField.getScene().getWindow();
		stage.close();
	}
	//Hàm hiển thị form nhập mật khẩu mới
	public void showPasswordInput() {
		passwordAlternationContainer.setVisible(true);
		newPasswordField.setVisible(false);
		newPasswordHideField.setVisible(true);
	}
	//Xác thực mật khẩu hợp lệ
	public boolean isValidPassword(String password) {
		if (password.length()<8) {
			return false;
		} else return true;
	}
	
	//Hàm lưu những thay đổi lên cơ sở dữ liệu
	public void save() {
		String sql = "";
//		boolean isOk = true;
		PreparedStatement ps = null;
		try(Connection con = Database.connectDB()) {
            con.setAutoCommit(false);
			if (currentRole == RoleName.RECEPTIONIST) {
				//Lễ tân
				sql = "UPDATE receptionists SET receptionist_name = ?, receptionist_email = ?, "
						+ "phone_number = ?, address = ? WHERE receptionist_id = ?";
				ps = con.prepareStatement(sql);
				ps.setString(1, nameField.getText());
				ps.setString(2, emailField.getText());
				ps.setString(3, phoneField.getText());
				ps.setString(4, addressField.getText());
				ps.setString(5, employeeId);
				
				//Thực hiện lệnh
				int update = ps.executeUpdate();
				if (update != 0) {
					System.out.println("Cập nhật lễ tân");
				}
				
				//Thay đổi dữ liệu để hiển thị trên bảng của danh sách tài khoản
//				Receptionist rep = (Receptionist) emp.getData();
//				emp.setEmployeeName(nameField.getText());
				} else if (currentRole == RoleName.DOCTOR) {
				//Bác sĩ
					sql = "UPDATE doctors SET doctor_name = ?, 	doctor_email = ?, "
							+ "phone_number = ?, address = ?, experience_year = ? WHERE doctor_id = ?";
					ps = con.prepareStatement(sql);
					ps.setString(1, nameField.getText());
					ps.setString(2, emailField.getText());
					ps.setString(3, phoneField.getText());
					ps.setString(4, addressField.getText());
					ps.setFloat(5, Float.parseFloat(experienceYearField.getText()));
					ps.setString(6, employeeId);
					
					//Thực thi lệnh
					int update = ps.executeUpdate();
					if (update != 0) {
						System.out.println("Cập nhật bác sĩ");
					}
				} else {
					System.out.println("Update duoc si");
				//Dược sĩ
					sql = "UPDATE pharmacists SET pharmacist_name = ?, pharmacist_email = ?, "
							+ "phone_number = ?, address = ?, experience_year = ? WHERE pharmacist_id = ?";
					ps = con.prepareStatement(sql);
					ps.setString(1, nameField.getText());
					ps.setString(2, emailField.getText());
					ps.setString(3, phoneField.getText());
					ps.setString(4, addressField.getText());
					ps.setFloat(5, Float.parseFloat(experienceYearField.getText()));
					ps.setString(6, employeeId);
					
					//Thực thi lệnh
					int update = ps.executeUpdate();
					if (update!=0) {
						System.out.println("Cập nhật dược sĩ");
					}
				}
			//Lệnh cập nhật password nếu nó được nhập vào
			if (!newPasswordField.getText().isEmpty()) {
				if (!isValidPassword(newPasswordField.getText())) {
					Message.showMessage("Mật khẩu ít nhất 8 ký tự", AlertType.ERROR);
					return;
				} else {
					String acSql = "UPDATE accounts SET password = ?, CreationDate = NOW() WHERE account_id = ?";
					String encryptPassword = PasswordEncryptor.hashPassword(newPasswordField.getText());
					ps = con.prepareStatement(acSql);
					ps.setString(1, encryptPassword);
					ps.setInt(2, emp.getAccountId());
					
					//Thực thi lệnh
					int update = ps.executeUpdate();
					if (update!=0) {
						System.out.println("Cập nhật mật khẩu người đã có tài khoản");
					}
				}
			}
			//Lệnh cập nhật username
			if (isRegisteredAccount) {
				if (usernameField.getText().isEmpty()) {
					Message.showMessage("Vui lòng nhập tên đăng nhập", AlertType.ERROR);
					return;
				} else {
					String acSql = "UPDATE accounts SET username = ?, CreationDate = NOW() WHERE account_id = ?";
					ps = con.prepareStatement(acSql);
					ps.setString(1, usernameField.getText());
					ps.setInt(2, emp.getAccountId());
					
					//Thực thi lệnh
					int update = ps.executeUpdate();
					if (update!=0) {
						System.out.println("Cập nhật username cho người có tài khoản");
					}
				}
			}
			
			
			//Lệnh cập nhật ảnh đại diện
			if (imagePath!=null) {
				ImageUtils.updateImage(imagePath, imageId);
			}
		
			//Cập nhật đối tượng employee để truyền lại cho stage cha
			emp.setEmployeeName(nameField.getText());
			if (isRegisteredAccount) {
				emp.setUsername(usernameField.getText());
			}
			if (currentRole == RoleName.RECEPTIONIST) {
				Receptionist rep = (Receptionist) emp.getData();
				rep.setReceptionistName(nameField.getText());
				rep.setReceptionistEmail(emailField.getText());
				rep.setPhone_number(phoneField.getText());
				rep.setAddress(addressField.getText());
				
			} else if (currentRole == RoleName.DOCTOR) {
				Doctor doc = (Doctor) emp.getData();
				doc.setDoctorName(nameField.getText());
				doc.setDoctorEmail(emailField.getText());
				doc.setPhoneNumber(phoneField.getText());
				doc.setAddress(addressField.getText());
				doc.setExperienceYear(Float.parseFloat(experienceYearField.getText()));
			} else if (currentRole == RoleName.PHARMACIST) {
				Pharmacist phar = (Pharmacist) emp.getData();
				phar.setPharmacistName(nameField.getText());
				phar.setPharmacistEmail(emailField.getText());
				phar.setPhoneNumber(phoneField.getText());
				phar.setAddress(addressField.getText());
				phar.setExperienceYear(Float.parseFloat(experienceYearField.getText()));
			}
			
			Message.showMessage("Cập nhật thông tin thành công", AlertType.INFORMATION);
			con.commit();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Hàm lấy đường dẫn tuyệt đối file ảnh, nếu null là người dùng chưa chọn
	public void uploadImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image File", "*.png", "*.jpg"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile!=null) {
			Image image = new Image(selectedFile.toURI().toString());
			avatarImageView.setImage(image);
			 imagePath = selectedFile.getAbsolutePath();
		}
	}
	
	//Hàm hiển thị password
	public void showPassword() {
		isShowPassword = !isShowPassword;
		if (isShowPassword) {
			newPasswordHideField.setVisible(false);
			newPasswordField.setVisible(true);
		} else {
			newPasswordHideField.setVisible(true);
			newPasswordField.setVisible(false);
		}
		
		System.out.println("Gia tri trong TextField: "+newPasswordField.getText());
		System.out.println("Gia tri trong PassswordField "+newPasswordHideField.getText());
	}
}
