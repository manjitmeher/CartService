package com.cart.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CartExceptionHandler {

	@ExceptionHandler(CartException.class)
	public ResponseEntity<ErrorMessage> userExceptionhandler(CartException ex){
		ErrorMessage msg = new ErrorMessage();
		msg.setErrorMessage(ex.getMessage());
		msg.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		
		return new ResponseEntity<ErrorMessage>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(UserLoginException.class)
	public ResponseEntity<ErrorMessage> userLoginExceptionhandler(UserLoginException ex){
		ErrorMessage msg = new ErrorMessage();
		msg.setErrorMessage(ex.getMessage());
		msg.setStatus(HttpStatus.UNAUTHORIZED.value());
		
		return new ResponseEntity<ErrorMessage>(msg, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> userLoginExceptionhandler(MethodArgumentNotValidException ex){
		ErrorMessage msg = new ErrorMessage();
		String errorMsg = ex.getBindingResult().getAllErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.joining(", "));
		msg.setErrorMessage(errorMsg);
		msg.setStatus(ex.getStatusCode().value());
		
		return new ResponseEntity<ErrorMessage>(msg, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ErrorMessage> addressNotFoundHandler(ProductNotFoundException ex){
		ErrorMessage msg = new ErrorMessage();
		msg.setErrorMessage(ex.getMessage());
		msg.setStatus(HttpStatus.NOT_FOUND.value());
		
		return new ResponseEntity<ErrorMessage>(msg, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(AddressNotFoundException.class)
	public ResponseEntity<ErrorMessage> addressNotFoundHandler(AddressNotFoundException ex){
		ErrorMessage msg = new ErrorMessage();
		msg.setErrorMessage(ex.getMessage());
		msg.setStatus(HttpStatus.NOT_FOUND.value());
		
		return new ResponseEntity<ErrorMessage>(msg, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> exceptionhandler(Exception ex){
		ErrorMessage msg = new ErrorMessage();
		msg.setErrorMessage(ex.getMessage());
		msg.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		
		return new ResponseEntity<ErrorMessage>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
