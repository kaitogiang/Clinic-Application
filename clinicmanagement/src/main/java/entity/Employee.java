package entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Employee<T> {

	private SimpleIntegerProperty orderNumber;
	
	private SimpleStringProperty employeeName;
	
	private SimpleStringProperty username;
	
	private SimpleStringProperty role;
	
	private String roleDescription;
		
	private String rolePermission;
	
	private T data;
	
	private int accountId;
	
	private String accountCreationDate;
	
	public Employee(int orderNumber, String employeeName, String username, String role, String roleDescription, String rolePermission, T data, 
			int accountId, String accountCreationDate) {
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
		this.employeeName = new SimpleStringProperty(employeeName);
		this.username = new SimpleStringProperty(username);
		this.role = new SimpleStringProperty(role);
		this.roleDescription = roleDescription;
		this.data = data;
		this.accountId = accountId;
		this.rolePermission = rolePermission;
		this.accountCreationDate = accountCreationDate;
	}
	
	public String getRolePermission() {
		return rolePermission;
	}
	
	public String getAccountCreationDate() {
		return accountCreationDate;
	}
	
	public int getOrderNumberValue() {
		return orderNumber.get();
	}
	
	public String getEmployeeNameValue() {
		return employeeName.get();
	}
	
	public String getUsernameValue() {
		return username.get();
	}
	
	public SimpleStringProperty getRole() {
		return role;
	}
	
	public SimpleIntegerProperty getOrderNumber() {
		return orderNumber;
	}
	
	public SimpleStringProperty getEmployeeName() {
		return employeeName;
	}
	
	public SimpleStringProperty getUsername() {
		return username;
	}
	
	public String getRoleDescription() {
		return roleDescription;
	}
	
	public T getData() {
		return data;
	}
	
	public int getAccountId() {
		return accountId;
	}
	
	
	public String getRoleValue() {
		return role.get();
	}
	
	
	public void setOrderNumber(int orderNumber) {
		this.orderNumber.set(orderNumber);
	}
	
	public void setEmployeeName(String employeeName) {
		this.employeeName.set(employeeName);
	}
	
	public void setUsername(String username) {
		this.username.set(username);
	}
	
	public void setRole(String role) {
		this.role.set(role);
	}
	
	public void setRolePermission(String rolePermission) {
		this.rolePermission = rolePermission;
	}
	
	public void setAccountCreationDate(String accountCreationDate) {
		this.accountCreationDate = accountCreationDate;
	}
	
	public String toString() {
		return employeeName.get() + " - " + username.get() + " - " + role.get() + " - " +data; 
	}
}
