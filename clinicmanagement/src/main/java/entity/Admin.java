package entity;

public class Admin {
	
	private String adminId;
	
	private String adminName;
	
	private String adminEmail;
	
	private String phoneNumber;
	
	private String address;
	
	private float experience_year;
	
	private String role;
	
	private int imageId;
	
	public Admin(String adminId, String adminName, String adminEmail, String phoneNumber, 
			String address, float experience_year, String role, int imageId) {
		this.adminId = adminId;
		this.adminName = adminName;
		this.adminEmail = adminEmail;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.role = role;
		this.imageId = imageId;
		this.experience_year = experience_year;
	}

	public float getExperience_year() {
		return experience_year;
	}

	public void setExperience_year(float experience_year) {
		this.experience_year = experience_year;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
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

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
}
