package entity;

public class Doctor {

	private String doctorId;
	
	private String doctorName;
	
	private String doctorEmail;
	
	private String phoneNumber;
	
	private String address;
	
	private float experienceYear;
	
	private String role;
	
	private String roleDescription;
	
	private String permissions;

	private int imageId;

	public Doctor(String doctorId, String doctorName, String doctorEmail, String phoneNumber, String address,
			float experienceYear, String role, String roleDescription, String permissions, int imageId) {
		this.doctorId = doctorId;
		this.doctorName = doctorName;
		this.doctorEmail = doctorEmail;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.experienceYear = experienceYear;
		this.role = role;
		this.roleDescription = roleDescription;
		this.permissions = permissions;
		this.imageId = imageId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getDoctorEmail() {
		return doctorEmail;
	}

	public void setDoctorEmail(String doctorEmail) {
		this.doctorEmail = doctorEmail;
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
		return doctorId+"-"+doctorName;
	}
}
