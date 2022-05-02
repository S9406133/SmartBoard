package com.smartboard.model;

@SuppressWarnings("serial")
public class StringLengthException extends Exception{
	
	public StringLengthException() {
		super();
	}
	
	public StringLengthException(String message) {
		super(message);
	}

	@Override
	public String getMessage() {
		return "Invalid string length";
	}

}
