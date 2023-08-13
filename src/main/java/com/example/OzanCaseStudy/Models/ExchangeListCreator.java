package com.example.OzanCaseStudy.Models;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;

public class ExchangeListCreator {

	org.slf4j.Logger logger = LoggerFactory.getLogger(ExchangeListCreator.class);
	ArrayList<String> exchangeList = new ArrayList<String>();
	JSONObject rates = null;

	public ExchangeListCreator() {
		// This class posts request to data.fixer.io to get the current exchange rates
		// and returns them as ArrayList
		// ExchangeList ArrayList only returns the name of the currencies in the api and
		// rates JSONObject returns the currencies and the conversion rates as an object
		exchangeList.add("RRRR");

		try {
			URL url = new URL("http://data.fixer.io/api/latest?access_key=8d8476049e6930d08dac237b4ee32d85");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			int responseCode = connection.getResponseCode();

			if (responseCode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responseCode);
			} else {
				StringBuilder informationString = new StringBuilder();
				Scanner scanner = new Scanner(url.openStream());

				while (scanner.hasNext()) {
					informationString.append(scanner.nextLine());
				}

				scanner.close();

				JSONParser parse = new JSONParser();
				JSONObject object = (JSONObject) parse.parse(String.valueOf(informationString));
				rates = (JSONObject) object.get("rates");

				Iterator i = rates.keySet().iterator();
				while (i.hasNext()) {
					exchangeList.add((String) i.next());
				}

				exchangeList.remove(0);
			}

		} catch (Exception e) {
			logger.error("CONNECTION TO EXCHANGE RATE SERVERS FAILED");
		}
	}

	public ArrayList<String> getExchangeList() {
		return exchangeList;
	}

	public JSONObject getRates() {
		return rates;
	}
}
