package com.cart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartId;
	private Double totalAmount;
	private Double discountedAmount;
	private String products;
	private String userId;
	private Long selectedAddressId;

	public Cart(String userId) {
		this.totalAmount = 0.0;
		this.discountedAmount = 0.0;
		this.products = "";
		this.userId = userId;
	}
	
	public Cart() {
	}
	
	public void resetCart() {
		this.totalAmount = 0.0;
		this.discountedAmount = 0.0;
		this.products = "";
	}
	
	public Long getSelectedAddressId() {
		return selectedAddressId;
	}

	public void setSelectedAddressId(Long selectedAddressId) {
		this.selectedAddressId = selectedAddressId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getCartId() {
		return cartId;
	}

	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Double discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}
}
