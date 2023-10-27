package Tip.Connect.service;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public class FireBaseService {

    private final Firestore dbFirestore = FirestoreClient.getFirestore();

    public String getUser(String uid) throws InterruptedException, ExecutionException {
        DocumentReference documentReference = dbFirestore.collection("userTip").document(uid);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        String response = "";
        if(document.exists()){
            response = document.toString();
            return response;
        }
        return null;
    }


}
