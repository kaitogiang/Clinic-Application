package entity;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MedicalService {

	private SimpleIntegerProperty orderNumber;
	
	private SimpleStringProperty id;
	
	private SimpleStringProperty serviceName;
	
	private SimpleFloatProperty fee;
	
	public MedicalService(int orderNumber, String id, String serviceName, float fee) {
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
		this.id = new SimpleStringProperty(id);
		this.serviceName = new SimpleStringProperty(serviceName);
		this.fee = new SimpleFloatProperty(fee);
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

	public SimpleStringProperty getServiceName() {
		return serviceName;
	}

	public void setServiceName(SimpleStringProperty serviceName) {
		this.serviceName = serviceName;
	}

	public SimpleFloatProperty getFee() {
		return fee;
	}

	public void setFee(SimpleFloatProperty fee) {
		this.fee = fee;
	}
	
	public int getOrderNumberValue() {
		return orderNumber.get();
	}
	
	public String getServiceNameValue() {
		return serviceName.get();
	}
	
	public float getFeeValue() {
		return fee.get();
	}
	
	public String getIdValue() {
		return id.get();
	}
	
}
