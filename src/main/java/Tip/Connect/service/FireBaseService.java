
package Tip.Connect.service;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
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

    public String uploadImageToStorge(MultipartFile file, String fileName) throws FileNotFoundException,IOException {
        StorageClient storageClient = StorageClient.getInstance();
        InputStream testFile = new FileInputStream(convertToFile(file));
        String blobString = "UserArea/urlPic/avatar/" + fileName;
        Bucket bucket = storageClient.bucket();
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucket.getName(), blobString)).setContentType("image/jpeg").build();
        blobInfo = bucket.getStorage().create(blobInfo,testFile);
        String URL = "https://firebasestorage.googleapis.com/v0/b/"+blobInfo.getBucket()+"/o/"+blobInfo.getName()+"?alt=media";
        return URL;
    }

    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = new File("test.jpeg");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        return tempFile;
    }



}
