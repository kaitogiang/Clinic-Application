package entity;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Expenditure {

	private SimpleIntegerProperty orderNumber;
	
	private SimpleStringProperty id;
	
	private SimpleStringProperty purpose;
	
	private SimpleFloatProperty amount;
	
	private SimpleStringProperty date;
	
	public Expenditure(int orderNumber, String id, String purpose, float amount, String date) {
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
		this.id = new SimpleStringProperty(id);
		this.purpose = new SimpleStringProperty(purpose);
		this.amount = new SimpleFloatProperty(amount);
		this.date = new SimpleStringProperty(date);
	}

	public SimpleStringProperty getId() {
		return id;
	}

	public void setId(SimpleStringProperty id) {
		this.id = id;
	}

	public SimpleIntegerProperty getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(SimpleIntegerProperty orderNumber) {
		this.orderNumber = orderNumber;
	}

	public SimpleStringProperty getPurpose() {
		return purpose;
	}

	public void setPurpose(SimpleStringProperty purpose) {
		this.purpose = purpose;
	}

	public SimpleFloatProperty getAmount() {
		return amount;
	}

	public void setAmount(SimpleFloatProperty amount) {
		this.amount = amount;
	}

	public SimpleStringProperty getDate() {
		return date;
	}

	public void setDate(SimpleStringProperty date) {
		this.date = date;
	}
	
	public int getOrderNumberValue() {
		return orderNumber.get();
	}
	
	public String getPurposeValue() {
		return purpose.get();
	}
	 
	public float getAmountValue() {
		return amount.get();
	}
	
	public String getDateValue() {
		return date.get();
	}
	
	public String getIdValue() {
		return id.get();
	}
	
}
