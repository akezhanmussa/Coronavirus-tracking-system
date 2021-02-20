package com.senior.server;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;


@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(ServerApplication.class, args);
        String filePath = "src/main/resources/covidtracerapp-9dad0-firebase-adminsdk-uv88d-9ce746e808.json";

        try {
            FileInputStream serviceAccount = new FileInputStream(filePath);
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder().
                    setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
            if(FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(firebaseOptions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
