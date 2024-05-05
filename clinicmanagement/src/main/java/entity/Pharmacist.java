package entity;

public class Pharmacist {

private String pharmacistId;
	
	private String pharmacistName;
	
	private String pharmacistEmail;
	
	private String phoneNumber;
	
	private String address;
	
	private float experienceYear;
	
	private String role;
	
	private String roleDescription;
	
	private String permissions;

	private int imageId;

	public Pharmacist(String pharmacistId, String pharmacistName, String pharmacistEmail, String phoneNumber,
			String address, float experienceYear, String role, String roleDescription, String permissions,
			int imageId) {
		this.pharmacistId = pharmacistId;
		this.pharmacistName = pharmacistName;
		this.pharmacistEmail = pharmacistEmail;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.experienceYear = experienceYear;
		this.role = role;
		this.roleDescription = roleDescription;
		this.permissions = permissions;
		this.imageId = imageId;
	}

	public String getPharmacistId() {
		return pharmacistId;
	}

	public void setPharmacistId(String pharmacistId) {
		this.pharmacistId = pharmacistId;
	}

	public String getPharmacistName() {
		return pharmacistName;
	}

	public void setPharmacistName(String pharmacistName) {
		this.pharmacistName = pharmacistName;
	}

	public String getPharmacistEmail() {
		return pharmacistEmail;
	}

	public void setPharmacistEmail(String pharmacistEmail) {
		this.pharmacistEmail = pharmacistEmail;
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

	public float getExperienceYear() {
		return experienceYear;
	}

	public void setExperienceYear(float experienceYear) {
		this.experienceYear = experienceYear;
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
	
	public String toString() {
		return pharmacistId+"-"+pharmacistName;
	}
}
