package com.jewelleryshop.modal;

import org.springframework.beans.factory.annotation.Autowired;

public class Size {

	@Autowired
	private String name;
	@Autowired
	private int quantity;
	
	public Size(String string) {
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
}
