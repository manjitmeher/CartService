package com.cart.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.cart.dto.CartDTO;
import com.cart.dto.OrderDTO;
import com.cart.dto.UserDTO;
import com.cart.exception.AddressNotFoundException;
import com.cart.exception.CartException;
import com.cart.exception.UserLoginException;

import jakarta.servlet.http.HttpServletRequest;

public interface CartService {

	public Long createCart(String userId) throws CartException;
	
	public String addProductToCart(String productId, HttpServletRequest request) throws CartException, UserLoginException;
	
	public String removeProductFromCart(String productId, HttpServletRequest request) throws CartException, UserLoginException;
	
	public CartDTO getCartDetails(HttpServletRequest request) throws UserLoginException;
	
	public String buyCartedProduct(HttpServletRequest request) throws CartException, UserLoginException, InterruptedException, ExecutionException;
	
	public String buySpecificProduct(String productId, String quantity, HttpServletRequest request) throws CartException, UserLoginException, InterruptedException, ExecutionException;

	public List<OrderDTO> getOrderDetails(HttpServletRequest request) throws UserLoginException;
	
	public UserDTO validateToken(HttpServletRequest request) throws UserLoginException;
	
	public Boolean changeAddress(HttpServletRequest request, Long addressId) throws CartException, UserLoginException, AddressNotFoundException;
}
