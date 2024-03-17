package entity;

import java.sql.Timestamp;

public class History {

	private int historyId;
	private String patientId;
	private Timestamp date;
	private int status;
	
	public History(int historyId, String patientId, Timestamp date, int status) {
		this.historyId = historyId;
		this.patientId = patientId;
		this.date = date;
		this.status = status;
	}

	public int getHistoryId() {
		return historyId;
	}

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
	
	public int getStatus() {
		return status; 
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	
	
}
