package com.senior.server.controllers;

import com.senior.server.domain.Location;
import com.senior.server.domain.User;
import com.senior.server.services.DataFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        return new ResponseEntity<List<String>>(positiveList, HttpStatus.OK);
    }

    @RequestMapping(path = "get-all-positive-by-location", method = RequestMethod.POST)
    public ResponseEntity<?> getAllPositiveByLocation(@RequestBody Location location) {
        List<User> positiveList = dataFilterService.givePositiveInfectedPersonListByLocation(location);
        return new ResponseEntity<List<User>>(positiveList, HttpStatus.OK);
    }

}