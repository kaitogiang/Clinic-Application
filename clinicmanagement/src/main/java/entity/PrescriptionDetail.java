package entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class PrescriptionDetail {

	private SimpleIntegerProperty orderNumber;
	
	private SimpleStringProperty medicineName;
	
	private SimpleIntegerProperty medicineQuantity;
	
	private SimpleStringProperty usage;
	
	private SimpleStringProperty notes;
	
	private SimpleObjectProperty<Medicine> medicine;
	
	public PrescriptionDetail(int orderNumber, String medicineName, int medicineQuantity
							, String usage, String notes, Medicine medicine) {
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
		this.medicineName = new SimpleStringProperty(medicineName);
		this.medicineQuantity = new SimpleIntegerProperty(medicineQuantity);
		this.usage = new SimpleStringProperty(usage);
		this.notes = new SimpleStringProperty(notes);
		this.medicine = new SimpleObjectProperty<Medicine>(medicine);
	}
	
	public PrescriptionDetail(int orderNumber, String medicineName, int medicineQuantity 
							, String usage, Medicine medicine) {
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
		this.medicineName = new SimpleStringProperty(medicineName);
		this.medicineQuantity = new SimpleIntegerProperty(medicineQuantity);
		this.usage = new SimpleStringProperty(usage);
		this.medicine = new SimpleObjectProperty<Medicine>(medicine);
	}
	
	public PrescriptionDetail(int orderNumber, String medicineName, int medicineQuantity 
			, String usage) {
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
		this.medicineName = new SimpleStringProperty(medicineName);
		this.medicineQuantity = new SimpleIntegerProperty(medicineQuantity);
		this.usage = new SimpleStringProperty(usage);
	}
	
	public SimpleIntegerProperty getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(SimpleIntegerProperty orderNumber) {
		this.orderNumber = orderNumber;
	}

	public SimpleStringProperty getMedicineName() {
		return medicineName;
	}

	public void setMedicineName(SimpleStringProperty medicineName) {
		this.medicineName = medicineName;
	}

	public SimpleIntegerProperty getMedicineQuantity() {
		return medicineQuantity;
	}

	public void setMedicineQuantity(SimpleIntegerProperty medicineQuantity) {
		this.medicineQuantity = medicineQuantity;
	}

	public SimpleStringProperty getUsage() {
		return usage;
	}

	public void setUsage(SimpleStringProperty usage) {
		this.usage = usage;
	}

	public SimpleStringProperty getNotes() {
		return notes;
	}

	public void setNotes(SimpleStringProperty notes) {
		this.notes = notes;
	}

	public SimpleObjectProperty<Medicine> getMedicine() {
		return medicine;
	}

	public void setMedicine(SimpleObjectProperty<Medicine> medicine) {
		this.medicine = medicine;
	}
	
	public int getOrderNumberValue() {
		return orderNumber.get();
	}
	
	public String getMedicineNameValue() {
		return medicineName.get();
	}
	
	public int medicineQuantityValue() {
		return medicineQuantity.get();
	}
	
	public String getUsageValue() {
		return usage.get();
	}
	
	public String getNotesValue() {
		return notes.get();
	}
	
	public Medicine getMedicineValue() {
		return medicine.get();
	}
	
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
	}
	
	public int getMedicineQuantityValue() {
		return medicineQuantity.get();
	}
	
	
	public String toString() {
		return orderNumber.get() + " - " + medicineName.get() + " - " + medicineQuantity.get() + 
				" - " + usage.get() + " - " + notes.get() + " id thuoc: " + medicine.get();
	}
}
