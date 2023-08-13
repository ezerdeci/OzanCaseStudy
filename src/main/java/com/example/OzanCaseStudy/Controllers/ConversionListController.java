package com.example.OzanCaseStudy.Controllers;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;
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

import com.example.OzanCaseStudy.Models.AssignId;
import com.example.OzanCaseStudy.Models.FireBaseService;
import com.example.OzanCaseStudy.Models.Transaction;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

@Controller
public class ConversionListController {
	//Application runs on default port with current settings(8080), http://localhost:8080/conversion-list

	static Firestore db = null;
	static FireBaseService fbs = null;
	static String searchOutput = "";
	static List<Transaction> transactions = null;
	org.slf4j.Logger logger = LoggerFactory.getLogger(ConversionListController.class);

	@PostMapping("/search-transaction")
	@ResponseBody
	public void getExchange(@RequestBody Map<String, String> requestData, Model model) {
		// Firstly I get the inputs from the html page and create transactions list that
		// will be printed on the page
		String date = requestData.get("date");
		String transactionId = requestData.get("transactionId");
		transactions = new ArrayList<Transaction>();

		// Since there is 3 different cases for inputs I have used if else structure for
		// them
		if (date == "") {
			if (transactionId == "") {
				// If both inputs are empty system prints wrong input
				System.out.println("No input given");
			} else {
				// If only transaction id is given code gets every collection from the data base
				// and checks which date has the transaction with the given id
				Iterable<CollectionReference> collections = db.getCollections();
				// Iterates over every collection
				for (CollectionReference collRef : collections) {
					ApiFuture<QuerySnapshot> future = collRef.get();
					try {
						List<QueryDocumentSnapshot> documents = future.get().getDocuments();
						// Iterates over every document in the collection
						for (QueryDocumentSnapshot document : documents) {
							if (document.getId().equals(transactionId)) {
								// If a document exists with the given id it pulls the data from the data base
								// and creates an transaction variable with the data and adds the transaction
								// into transactions list
								double tempAmount = (double) document.getData().get("amount");
								Transaction transaction = new Transaction(document.getId(),
										(String) document.getData().get("from"), (String) document.getData().get("to"),
										tempAmount);
								transactions.add(transaction);
							}
						}
					} catch (Exception e) {
						logger.error("CONNECTION TO DATABASE FAILED");
					}
				}
				System.out.println("No transaction found with the given ID in the history"); // No transaction found
																								// with the id code
			}
		} else {
			if (transactionId == "") {
				// If only date is given and id is not given collection with the date name is
				// pulled from the data base
				CollectionReference collRef = db.collection(date);
				ApiFuture<QuerySnapshot> future = collRef.get();
				try {
					List<QueryDocumentSnapshot> documents = future.get().getDocuments();
					transactions = new ArrayList<Transaction>();
					// Every document on the collection is pulled and converted in to transactin
					// class element and added into the list
					for (DocumentSnapshot document : documents) {
						double tempAmount = (double) document.getData().get("amount");
						Transaction transaction = new Transaction(document.getId(),
								(String) document.getData().get("from"), (String) document.getData().get("to"),
								tempAmount);
						transactions.add(transaction);
					}
				} catch (Exception e) {
					logger.error("CONNECTION TO DATABASE FAILED");
				}
				if (transactions.size() == 0) {
					System.out.println("No transaction exists for this date or invalid date input");
				}

			} else {
				// If both date and id is given i try to pull it directly with the date and id
				// using the following line
				// If it is successfull it creates the transaction class element and adds it
				// into the list which will be displayed
				DocumentReference docRef = db.collection(date).document(transactionId);
				ApiFuture<DocumentSnapshot> future = docRef.get();
				DocumentSnapshot document;
				try {
					document = future.get();
					if (document.exists()) {
						transactions = new ArrayList<Transaction>();
						double tempAmount = (double) document.getData().get("amount");
						Transaction transaction = new Transaction(document.getId(),
								(String) document.getData().get("from"), (String) document.getData().get("to"),
								tempAmount);
						transactions.add(transaction);
					} else {
						// If no transaction with that id exists in that date following line is printed
						System.out.println("No transaction exists with the given id for the given date");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("CONNECTION TO DATABASE FAILED");
				}
			}
		}

	}

	static {
		try {
			db = FireBaseService.getDb();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping(path = "/conversion-list")
	public String getConversionList(Model model) {
		model.addAttribute("searchOutput", searchOutput);
		model.addAttribute("transactions", transactions);
		return "conversion-list";
	}

}
