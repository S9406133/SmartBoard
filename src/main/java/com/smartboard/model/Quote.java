package com.smartboard.model;

public class Quote {
	
	private final String quote;
	private final String author;
	
	public Quote (String quote, String author) {
		this.quote = quote;
		this.author = author;
	}
	
	@Override
	public String toString() {
		return String.format("\"%s\" - %s", quote, author);
	}

}
