package entity;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Patient {
	private SimpleStringProperty patientId;
	
	private SimpleStringProperty patientName;
	
	private SimpleIntegerProperty age;
	
	private SimpleFloatProperty weight; 
	
	private SimpleStringProperty address;
	
	private SimpleStringProperty phoneNumber;
	
	private SimpleIntegerProperty orderNumber;

	private SimpleStringProperty status;
	
	//Hàm xây dựng dành cho receptionist sử dụng
	public Patient(String patientId, String patientName, int age,
			float weight, String address, String phoneNumber,int orderNumber) {
		this.patientId = new SimpleStringProperty(patientId);
		this.patientName = new SimpleStringProperty(patientName);
		this.age = new SimpleIntegerProperty(age);
		this.weight = new SimpleFloatProperty(weight);
		this.address = new SimpleStringProperty(address);
		this.phoneNumber = new SimpleStringProperty(phoneNumber);
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
	}
	//Hàm xây dựng được sử dụng cho Doctor
	public Patient(String patientId, int orderNumber, String patientName, int age,
			float weight, String phoneNumber, String address, String status) {
		this.patientId = new SimpleStringProperty(patientId);
		this.status = new SimpleStringProperty(status);
		this.patientName = new SimpleStringProperty(patientName);
		this.age = new SimpleIntegerProperty(age);
		this.weight = new SimpleFloatProperty(weight);
		this.address = new SimpleStringProperty(address);
		this.phoneNumber = new SimpleStringProperty(phoneNumber);
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
	}
	
	public Patient(String patientId, String patientName, int age, float weight, 
			String phoneNumber, String address) {
		this.patientId = new SimpleStringProperty(patientId);
		this.patientName = new SimpleStringProperty(patientName);
		this.age = new SimpleIntegerProperty(age);
		this.weight = new SimpleFloatProperty(weight);
		this.phoneNumber = new SimpleStringProperty(phoneNumber);
		this.address = new SimpleStringProperty(address);
	}
	
	public Patient(Patient e) {
		this.patientId = new SimpleStringProperty(e.getPatientIdValue());
		this.status = new SimpleStringProperty(e.getStatusValue());
		this.patientName = new SimpleStringProperty(e.getPatientNameValue());
		this.age = new SimpleIntegerProperty(e.getPatientAgeValue());
		this.weight = new SimpleFloatProperty(e.getPatientWeightValue());
		this.address = new SimpleStringProperty(e.getPatientAddressValue());
		this.phoneNumber = new SimpleStringProperty(e.getPatientPhoneNumberValue());
		this.orderNumber = new SimpleIntegerProperty(e.getOrderNumberValue());
	}
	
	public SimpleStringProperty getPatientId() {
		return patientId;
	}

	public void setPatientId(SimpleStringProperty patientId) {
		this.patientId = patientId;
	}

	public SimpleStringProperty getPatientName() {
		return patientName;
	}

	public void setPatientName(SimpleStringProperty patientName) {
		this.patientName = patientName;
	}

	public SimpleIntegerProperty getAge() {
		return age;
	}

	public void setAge(SimpleIntegerProperty age) {
		this.age = age;
	}

	public SimpleFloatProperty getWeight() {
		return weight;
	}

	public void setWeight(SimpleFloatProperty weight) {
		this.weight = weight;
	}

	public SimpleStringProperty getAddress() {
		return address;
	}

	public void setAddress(SimpleStringProperty address) {
		this.address = address;
	}

	public SimpleStringProperty getStatus() {
		return status;
	}
	
	public void setStatus(SimpleStringProperty status) {
		this.status = status;
	}
	
	public SimpleStringProperty getPhoneNumber() {
		return phoneNumber;
	}
	
	
	public void setPhoneNumber(SimpleStringProperty phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setOrderNumber(SimpleIntegerProperty orderNumber) {
		this.orderNumber = orderNumber;
	}
	public SimpleIntegerProperty getOrderNumber() {
		return orderNumber;
	}
	
	public String getPatientIdValue() {
		return patientId.get();
	}
	
	public String getPatientNameValue() {
		return patientName.get();
	}
	
	public int getPatientAgeValue() {
		return age.get();
	}
	
	public float getPatientWeightValue() {
		return weight.get();
	}
	
	public String getPatientAddressValue() {
		return address.get();
	}
	
	public String getPatientPhoneNumberValue() {
		return phoneNumber.get();
	}
	
	public int getOrderNumberValue() {
		return orderNumber.get();
	}
	
	public String getStatusValue() {
		return status.get();
	}
	
	public String toString() {
		return patientId.get()+" - "+orderNumber.get()+" - "+patientName.get() + " - " + age.get() + " - "
				+ weight.get()+ " - " + address.get() + " - " + phoneNumber.get();
	}
	//Hàm dành cho kiểm tra thông tin cơ bản
	public boolean isSameAs(Patient e) {
		boolean check = false;
		
		if (getPatientIdValue().equals(e.getPatientIdValue()) 
			&& getPatientNameValue().equals(e.getPatientNameValue())
			&& getPatientAgeValue()==e.getPatientAgeValue() 
			&& getPatientWeightValue() == e.getPatientWeightValue()
			&& getPatientAddressValue().equals(e.getPatientAddressValue()) 
			&& getPatientPhoneNumberValue().equals(e.getPatientPhoneNumberValue())
			&& getOrderNumberValue() == e.getOrderNumberValue()) {
			check = true;
		}
		return check;
	}
	//Hàm so sánh toàn thể hai đối tượng, trả về true nếu tất cả đều giống nhau
	public boolean isEqual(Patient e) {
		boolean check = false;
		if (getPatientIdValue().equals(e.getPatientIdValue()) 
				&& getPatientNameValue().equals(e.getPatientNameValue())
				&& getPatientAgeValue()==e.getPatientAgeValue() 
				&& getPatientWeightValue() == e.getPatientWeightValue()
				&& getPatientAddressValue().equals(e.getPatientAddressValue()) 
				&& getPatientPhoneNumberValue().equals(e.getPatientPhoneNumberValue())
				&& getOrderNumberValue() == e.getOrderNumberValue()
				&& getStatusValue().equals(e.getStatusValue())) {
				check = true;
			}
			return check;
	}
	
	//Hàm kiểm tra trạng thái của hai đối tượng
//	public boolean isSameStatusWith(Patient e) {
//		return getStatusValue().equals(e.getStatusValue());
//	}
}

	