package com.senior.server.controllers;

import com.senior.server.domain.Location;
import com.senior.server.domain.User;
import com.senior.server.services.DataFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(path = "/data-api")
public class DataRequestController {

    private DataFilterService dataFilterService;

    @Autowired
    public void setDataFilterService(DataFilterService dataFilterService) {
        this.dataFilterService = dataFilterService;
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
    public ResponseEntity<?> setToBeInfectedWithId(@RequestBody Map<String, String> body) {
        String id = body.getOrDefault("id", "");
        boolean  wasUpdated = dataFilterService.setPersonToBeInfected(id);
        Map<String, String> response = new HashMap();
        if (wasUpdated) {
            response.put("status", id + " was updated");
        } else {
            response.put("status", id + " was not updated  since it does not exist in DB");
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

}