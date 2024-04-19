package entity;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Invoice {
	SimpleIntegerProperty orderNumber;
	SimpleStringProperty invoiceId;
	SimpleStringProperty precriptionId;
	SimpleStringProperty creationDate;
	SimpleFloatProperty totalAmount;
	
	public Invoice(int orderNumber, String invoiceId, String prescriptionId, String creationDate, float totalAmount) {
		this.orderNumber = new SimpleIntegerProperty(orderNumber);
		this.invoiceId = new SimpleStringProperty(invoiceId);
		this.precriptionId = new SimpleStringProperty(prescriptionId);
		this.creationDate = new SimpleStringProperty(creationDate);
		this.totalAmount = new SimpleFloatProperty(totalAmount);
	}

	public SimpleIntegerProperty getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(SimpleIntegerProperty orderNumber) {
		this.orderNumber = orderNumber;
	}

	public SimpleStringProperty getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(SimpleStringProperty invoiceId) {
		this.invoiceId = invoiceId;
	}

	public SimpleStringProperty getPrecriptionId() {
		return precriptionId;
	}

	public void setPrecriptionId(SimpleStringProperty precriptionId) {
		this.precriptionId = precriptionId;
	}

	public SimpleStringProperty getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(SimpleStringProperty creationDate) {
		this.creationDate = creationDate;
	}

	public SimpleFloatProperty getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(SimpleFloatProperty totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public String getInvoiceIdValue() {
		return invoiceId.get();
	}
	
	public String getPrescriptionIdValue() {
		return precriptionId.get();
	}
	
	public String getCreationDateValue() {
		return creationDate.get();
	}
	
	public float getTotalAmountValue() {
		return totalAmount.get();
	}
}
