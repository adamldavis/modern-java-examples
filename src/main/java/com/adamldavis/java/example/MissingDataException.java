package com.adamldavis.java.example;

@SuppressWarnings("serial")
public class MissingDataException extends Exception {

	public MissingDataException() {
		super();
	}

	public MissingDataException(String message) {
		super(message);
	}
}