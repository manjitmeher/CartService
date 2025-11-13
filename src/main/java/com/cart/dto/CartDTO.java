package com.cart.dto;

import java.util.List;

import com.cart.entity.Cart;

public class CartDTO {

	private Long cartId;
	private Double totalAmount;
	private Double discountedAmount;
	private List<ProductDTO> products;
	private String userId;
	private Long selectedAddressId;

	public Long getSelectedAddressId() {
		return selectedAddressId;
	}

	public void setSelectedAddressId(Long selectedAddressId) {
		this.selectedAddressId = selectedAddressId;
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

	public List<ProductDTO> getProducts() {
		return products;
	}

	public void setProducts(List<ProductDTO> products) {
		this.products = products;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public static CartDTO entityToDTO(Cart cart, List<ProductDTO> productDTO) {
		CartDTO dto = new CartDTO();
		dto.setCartId(cart.getCartId());
		dto.setUserId(cart.getUserId());
		dto.setTotalAmount(cart.getTotalAmount());
		dto.setDiscountedAmount(cart.getDiscountedAmount());
		dto.setProducts(productDTO);
		dto.setSelectedAddressId(cart.getSelectedAddressId());
		
		return dto;
	}
}

