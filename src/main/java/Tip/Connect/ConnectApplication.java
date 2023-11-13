package Tip.Connect;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@SpringBootApplication
public class ConnectApplication {

	public static void main(String[] args) throws IOException {

		Resource resource = new ClassPathResource("tipconnect-14d4b-firebase.json");
		FileInputStream serviceAccount = new FileInputStream(resource.getFile());
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setStorageBucket("tipconnect-14d4b.appspot.com")
				.build();
		if(FirebaseApp.getApps().isEmpty()) { //<--- check with this line
			FirebaseApp.initializeApp(options);
		}

		SpringApplication.run(ConnectApplication.class, args);

	}

}
