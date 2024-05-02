package controllers;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.Admin;
import entity.AnimationUtils;
import entity.ImageUtils;
import entity.Message;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
    
    //user-defined variable
    
    private Stage currentStage;
    
    private Scene currentScene;
    
    private LocalDate currentDate;
    
    private Admin admin;
    
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
    
  //Chuyển đến tab trang cá nhân của người dùng
  	public void goToPersonalInfo() {
  		//Ẩn hiện các mục cần thiết
  		asideBarTitle.setText("Thông tin cá nhân");
  		personalContainer.setVisible(true);
  		saveInfoButton.setVisible(false);
  		prescriptionContainer.setVisible(false);
  
  		//Loại bỏ chọn tab
//  		ActiveStateUtils.removeStyleClass(medicineTab);
//  		ActiveStateUtils.removeStyleClass(prescriptionTab);
//		ActiveStateUtils.removeStyleClass(searchInvoiceTab);
//		ActiveStateUtils.removeStyleClass(staticticsTab);

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
}
