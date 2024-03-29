package entity;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Medicine {

	private SimpleStringProperty medicineId;
	
	private SimpleIntegerProperty orderNumber;
	
	private SimpleStringProperty medicineName;
	
	private SimpleStringProperty description;
	
	private SimpleStringProperty manufacturer;
	
	private SimpleIntegerProperty dosage;
	
	private SimpleStringProperty dosage_unit;
	
	private SimpleFloatProperty unit_price;
	
	private SimpleStringProperty expiration_date;
	
	private SimpleIntegerProperty quantity;
	
	public Medicine(String medicineId, int orderNumber, String medicineName,
					String description, String manufacturer, int dosage,
					String dosage_unit, float unit_price, String expiration_date, int quantity) {
		this.medicineId = new SimpleStringProperty(medicineId);
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
		this.medicineName = new SimpleStringProperty(medicineName);
		this.description = new SimpleStringProperty(description);
		this.manufacturer = new SimpleStringProperty(manufacturer);
		this.dosage = new SimpleIntegerProperty(dosage);
		this.dosage_unit = new SimpleStringProperty(dosage_unit);
		this.unit_price = new SimpleFloatProperty(unit_price);
		this.expiration_date = new SimpleStringProperty(expiration_date);
		this.quantity = new SimpleIntegerProperty(quantity);
	}

	public SimpleIntegerProperty getQuantity() {
		return quantity;
	}

	public void setQuantity(SimpleIntegerProperty quantity) {
		this.quantity = quantity;
	}

	public SimpleStringProperty getMedicineId() {
		return medicineId;
	}

	public void setMedicineId(SimpleStringProperty medicineId) {
		this.medicineId = medicineId;
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

	public SimpleStringProperty getDescription() {
		return description;
	}

	public void setDescription(SimpleStringProperty description) {
		this.description = description;
	}

	public SimpleStringProperty getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(SimpleStringProperty manufacturer) {
		this.manufacturer = manufacturer;
	}

	public SimpleIntegerProperty getDosage() {
		return dosage;
	}

	public void setDosage(SimpleIntegerProperty dosage) {
		this.dosage = dosage;
	}

	public SimpleStringProperty getDosage_unit() {
		return dosage_unit;
	}

	public void setDosage_unit(SimpleStringProperty dosage_unit) {
		this.dosage_unit = dosage_unit;
	}

	public SimpleFloatProperty getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(SimpleFloatProperty unit_price) {
		this.unit_price = unit_price;
	}

	public SimpleStringProperty getExpiration_date() {
		return expiration_date;
	}

	public void setExpiration_date(SimpleStringProperty expiration_date) {
		this.expiration_date = expiration_date;
	}
	
	public String getMedicineIdValue() {
		return medicineId.get();
	}
	
	public int getOrderNumberVaue() {
		return orderNumber.get();
	}
	
	public String getMedicineNameValue() {
		return medicineName.get();
	}
	
	public String getDescriptionValue() {
		return description.get();
	}
	
	public String getManufacturerValue() {
		return manufacturer.get();
	}
	
	public int getDosageValue() {
		return dosage.get();
	}
	
	public String getDosageUnitValue() {
		return dosage_unit.get();
	}
	
	public float getUnitPriceValue() {
		return unit_price.get();
	}
	
	public String getExpirationDateValue() {
		return expiration_date.get();
	}
	
	public int getQuantityValue() {
		return quantity.get();
	}
	
	public String toString() {
		return medicineName.get();
	}
}

