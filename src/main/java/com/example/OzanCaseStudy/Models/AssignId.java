package com.example.OzanCaseStudy.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

public class AssignId {

	org.slf4j.Logger logger = LoggerFactory.getLogger(AssignId.class);
	static Firestore db = FireBaseService.getDb();
	String id = "";
	int idLength = 8;

	public AssignId() {
		this.id = idChecker();

	}

	public String getId() {
		return id;

	}

	private String createId() {
		// This function creates random string id with the length that is assigned above
		// Only uppercase letters and numbers are used for id
		String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
		StringBuilder sb = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < idLength; i++) {
			int index = random.nextInt(alphaNumeric.length());
			char randomChar = alphaNumeric.charAt(index);
			sb.append(randomChar);
		}
		return sb.toString();

	}

	private String idChecker() {
		// This function checks if the created id by the previous function is existing
		// id in the database for another transaction
		// If it exists it creates another id with the same function and checks it again
		String tempId = createId();
		Iterable<CollectionReference> collections = db.getCollections();
		for (CollectionReference collRef : collections) {
			ApiFuture<QuerySnapshot> future = collRef.get();
			try {
				List<QueryDocumentSnapshot> documents = future.get().getDocuments();
				for (QueryDocumentSnapshot document : documents) {
					if (document.getId().equals(tempId)) {
						tempId = idChecker();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("DOCUMENTS COULDN'T BE FETCHED FROM THE SERVER");
			}
		}
		return tempId;
	}

}
