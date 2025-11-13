package com.cart.dto;

import java.time.LocalDate;
import java.util.List;

import com.cart.entity.OrderData;

public class OrderDTO {

	private Long orderId;
	private String userId;
	private Double amount;
	private List<ProductDTO> products;
	private LocalDate orderDate;
	private Long pincode;
	private String city;
	private String streetNumber;
	private String orderStatus;
	
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public List<ProductDTO> getProducts() {
		return products;
	}
	public void setProducts(List<ProductDTO> products) {
		this.products = products;
	}
	public LocalDate getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}
	public Long getPincode() {
		return pincode;
	}
	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public static OrderDTO entityToDTO(OrderData order) {
		OrderDTO dto = new OrderDTO();
		dto.setAmount(order.getAmount());
		dto.setCity(order.getCity());
		dto.setOrderDate(order.getOrderDate());
		dto.setOrderId(order.getOrderId());
		dto.setOrderStatus(order.getOrderStatus());
		dto.setPincode(order.getPincode());
		dto.setStreetNumber(order.getStreetNumber());
		dto.setUserId(order.getUserId());
		
		return dto;
	}
}
