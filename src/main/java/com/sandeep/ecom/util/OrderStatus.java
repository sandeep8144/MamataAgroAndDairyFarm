package com.sandeep.ecom.util;

public enum OrderStatus {

	IN_PROGESS(1, "In Progess"),
	ORDER_RECEIVED(2, "Order Received"),
	PRODUCT_PACKED(3, "Product Packed"),
	OUT_FOR_DELIVEY(4, "Out for Delivery"),
	DELIVERED(5, "Delivered"),
	CANCEL(6, "Cancelled");

	private Integer id;

	private String name;

	private OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
