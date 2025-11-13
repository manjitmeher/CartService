package com.cart.exception;

public class UserLoginException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserLoginException() {
		super();
	}

	public UserLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UserLoginException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserLoginException(String message) {
		super(message);
	}

	public UserLoginException(Throwable cause) {
		super(cause);
	}

}
