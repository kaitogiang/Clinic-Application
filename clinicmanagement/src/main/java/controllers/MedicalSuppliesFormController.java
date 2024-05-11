package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

import entity.MedicalSupplies;
import entity.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.Database;

public class MedicalSuppliesFormController {

	 @FXML
	    private Label goodQuantity;

	    @FXML
	    private VBox medicalSuppliesFormContainer;

	    @FXML
	    private VBox medicalSuppliesLabelContainer;

	    @FXML
	    private TextField medicalSuppliesNameField;

	    @FXML
	    private Label medicalSuppliesNameLabel;

	    @FXML
	    private TextField medicalSuppliesPriceField;

	    @FXML
	    private Label medicalSuppliesPriceLabel;

	    @FXML
	    private TextField medicalSuppliesQuantityField;

	    @FXML
	    private Label medicalSuppliesQuantityLabel;

	    @FXML
	    private Label medicalSuppliesTitle;

	    @FXML
	    private Label medicalSuppliesTotalLabel;

	    @FXML
	    private Label spoiledQuantity;
	    
	    @FXML
	    private Button addButton;

	    @FXML
	    private TextField medicalSuppliesSpoiled;

	    @FXML
	    private VBox spoiledContainer;
	    
	    //User-defined variable
	    private AdminController parentController;
	    
	    Consumer<MedicalSupplies> callback;
	    
	    public void setData(MedicalSupplies m, AdminController parentController, boolean isEdited) {
	    	if (m!=null) {
	    		//Hiện form edit
	    		if (isEdited) {
	    			this.parentController = parentController;
	    			medicalSuppliesTitle.setText("Cập nhật vật tư y tế");
		    		medicalSuppliesFormContainer.setVisible(true);
		    		medicalSuppliesLabelContainer.setVisible(false);
		    		addButton.setVisible(true);
		    		addButton.setText("Cập nhật vật tư");
		    		spoiledContainer.setVisible(true);
		    		medicalSuppliesNameField.setText(m.getNameValue());
		    		medicalSuppliesQuantityField.setText(String.valueOf(m.getQuantityValue()));
		    		medicalSuppliesPriceField.setText(String.valueOf(m.getPriceValue()));
		    		medicalSuppliesSpoiled.setText(String.valueOf(m.getSpoiledQuantityValue()));
		    		
		    		addButton.setOnAction(e->{
		    			updateMedicalSupplies(m);
		    		});
		    		return;
	    		}
	    		
	    		//Hiển thị form xem
	    		medicalSuppliesTitle.setText("Chi tiết vật tư");
	    		medicalSuppliesLabelContainer.setVisible(true);
	    		medicalSuppliesFormContainer.setVisible(false);
	    		medicalSuppliesNameLabel.setText(m.getNameValue());
	    		medicalSuppliesQuantityLabel.setText(String.valueOf(m.getQuantityValue()));
	    		medicalSuppliesPriceLabel.setText(m.getPriceValue()+" VNĐ");
	    		medicalSuppliesTotalLabel.setText(m.getTotalValue()+" VNĐ");
	    		goodQuantity.setText(String.valueOf(m.getQuantityValue() - m.getSpoiledQuantityValue()));
	    		spoiledQuantity.setText(String.valueOf(m.getSpoiledQuantityValue()));
	    		addButton.setVisible(false);
	    		spoiledContainer.setVisible(false);
	    	} else {
	    		this.parentController = parentController;
	    		callback = parentController::addMedicalSuppliesElement;
	    		//Hiển thị form nhập 
	    		medicalSuppliesTitle.setText("Thêm vật tư y tế mới");
	    		medicalSuppliesFormContainer.setVisible(true);
	    		medicalSuppliesLabelContainer.setVisible(false);
	    		addButton.setVisible(true);
	    		spoiledContainer.setVisible(false);
	    	}
	    }
	    
