package com.example.OzanCaseStudy.Controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.OzanCaseStudy.Models.AssignId;
import com.example.OzanCaseStudy.Models.Date;
import com.example.OzanCaseStudy.Models.ExchangeListCreator;
import com.example.OzanCaseStudy.Models.FireBaseService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.internal.FirebaseService;

@Controller
public class ConversionController {
	//Application runs on default port with current settings(8080), http://localhost:8080/conversion

	static List<String> fromList = null;
	static List<String> toList = null;
	static JSONObject rates = null;
	static FireBaseService fbs = null;
	static String transactionMessage = "";
	static Firestore db = null;
	static DecimalFormat df = new DecimalFormat("0.00");
	static String selectedToValue = "USD";
	static String selectedFromValue = "EUR";

	@PostMapping("/submit-transaction")
	@ResponseBody
	public String transactionFunction(@RequestBody Map<String, String> requestData, Model model) {
		// Firstly I get the values from the html into created variables
		selectedFromValue = requestData.get("selectedFromValue");
		selectedToValue = requestData.get("selectedToValue");

		// I check if the amount text box is empty
		if (requestData.get("transAmount") == "") {
			transactionMessage = "No Input Entered";
		}
		// Then i check if the amount starts with 0 because if it does, parseDouble
		// function still can return an double but I don't think a number starting with
		// 0 is a valid value
		else if (requestData.get("transAmount").charAt(0) == '0') {
			transactionMessage = "Invalid Input";
		} else {
			try {
				// I try to convert the string into double if it fails to do so, it prints error
				// message as invalid input on the html
				double amount = Double.parseDouble(requestData.get("transAmount"));
				AssignId idCreator = new AssignId();
				double newCurrencyAmount = amount;
				// The purpose of the following two parts is that if the exchange rate is 1
				// JSONObject returns it as long while if it is not 1 it returns as double
				// To stop the errors due to parsing long to double I have implemented the
				// following parts
				try {
					try {
						newCurrencyAmount = newCurrencyAmount / ((double) rates.get(selectedFromValue));
					} catch (Exception e) {
					}
					newCurrencyAmount = newCurrencyAmount * ((double) rates.get(selectedToValue));
				} catch (Exception e) {
				}

				transactionMessage = "Your transaction is completed for " + df.format(amount) + " " + selectedFromValue
						+ " to " + df.format(newCurrencyAmount) + " " + selectedToValue + " with the transaction ID: "
						+ idCreator.getId();

				// If conversion is valid I put the data on the database
				Map<String, Object> docData = new HashMap<>();
				Date date = new Date();
				docData.put("from", selectedFromValue);
				docData.put("to", selectedToValue);
				docData.put("amount", newCurrencyAmount);

				db.collection(date.getDate()).document(idCreator.getId()).set(docData);

			} catch (Exception e) {
				transactionMessage = "Invalid Input";
			}
		}
		// example that works in firestore
		return "conversion";
	}

	static {
		// I call the functions that I have created and fill the lists required for this
		// page
		ExchangeListCreator listCreator = new ExchangeListCreator();
		fromList = listCreator.getExchangeList();
		;
		toList = listCreator.getExchangeList();
		rates = listCreator.getRates();

		try {
			fbs = new FireBaseService();
			db = fbs.getDb();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@GetMapping(path = "/conversion")
	public String conversion(Model model) {
		model.addAttribute("fromList", fromList);
		model.addAttribute("toList", toList);
		model.addAttribute("transactionMessage", transactionMessage);
		model.addAttribute("initialToSelection", selectedToValue);
		model.addAttribute("initialFromSelection", selectedFromValue);
		return "conversion";
	}
}
