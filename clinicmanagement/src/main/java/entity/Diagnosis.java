package entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Diagnosis {

	private SimpleIntegerProperty diagnosisId;
	
	private SimpleStringProperty patientName;
	
	private SimpleStringProperty doctorName;
	
	private SimpleStringProperty diagnosis;
	
	private SimpleStringProperty testResult;
	
	private SimpleStringProperty notes;
	
	private SimpleStringProperty diagnosisDate;
	
	private SimpleStringProperty prescriptionId; 
	
	public Diagnosis(int diagnosisId, String patientName, String doctorName, 
			String diagnosis, String testResult, 
			String notes, String diagnosisDate, String prescriptionId) { 
		
		this.diagnosisId = new SimpleIntegerProperty(diagnosisId);
		
		this.patientName = new SimpleStringProperty(patientName);
		
		this.doctorName = new SimpleStringProperty(doctorName);
		
		this.diagnosis = new SimpleStringProperty(diagnosis);
		
		this.testResult = new SimpleStringProperty(testResult);
		
		this.notes = new SimpleStringProperty(notes);
		
		this.diagnosisDate = new SimpleStringProperty(diagnosisDate);
		
		this.prescriptionId = new SimpleStringProperty(prescriptionId);
		
	}

	public String toString() {
		return getDiagnosisIdValue()+" - "+ getPatientNameValue() + " - "
				+ getDoctorNameValue() + " - " + getDiagnosisValue() + " - "
				+ getTestResultValue() + " - " + getNotesValue() + " - "
				+ getDiagnosisDateValue() + " - " + " id thuoc";
	}
	
	public SimpleIntegerProperty getDiagnosisId() {
		return diagnosisId;
	}

	public void setDiagnosisId(SimpleIntegerProperty diagnosisId) {
		this.diagnosisId = diagnosisId;
	}

	public SimpleStringProperty getPatientName() {
		return patientName;
	}
	
	public void setPatientName(SimpleStringProperty patientName) {
		this.patientName = patientName;
	}
	
	public SimpleStringProperty getDoctorName() {
		return doctorName;
	}
	
	public void setDoctorName(SimpleStringProperty doctorName) {
		this.doctorName = doctorName;
	}
	
	public SimpleStringProperty getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(SimpleStringProperty diagnosis) {
		this.diagnosis = diagnosis;
	}

	public SimpleStringProperty getTestResult() {
		return testResult;
	}

	public void setTestResult(SimpleStringProperty testResult) {
		this.testResult = testResult;
	}

	public SimpleStringProperty getNotes() {
		return notes;
	}

	public void setNotes(SimpleStringProperty notes) {
		this.notes = notes;
	}

	public SimpleStringProperty getDiagnosisDate() {
		return diagnosisDate;
	}

	public void setDiagnosisDate(SimpleStringProperty diagnosisDate) {
		this.diagnosisDate = diagnosisDate;
	}
	
	public int getDiagnosisIdValue() {
		return diagnosisId.get();
	}
	
	public String getPatientNameValue() {
		return patientName.get();
	}
	
	public String getDoctorNameValue() {
		return doctorName.get();
	}
	
	public String getDiagnosisValue() {
		return diagnosis.get();
	}
	
	public String getTestResultValue() {
		return testResult.get();
	}
	
	public String getNotesValue() {
		return notes.get();
	}
	
	public String getDiagnosisDateValue() {
		return diagnosisDate.get();
	}
	
	public String getPrescriptionIdValue() {
		return prescriptionId.get();
	}
	
}
