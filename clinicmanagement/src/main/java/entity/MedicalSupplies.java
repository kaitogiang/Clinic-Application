package entity;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MedicalSupplies {

	private SimpleIntegerProperty orderNumer;
	
	private SimpleStringProperty id;
	
	private SimpleStringProperty name;
	
	private SimpleIntegerProperty quantity;
	
	private SimpleIntegerProperty spoiledQuantity;
	
	private SimpleFloatProperty price;
	
	private SimpleFloatProperty total;
	
	public MedicalSupplies(int orderNumber, String id, String name, int quantity, int spoiledQuantity, float price, float total) {
		this.orderNumer = new SimpleIntegerProperty(orderNumber);
		this.id = new SimpleStringProperty(id);
		this.name = new SimpleStringProperty(name);
		this.quantity = new SimpleIntegerProperty(quantity);
		this.spoiledQuantity = new SimpleIntegerProperty(spoiledQuantity);
		this.price = new SimpleFloatProperty(price);
		this.total = new SimpleFloatProperty(total);
	}

	public SimpleStringProperty getId() {
		return id;
	}

	public void setId(SimpleStringProperty id) {
		this.id = id;
	}

	public SimpleIntegerProperty getOrderNumer() {
		return orderNumer;
	}

	public void setOrderNumer(SimpleIntegerProperty orderNumer) {
		this.orderNumer = orderNumer;
	}

	public SimpleStringProperty getName() {
		return name;
	}

	public void setName(SimpleStringProperty name) {
		this.name = name;
	}

	public SimpleIntegerProperty getQuantity() {
		return quantity;
	}

	public void setQuantity(SimpleIntegerProperty quantity) {
		this.quantity = quantity;
	}

	public SimpleIntegerProperty getSpoiledQuantity() {
		return spoiledQuantity;
	}

	public void setSpoiledQuantity(SimpleIntegerProperty spoiledQuantity) {
		this.spoiledQuantity = spoiledQuantity;
	}

	public SimpleFloatProperty getPrice() {
		return price;
	}

	public void setPrice(SimpleFloatProperty price) {
		this.price = price;
	}

	public SimpleFloatProperty getTotal() {
		return total;
	}

	public void setTotal(SimpleFloatProperty total) {
		this.total = total;
	}
	
	public int getOrderNumberValue() {
		return this.orderNumer.get();
	}
	
	public String getNameValue() {
		return this.name.get();
	}
	
	public int getQuantityValue() {
		return quantity.get();
	}
	
	public int getSpoiledQuantityValue() {
		return spoiledQuantity.get();
	}
	
	public float getPriceValue() {
		return price.get();
	}
	
	public float getTotalValue() {
		return total.get();
	}
	
	public String getIdValue() {
		return id.get();
	}
	
	public void setOrderNumber(int orderNumber) {
		this.orderNumer.set(orderNumber);
	}
	
	public void setNameValue(String name) {
		this.name.set(name);
	}
	
	public void setQuantityValue(int quantity) {
		this.quantity.set(quantity);
	}
	
	public void setSpoiledQuantity(int spoiledQuantity) {
		this.spoiledQuantity.set(spoiledQuantity);
	}
	
	public void setPriceValue(float price) {
		this.price.set(price);
	}
	
	public void setTotalValue(float total) {
		this.total.set(total);
	}
	
	public void setAllValue(String name, int quantity, int spoiledQuantity, float price, float total) {
		this.name.set(name);
		this.quantity.set(quantity);
		this.spoiledQuantity.set(spoiledQuantity);
		this.price.set(price);
		this.total.set(total);
	}
}
