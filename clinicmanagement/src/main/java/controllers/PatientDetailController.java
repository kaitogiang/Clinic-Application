package controllers;

import java.util.List;

import entity.History;
import entity.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PatientDetailController {

	@FXML
    private Label asideBarTitle;

    @FXML
    private ListView<String> historyFieldList;

    @FXML
    private TextArea patientAddressField;

    @FXML
    private TextField patientAgeField;

    @FXML
    private TextField patientNameField;

    @FXML
    private TextField patientPhoneField;

    @FXML
    private TextField patientWeightField;
    
    public void setData(Patient e, List<History> history) {
    	patientNameField.setText(e.getPatientNameValue());
    	patientAgeField.setText(String.valueOf(e.getPatientAgeValue()));
    	patientPhoneField.setText(e.getPatientPhoneNumberValue());
    	patientWeightField.setText(String.valueOf(e.getPatientWeightValue()));
    	patientAddressField.setText(e.getPatientAddressValue());
    	int i;
    	for(i=0; i<history.size(); i++) {
    		historyFieldList.getItems().add("Lần "+(i+1)+": "+history.get(i).getDate());
    	}
    }
    public void close() {
    	Stage currentStage = (Stage) patientAddressField.getScene().getWindow();
    	currentStage.close();
    }
}
