package com.senior.server.controllers;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.senior.server.domain.Coordinate;
import com.senior.server.domain.HotSpot;
import com.senior.server.domain.Location;
import com.senior.server.domain.User;
import com.senior.server.services.DataFilterService;
import com.senior.server.services.HotSpotModificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping(path = "/data-api")
public class DataRequestController {

    private static final Logger logger = LoggerFactory.getLogger(DataRequestController.class);
    private DataFilterService dataFilterService;
    private HotSpotModificationService hotSpotModificationService;

    @PostConstruct
    public void initialize() {
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

    @Autowired
    public void setDataFilterService(DataFilterService dataFilterService) {
        this.dataFilterService = dataFilterService;
    }

    @Autowired
    public void setHotSpotModificationService(HotSpotModificationService hotSpotModificationService) {
        this.hotSpotModificationService = hotSpotModificationService;
    }

    @RequestMapping(path = "get-all-positive", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPositive() {
        List<String> positiveList = dataFilterService.givePositiveInfectedPersonList();
        return new ResponseEntity(positiveList, HttpStatus.OK);
    }

    @RequestMapping(path = "get-all-positive-by-location", method = RequestMethod.POST)
    public ResponseEntity<?> getAllPositiveByLocation(@RequestBody Location location) {
        Set<User> positiveList = dataFilterService.givePositiveInfectedPersonSetByLocation(location);
        return new ResponseEntity(positiveList, HttpStatus.OK);
    }

    @RequestMapping(path = "set-to-be-infected", method = RequestMethod.POST)
    public ResponseEntity<?> setToBeInfectedWithId(@RequestBody Map<String, String> body) throws FirebaseMessagingException {
        String id = body.getOrDefault("id", "");
        boolean  wasUpdated = dataFilterService.setPersonToBeInfected(id);
        Map<String, String> response = new HashMap();
        if (wasUpdated) {
            response.put("status", id + " was updated");
        } else {
            response.put("status", id + " was not updated  since it does not exist in DB");
        }
        String topic = "all_devices";

        Message message = Message.builder()
                .putData("id", id)
                .setTopic(topic)
                .build();

        try {
            String messageResponse = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent message: " + messageResponse);
        } catch(FirebaseMessagingException exception) {
            logger.info(exception.getMessage());
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(path = "check-positive-with-my-list", method = RequestMethod.POST)
    public ResponseEntity<?> checkPositiveWithList(
            @RequestBody List<String> users,
            @RequestParam String city,
            @RequestParam String country) {
        List<User> intersectionList = dataFilterService.findIntersectionWithInfectedList(new Location(country, city), users);
        return new ResponseEntity(intersectionList, HttpStatus.OK);
    }

    @RequestMapping(path = "hotspots", method = RequestMethod.GET)
    public ResponseEntity<?> getHotSpots(
            Location location,
            Integer limit) {
        List<Coordinate> coordinates = dataFilterService.getPlacesByLocation(location, limit);
        return new ResponseEntity(coordinates, HttpStatus.OK);
    }

    @RequestMapping(path = "hotspotsV2", method = RequestMethod.GET)
    public ResponseEntity<?> getHotSpotsV2(
            Location location,
            Integer limit) {
        List<HotSpot> coordinates = hotSpotModificationService.getHotSpots();
        return new ResponseEntity(coordinates, HttpStatus.OK);
    }

    @RequestMapping(path = "hotspotsV2-clear", method = RequestMethod.POST)
    public ResponseEntity<?> clearHotSpotsV2() {
        hotSpotModificationService.clearHotSpots();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(path = "new-case", method = RequestMethod.POST)
    public ResponseEntity<?> addNewCase(
            @RequestBody Map<String, Double> body) {
        Map<String, Object> response = new HashMap();
        Double latitude = body.get("latitude");
        Double longitude = body.get("longitude");
        HotSpot hotSpot = hotSpotModificationService.addCase(latitude, longitude);
        logger.info("latitude " + latitude);
        logger.info("longitude " + longitude);
        if (hotSpot == null) {
            logger.info("Hotspot was not found");
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
