package com.senior.server.services;

import com.senior.server.domain.CovidCases;
import com.senior.server.domain.Location;

public interface StatisticsService {
    public CovidCases retrieveCovidCases(Location location);
}
