package entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

public class Prescription {

	private SimpleIntegerProperty orderNumber;
	
	private SimpleStringProperty prescriptionId;
	
	private SimpleObjectProperty<Patient> patient;

	private SimpleStringProperty note;
	
	private SimpleObjectProperty<ObservableList<PrescriptionDetail>> medicineList;
	
	public Prescription(int orderNumber, String prescriptionId, Patient patient, 
			 String note) {
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
		this.prescriptionId = new SimpleStringProperty(prescriptionId);
		this.patient = new SimpleObjectProperty<Patient>(patient);
		this.note = new SimpleStringProperty(note);
	}

	public void setMedicineList(ObservableList<PrescriptionDetail> medicineList) {
		this.medicineList = new SimpleObjectProperty<ObservableList<PrescriptionDetail>>(medicineList);
	}
	
	public SimpleObjectProperty<Patient> getPatient() {
		return patient;
	}

	public void setPatient(SimpleObjectProperty<Patient> patient) {
		this.patient = patient;
	}

	public SimpleIntegerProperty getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(SimpleIntegerProperty orderNumber) {
		this.orderNumber = orderNumber;
	}

	public SimpleStringProperty getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(SimpleStringProperty prescriptionId) {
		this.prescriptionId = prescriptionId;
	}
	
	public String getPrescriptionIdvalue() {
		return prescriptionId.get();
	}
	
	public String getNoteValue() {
		return note.get();
	}
	
	public Patient getPatientValue() {
		return patient.get();
	}
	
	public ObservableList<PrescriptionDetail> getMedicineList() {
		return medicineList.get();
	}
	
	public String toString() {
		return String.valueOf(orderNumber.get())+ "-" + prescriptionId.get() + " - " +
				patient.get() +  " - " +note.get() + " - " +
				"Kich thuoc MedicineList: "+medicineList.get().size();
	}
}
