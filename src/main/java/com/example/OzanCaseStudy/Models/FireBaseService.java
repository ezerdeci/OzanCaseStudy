package com.example.OzanCaseStudy.Models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseService {
	// This class makes the connection with the FireBase database and creates static
	// Firestore variable so that I can access to teh database in different parts

	static Firestore db;
	org.slf4j.Logger logger = LoggerFactory.getLogger(ExchangeListCreator.class);

	public FireBaseService() throws IOException {
		FileInputStream serviceAccount = null;
		try {
			String dir = System.getProperty("user.dir");
			dir = dir + "\\src\\main\\java\\com\\example\\OzanCaseStudy\\Models\\key.json";
			StringBuffer temp = new StringBuffer();
			for(int i = 0; i < dir.length(); i++) {
				if (dir.charAt(i) == '\\') {
					temp.append('/');
				} else {
					temp.append(dir.charAt(i));
				}
			}
			dir = temp.toString();
			serviceAccount = new FileInputStream(dir);
		} catch (Exception e) {
			logger.error("AUTHENTICATION KEY FILE IS NOT READ OR IT IS NOT VALID!!");
		}

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

		FirebaseApp.initializeApp(options);

		db = FirestoreClient.getFirestore();
	}

	public static Firestore getDb() {
		return db;
	}

}