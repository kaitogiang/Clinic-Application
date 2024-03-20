package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.boot.SpringApplication;

import application.ClinicmanagementApplication;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entity.Doctor;
import entity.Message;
import entity.PasswordEncryptor;
import entity.Pharmacist;
import entity.Receptionist;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.Database;

public class LoginController implements Initializable{


	@FXML
    private FontAwesomeIconView back;

    @FXML
    private TextField emailField;

    @FXML
    private FontAwesomeIconView home;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink loginForgetPasswordLink;

    @FXML
    private AnchorPane loginForm;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private TextField loginTextField;

    @FXML
    private PasswordField newPassword;

    @FXML
    private TextField otpField;

    @FXML
    private AnchorPane otpForm;

    @FXML
    private PasswordField repeatNewPassword;

    @FXML
    private Button resetButton;

    @FXML
    private AnchorPane resetForm;

    @FXML
    private Button sendOTPButton;

    @FXML
    private Button verifyButton;

    //user-defined variable
    public static Integer otp;
	    
    public static String emailAdd;
    
    private Connection con;
    
    enum Role {
    	RECEPTIONIST,
    	DOCTOR,
    	PHARMACIST,
    	MANAGER
    }
        
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		con = Database.connectDB();
		//Thêm tab key cho phần login
		loginTextField.setOnKeyPressed(event -> handleTextFieldPressed(event, loginPasswordField));
		loginPasswordField.setOnKeyPressed(event -> handleTextFieldPressed(event, loginTextField));
		//Thêm tab key cho phần gửi OTP
		emailField.setOnKeyPressed(event -> handleTextFieldPressed(event, otpField));
		otpField.setOnKeyPressed(event -> handleTextFieldPressed(event, emailField));
		//Lúc này scene chưa được khởi tạo, nên sẽ cho nó chạy sau
		Platform.runLater(()->{
			Scene scene = loginButton.getScene();
			scene.setOnKeyPressed(event -> login());
		});
	}
    
    //Hàm thay đổi giữa các form
    public void switchForm(ActionEvent e) {
    	if (e.getTarget().equals(loginForgetPasswordLink)) {
    		loginForm.setVisible(false);
        	otpForm.setVisible(true);
        	resetForm.setVisible(false);
        	//Tạo hiệu ứng ẩn hiện cho việc chuyển trang
        	FadeTransition fade = new FadeTransition();
        	fade.setDuration(Duration.seconds(1));
        	fade.setFromValue(0);
        	fade.setToValue(10);
        	fade.setNode(otpForm);
        	fade.play();
    	}
    }
    //Hàm quay trở về form đăng nhập
    public void backToLoginForm(MouseEvent e) {
//    	e.getTarget().toString().contains("id=back")
    	if(e!=null && (e.getTarget().toString().contains("id="+back.getId()))
    		|| (e!=null && e.getTarget().toString().contains("id="+home.getId())) || e==null ) {
    		loginForm.setVisible(true);
        	otpForm.setVisible(false);
        	resetForm.setVisible(false);
        	//Tạo hiệu ứng chuyển trang về trang đang nhập
        	FadeTransition fade = new FadeTransition();
        	fade.setDuration(Duration.seconds(1));
        	fade.setFromValue(0);
        	fade.setToValue(10);
        	fade.setNode(loginForm);
        	fade.play();
    	}
    }
    //Hàm gửi mã OTP và hết hạn trong vòng 2 phút
    public void send() {
    	if (emailField.getText().isEmpty()) {
    		Message.showMessage("Vui lòng nhập địa chỉ email của bạn", AlertType.ERROR);
    	} else {
    		Random random = new Random();
        	int min = 100000;
        	int max = 999999;
        	otp = random.nextInt(max-min);
        	//Đặt thời gian hết hạn cho mã OTP
        	Timer timer = new Timer();
        	TimerTask task = new TimerTask() {
        		public void run() {
        			otp = null;
        			System.out.println("OTP has expired");
        		}
        	};
        	timer.schedule(task, 2*60*1000);
        	emailAdd = emailField.getText();
        	SpringApplication.run(ClinicmanagementApplication.class, new String [0]);
        	Message.showMessage("OTP đã gửi, vui lòng kiểm tra mail của bạn", AlertType.INFORMATION);
    	}
    }
    //Hàm xác thực mã OTP, nếu hết hạn hoặc nhập sai sẽ báo lỗi
    public void verify() {
    	if (otpField.getText().isEmpty()) {
    		Message.showMessage("Vui lòng nhập vào mã OTP", AlertType.ERROR);
    	} else {
    		Integer otp2 = Integer.parseInt(otpField.getText());
        	if (otp!=null && otp.equals(otp2)) {
        		loginForm.setVisible(false);
            	otpForm.setVisible(false);
            	resetForm.setVisible(true);
            	//Tạo hiệu ứng ẩn hiện cho việc chuyển trang
            	FadeTransition fade = new FadeTransition();
            	fade.setDuration(Duration.seconds(1));
            	fade.setFromValue(0);
            	fade.setToValue(10);
            	fade.setNode(resetForm);
            	fade.play();
        	} else {
        		Message.showMessage("Mã OTP không đúng, vui lòng thử lại",AlertType.ERROR);
        	}
    	}
    }
 
    public void login() {
    	String username = loginTextField.getText();
    	String password = loginPasswordField.getText();
    	if (username.isEmpty() || password.isEmpty()) {
    		Message.showMessage("Vui lòng nhập đầy đủ thông tin",AlertType.ERROR);
    	} else if (!verifyAccount(username, password)) {
    		Message.showMessage("Tài khoản không tồn tại", AlertType.ERROR);
    	} else {
    		Role roles = getUsernamePosition(username);
    		if (roles == Role.RECEPTIONIST) {
        		loadStage("/views/ReceptionistMainScreen.fxml",roles, username, "Receptionist");
    		} else if (roles == Role.DOCTOR) {
    			loadStage("/views/DoctorMainScreen.fxml", roles, username, "Doctor");
    		} else if (roles == Role.PHARMACIST) {
    			loadStage("/views/PharmacistMainScreen.fxml", roles, username, "Pharmacist");
    			System.out.println("Nhan vien ban thuoc");
    		}
    	}
    }
    
    void loadStage(String fxmlFilePath,Role roles, String username, String title) {
    	//Nạp giao diện của nhân viên lễ tân    	
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
		String css = this.getClass().getResource("/css/style.css").toExternalForm();
		try {
			AnchorPane mainReceptionist = loader.load(); //Nạp file fxml vào AnchorPane
			//Lấy controller của ReceptionistMainScreen.fxml
			Stage currentStage = (Stage) loginButton.getScene().getWindow();
			Scene scene = new Scene(mainReceptionist);
			scene.getStylesheets().add(css);
			if (roles == Role.RECEPTIONIST) {
				ReceptionistController controller = loader.getController();
				scene.setUserData(getUserInfo(username)); //Truyền dữ liệu vào Scene để khi sang ReceptionistMainScreen thì có thể lấy dữ liệu
				controller.setCurrentStage(currentStage);
				controller.setMyScene(scene);
				
			} else if (roles == Role.DOCTOR) {
				DoctorController controller = loader.getController();
				scene.setUserData(getUserInfo(username));
				controller.setCurrentStage(currentStage);
				controller.setMyScene(scene);
				currentStage.setOnCloseRequest(e->{
//					controller.stopExecutorService();
					controller.stopBackgroundService();
				});
			} else if (roles == Role.PHARMACIST) {
				PharmacistController controller = loader.getController();
				scene.setUserData(getUserInfo(username));
				controller.setCurrentStage(currentStage);
				controller.setMyScene(scene);
//				currentStage.setOnCloseRequest(e->{
//					controller.stopBackgroundService();
//				});
			}
			currentStage.setMaximized(false);
			currentStage.setTitle(title);
			currentStage.setUserData("From login");
			//Thêm hiệu ứng chuyển đổi giữa hai scene
			FadeTransition transition = createFadeTransition(scene,10.0,0.0);
			transition.setOnFinished(e->{
				currentStage.setScene(scene);
				currentStage.setMaximized(true);
				FadeTransition trans2 = createFadeTransition(scene, 0.0, 10.0);
    			trans2.play();
			});
			transition.play();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		Message.showMessage("Đăng nhập thành công", AlertType.INFORMATION);
    }
    //Hàm kiểm tra vai trò của một username
    public Role getUsernamePosition(String username) {
    	String sql = "SELECT r.role_id FROM accounts acc JOIN roles r ON acc.role_id = r.role_id WHERE username = ?";
    	try(Connection con = Database.connectDB()) {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1, username);
    		ResultSet rs = ps.executeQuery();
    		if (rs.next()) {
    			switch(rs.getInt(1)) {
    			case 1: return Role.RECEPTIONIST;
    			case 2: return Role.DOCTOR;
    			case 3: return Role.PHARMACIST;
    			case 4: return Role.MANAGER;
    			}
    		}
    	} catch (Exception e) {
			System.out.println(e);
		}
    	return null;
    }
    FadeTransition createFadeTransition(Scene scene, Double fromValue, Double toValue) {
    	FadeTransition fade = new FadeTransition();
    	fade.setNode(scene.getRoot());
    	fade.setDuration(Duration.seconds(1));
		fade.setFromValue(fromValue);
		fade.setToValue(toValue);
    	return fade;
    }
    public boolean verifyAccount(String username, String password) {
    	String sql = "SELECT password FROM accounts WHERE username = ?";
    	try {
//        	Connection con = Database.connectDB();
        	PreparedStatement ps = con.prepareStatement(sql);
        	ps.setString(1, username);
        	ResultSet rs = ps.executeQuery();
        	if (rs.next()) {
        		String hashedPassword = rs.getString(1);
        		return PasswordEncryptor.checkPassword(password, hashedPassword);
        	}
    	} catch(Exception e) {
    		System.out.println(e);
    	}
    	return false;
    }
    public void resetPassword() {
    	String newPassword = this.newPassword.getText();
    	String repeatNewPassword = this.repeatNewPassword.getText();
    	if (newPassword.isEmpty() || repeatNewPassword.isEmpty()) {
    		Message.showMessage("Vui lòng nhập đầy đủ thông tin", AlertType.ERROR);
    	} else if (!newPassword.equals(repeatNewPassword)) {
    		Message.showMessage("Mật khẩu không khớp", AlertType.ERROR);
    	} else {
    		Message.showMessage("Đổi mật khẩu thành công", AlertType.INFORMATION);
    		backToLoginForm(null);
    	}
    }
    

    public Object getUserInfo(String username) {
    	Role roles = getUsernamePosition(username);
    	String sql = "";
    	System.out.println(roles);
    	//Nếu nhân viên lễ tân đăng nhập thì lấy thông tin của nhân viên lễ tân
    	//Nếu bác sĩ đăng nhập thì lấy thông tin bác sĩ
    	if (roles == Role.RECEPTIONIST) {
    		sql = "SELECT rep.receptionist_id,"
        			+ "rep.receptionist_name,"
        			+ "rep.receptionist_email,"
        			+ "rep.phone_number, "
        			+ "rep.address, "
        			+ "ro.role_name, "
        			+ "ro.description, "
        			+ "ro.permissions, "
        			+ "rep.image_id "
        			+ "FROM accounts acc JOIN receptionists rep "
        			+ "ON acc.account_id = rep.account_id "
        			+ "JOIN roles ro ON acc.role_id = ro.role_id "
        			+ "WHERE username = ?";
    	} else if (roles == Role.DOCTOR) {
    		sql = "SELECT doc.doctor_id, "
    				+ "doctor_name, "
    				+ "doctor_email, "
    				+ "doc.phone_number, "
    				+ "doc.address, "
    				+ "doc.experience_year, "
    				+ "ro.role_name, "
    				+ "ro.description, "
    				+ "ro.permissions, "
    				+ "doc.image_id "
    				+ "FROM accounts acc JOIN doctors doc ON "
    				+ "acc.account_id = doc.account_id "
    				+ "JOIN roles ro ON acc.role_id = ro.role_id "
    				+ "WHERE username = ?";
    	} else if (roles == Role.PHARMACIST) {
    		sql = "SELECT pharmacist_id, "
    			+ "pharmacist_name, "
    			+ "pharmacist_email, "
    			+ "phone_number, "
    			+ "address, "
    			+ "experience_year, "
    			+ "ro.role_name, "
    			+ "ro.description, "
    			+ "ro.permissions, "
    			+ "pha.image_id FROM accounts acc "
    			+ "JOIN pharmacists pha ON acc.account_id = pha.account_id "
    			+ "JOIN roles ro ON acc.role_id = ro.role_id "
    			+ "WHERE username = ?";
    	}
    	String id = "";
		String name = "";
		String email = "";
		String phone = "";
		String address = "";
		float experienceYear = 0.0f;
		String role = "";
		String description = "";
		String permission = "";
		int imageId = 0;
    	try {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1, username);
    		ResultSet rs = ps.executeQuery();
    		if (rs.next()) {
    			if (roles == Role.RECEPTIONIST) {
    				id = rs.getString(1);
        			name = rs.getString(2);
        			email = rs.getString(3);
        			phone = rs.getString(4);
        			address = rs.getString(5);
        			role = rs.getString(6);
        			description = rs.getString(7);
        			permission = rs.getString(8);
        			imageId = rs.getInt(9);
    			} else if (roles == Role.DOCTOR) {
    				id = rs.getString(1);
    				name = rs.getString(2);
    				email = rs.getString(3);
    				phone = rs.getString(4);
    				address = rs.getString(5);
    				experienceYear = rs.getFloat(6);
    				role = rs.getString(7);
    				description = rs.getString(8);
    				permission = rs.getString(9);
    				imageId = rs.getInt(10);
    			} else if (roles == Role.PHARMACIST) {
    				id = rs.getString(1);
    				name = rs.getString(2);
    				email = rs.getString(3);
    				phone = rs.getString(4);
    				address = rs.getString(5);
    				experienceYear = rs.getFloat(6);
    				role = rs.getString(7);
    				description = rs.getString(8);
    				permission = rs.getString(9);
    				imageId = rs.getInt(10);
    			}
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	if (roles == Role.RECEPTIONIST) {
        	return new Receptionist(id, name, email, phone, address, role, description, permission, imageId);
    	} else if (roles == Role.DOCTOR) {
    		return new Doctor(id, name, email, phone, address, experienceYear, role, description, permission, imageId);
    	} else if (roles == Role.PHARMACIST) {
    		return new Pharmacist(id, name, email, phone, address, experienceYear, role, description, permission, imageId);
    	}
    	return null;
    }
    //Hàm thêm nút tab để chuyển đổi giữa các text Field trong login
    public void handleTextFieldPressed(KeyEvent event, TextField nextTextField) {
    	if (event.getCode().equals(KeyCode.TAB)) {
    		nextTextField.requestFocus(); //Tập trung vào textField kế tiếp
    		event.consume();
    	}
    }
   
}