	    public void addMedicalSupplies() {
	    	String medicalSuppliesName = "";
	    	int quantity = 0;
	    	float price = 0;
	    	
	    	if (medicalSuppliesNameField.getText().isEmpty() 
	    			|| medicalSuppliesQuantityField.getText().isEmpty()
	    		|| medicalSuppliesPriceField.getText().isEmpty()	) {
	    		Message.showMessage("Vui lòng nhập đầy đủ thông tin", AlertType.ERROR);
	    	} else if (!parentController.isNumberFormat(medicalSuppliesQuantityField.getText())
	    		|| !parentController.isNumberFormat(medicalSuppliesPriceField.getText())	) {
	    		Message.showMessage("Vui lòng nhập đúng định dạng số", AlertType.ERROR);
	    	} else {
	    		//Lấy dữ liệu
	    		medicalSuppliesName = medicalSuppliesNameField.getText();
	    		quantity = Integer.parseInt(medicalSuppliesQuantityField.getText());
	    		price = Float.parseFloat(medicalSuppliesPriceField.getText());
	    		int spoiledQuantity = 0;
	    		float total = price * quantity;
	    		int order = 0;
	    		String id = "";
	    		do {
	    			id = parentController.generateUniqueId();
	    		} while(isExistedMedicalSuppliesId(id));
	    		
	    		MedicalSupplies mc = new MedicalSupplies(order, id, medicalSuppliesName, quantity, spoiledQuantity, price, total);
	    		callback.accept(mc);
	    		addMedicalSuppliesToDatabase(mc);
	    		Message.showMessage("Thêm vật tư y tế thành công", AlertType.INFORMATION);
	    		resetAllField();
	    	}
	    }
	    
	    public void addMedicalSuppliesToDatabase(MedicalSupplies mc) {
	    	String sql = "INSERT INTO medical_supplies VALUES(?,?,?,?,?,?,?)";
	    	try(Connection con = Database.connectDB()) {
	    		PreparedStatement ps = con.prepareStatement(sql);
	    		ps.setString(1, mc.getIdValue());
	    		ps.setString(2, mc.getNameValue());
	    		ps.setInt(3, mc.getQuantityValue());
	    		ps.setFloat(4, mc.getPriceValue());
	    		ps.setFloat(5, mc.getTotalValue());
	    		ps.setInt(6, mc.getSpoiledQuantityValue());
	    		ps.setString(7, parentController.getAmdinId());
	    		int update = ps.executeUpdate();
	    		if (update!=0) System.out.println("Thêm vật tư thành công");
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    public void updateMedicalSupplies(MedicalSupplies mc) {
	    	String name = medicalSuppliesNameField.getText();
	    	String quantity = medicalSuppliesQuantityField.getText();
	    	String unitPrice = medicalSuppliesPriceField.getText();
	    	String spoiled = medicalSuppliesSpoiled.getText();
	    	
	    	if (name.isEmpty() || quantity.isEmpty() || unitPrice.isEmpty() || spoiled.isEmpty()) {
	    		Message.showMessage("Vui lòng nhập đầy đủ thông tin", AlertType.ERROR);
	    		return;
	    	} else if (!parentController.isNumberFormat(quantity) 
	    			|| !parentController.isNumberFormat(unitPrice)
	    			|| !parentController.isNumberFormat(spoiled)) {
	    		Message.showMessage("Vui lòng nhập đúng định dạng số", AlertType.ERROR);
	    		return;
	    	}
	    	
	    	String sql = "UPDATE medical_supplies SET supplies_name = ?, "
	    			+ "quantity = ?, unit_price = ?, "
	    			+ "total_price = quantity * unit_price, spoiled_quantity = ? "
	    			+ "WHERE supplies_id = ?";
	    	try(Connection con = Database.connectDB()) {
	    		PreparedStatement ps = con.prepareStatement(sql);
	    		ps.setString(1, medicalSuppliesNameField.getText());
	    		ps.setInt(2, Integer.valueOf(medicalSuppliesQuantityField.getText()));
	    		ps.setFloat(3, Float.valueOf(medicalSuppliesPriceField.getText()));
	    		ps.setInt(4, Integer.valueOf(medicalSuppliesSpoiled.getText()));
	    		ps.setString(5, mc.getIdValue());
	    		int update = ps.executeUpdate();
	    		if (update!=0) System.out.println("Thêm vật tư thành công");
	    		//Cập nhật lại đối tượng
	    		float total = Integer.parseInt(quantity) * Float.parseFloat(unitPrice);
	    		mc.setAllValue(name, Integer.parseInt(quantity), Integer.parseInt(spoiled), Float.parseFloat(unitPrice), total);
    			Message.showMessage("Cập nhật vật tư thành công", AlertType.INFORMATION);
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    //Hàm kiểm tra trùng Id
	    public boolean isExistedMedicalSuppliesId(String id) {
	    	String sql = "SELECT * FROM medical_supplies WHERE supplies_id = ?";
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
	    
	    public void close() {
	    	Stage stage = (Stage) medicalSuppliesFormContainer.getScene().getWindow();
	    	stage.close();
	    }
	    
	    public void resetAllField() {
	    	medicalSuppliesNameField.setText("");
	    	medicalSuppliesQuantityField.setText("");
	    	medicalSuppliesPriceField.setText("");
	    }
}
