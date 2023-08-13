package com.example.OzanCaseStudy.Models;

public class Transaction {

	private String id;
	private String from;
	private String to;
	private double amount;
	private double formattedAmount;

	public Transaction(String id, String from, String to, double amount) {
		// This is an class for the transactions that I print on the conversion list api
		// I have also created formattedAmount variable so that I can display the
		// currencies in a better way
		this.id = id;
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.formattedAmount = amount * 100;
		this.formattedAmount = Math.round(this.formattedAmount);
		this.formattedAmount = this.formattedAmount / 100;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getFormattedAmount() {
		return this.formattedAmount;
	}
}
