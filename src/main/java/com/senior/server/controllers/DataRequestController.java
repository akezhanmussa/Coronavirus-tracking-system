package com.senior.server.controllers;

import com.senior.server.domain.Location;
import com.senior.server.domain.User;
import com.senior.server.services.DataFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @RequestMapping(path = "check-positive-with-my-list", method = RequestMethod.POST)
    public ResponseEntity<?> checkPositiveWithList(
            @RequestBody List<String> users,
            @RequestParam String city,
            @RequestParam String country) {
        List<User> intersectionList = dataFilterService.findIntersectionWithInfectedList(new Location(country, city), users);
        return new ResponseEntity(intersectionList, HttpStatus.OK);
    }

}