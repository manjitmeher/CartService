package com.cart.api;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cart.dto.CartDTO;
import com.cart.dto.OrderDTO;
import com.cart.exception.AddressNotFoundException;
import com.cart.exception.CartException;
import com.cart.exception.UserLoginException;
import com.cart.service.CartService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartService cartService;
	
	@Autowired
	private HttpServletRequest request;
	
	@GetMapping("/createCart")
	public Long createCart(@RequestParam("userId") String userId) throws CartException {
		Long response = cartService.createCart(userId);
		return response;
	}
	
	@GetMapping("/addProductToCart")
	public ResponseEntity<String> addProductToCart(@RequestParam("productId") String productId) throws CartException, UserLoginException{
		String response = cartService.addProductToCart(productId, request);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@GetMapping("/removeProductFromCart")
	public ResponseEntity<String> removeProductFromCart(@RequestParam("productId") String productId) throws CartException, UserLoginException{
		String response = cartService.removeProductFromCart(productId, request);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@GetMapping("/getCartDetails")
	public ResponseEntity<CartDTO> getCartDetails() throws CartException, UserLoginException{
		CartDTO response = cartService.getCartDetails(request);
		return new ResponseEntity<CartDTO>(response, HttpStatus.OK);
	}
	
	@GetMapping("/buyCartedProduct")
	public ResponseEntity<String> buyCartedProduct() throws CartException, UserLoginException, InterruptedException, ExecutionException{
		String response = cartService.buyCartedProduct(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/buySpecificProduct")
	public ResponseEntity<String> buySpecificProduct(@RequestParam("productId") String productId,@RequestParam("quantity") String quantity) throws CartException, UserLoginException, InterruptedException, ExecutionException{
		String response = cartService.buySpecificProduct(productId, quantity, request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/getOrdersDetails")
	public ResponseEntity<List<OrderDTO>> getOrdersDetails() throws CartException, UserLoginException{
		List<OrderDTO> response = cartService.getOrderDetails(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/changeAddress")
	public ResponseEntity<Boolean> changeAddress(@RequestParam("addressId") Long addressId) throws AddressNotFoundException, CartException, UserLoginException{
		Boolean response = cartService.changeAddress(request, addressId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
