package com.example.OzanCaseStudy.Controllers;

import java.util.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.OzanCaseStudy.Models.ExchangeListCreator;

@Controller
public class ExchangeRateController {
	//Application runs on default port with current settings(8080), http://localhost:8080/exchange-rate

	static List<String> fromList = null;
	static List<String> toList = null;
	static String rateMessage = "Please Select the Currency";
	static JSONObject rates = null;
	static String selectedToExchange = "USD";
	static String selectedFromExchange = "EUR";

	@PostMapping("/new-rate-function") 
	@ResponseBody
	public String newRateFunction(@RequestBody Map<String, String> requestData, Model model) {
		// I get the data from the html
		String toSelectedValue = requestData.get("toSelectedValue");
		String fromSelectedValue = requestData.get("fromSelectedValue");

		// I get the rates from the lists that I have created and edit the rate message
		rateMessage = fromSelectedValue;
		double newAmount = 1;
		// Similar to the part in ConversionController if the rate is 1 it returns as
		// long and parsing long to double gives an error, to prevent that I have
		// implemented the following strategy
		try {
			try {
				newAmount = (double) rates.get(fromSelectedValue);
			} catch (Exception e) {
			}
			newAmount = ((double) rates.get(toSelectedValue)) / newAmount;

		} catch (Exception e) {
		}
		String text = fromSelectedValue + " to " + toSelectedValue + " exchange rate is: "
				+ String.format("%.3f", newAmount);
		rateMessage = text;
		selectedToExchange = toSelectedValue;
		selectedFromExchange = fromSelectedValue;
		return "exchange-rate";
	}

	static {
		// I create the lists with the usage of the functions that I have build
		ExchangeListCreator listCreator = new ExchangeListCreator();
		fromList = listCreator.getExchangeList();
		toList = listCreator.getExchangeList();
		rates = listCreator.getRates();
	}

	@GetMapping(path = "/exchange-rate")
	public String getExchangeRate(Model model) {
		model.addAttribute("fromList", fromList);
		model.addAttribute("toList", toList);
		model.addAttribute("rateMessage", rateMessage);
		model.addAttribute("initialToSelection", selectedToExchange);
		model.addAttribute("initialFromSelection", selectedFromExchange);
		return "exchange-rate";
	}
}
