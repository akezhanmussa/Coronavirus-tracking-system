package com.senior.server.controllers;

import com.senior.server.domain.CovidCases;
import com.senior.server.domain.Location;
import com.senior.server.domain.User;
import com.senior.server.services.StatisticsService;
import com.senior.server.services.StatisticsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(path = "/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @RequestMapping(path = "covid-cases-by-location", method = RequestMethod.POST)
    public ResponseEntity<?> getAllPositiveByLocation(@RequestBody Location location) {
        CovidCases covidCases = statisticsService.retrieveCovidCases(location);
        return new ResponseEntity(covidCases, HttpStatus.OK);
    }
}
