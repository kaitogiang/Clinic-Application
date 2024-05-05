package entity;

public class Receptionist {

	private String receptionistId;
	
	private String receptionistName;
	
	private String receptionistEmail;
	
	private String phone_number;
	
	private String address;
	
	private String role;
	
	private String roleDescription;
	
	private String permissions;

	private int imageId; 
	
	public Receptionist(String receptionistId, String receptionistName, String receptionistEmail, String phone_number,
			String address, String role, String roleDescription, String permissions, int imageId) {
		this.receptionistId = receptionistId;
		this.receptionistName = receptionistName;
		this.receptionistEmail = receptionistEmail;
		this.phone_number = phone_number;
		this.address = address;
		this.role = role;
		this.roleDescription = roleDescription;
		this.permissions = permissions;
		this.imageId = imageId;
	}

	public String getReceptionistId() {
		return receptionistId;
	}

	public void setReceptionistId(String receptionistId) {
		this.receptionistId = receptionistId;
	}

	public String getReceptionistName() {
		return receptionistName;
	}

	public void setReceptionistName(String receptionistName) {
		this.receptionistName = receptionistName;
	}

	public String getReceptionistEmail() {
		return receptionistEmail;
	}

	public void setReceptionistEmail(String receptionistEmail) {
		this.receptionistEmail = receptionistEmail;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getAdress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
//	@Override
//	public String toString() {
//		return "Receptionist [receptionistId=" + receptionistId + ", receptionistName=" + receptionistName
//				+ ", receptionistEmail=" + receptionistEmail + ", phone_number=" + phone_number + ", role=" + role
//				+ ", roleDescription=" + roleDescription + ", permissions=" + permissions + "]";
//	}
	
	@Override
	public String toString() {
		return receptionistId+"-"+receptionistName;
	}
	
}
