package com.example.OzanCaseStudy.Models;

public class Date {
	// This class returns the date of the event executed

	private java.sql.Date date = null;

	public Date() {
		long millis = System.currentTimeMillis();
		date = new java.sql.Date(millis);
	}

	public String getDate() {
		return date.toString();
	}
}
