package com.senior.server.services;
import com.senior.server.domain.Coordinate;
import com.senior.server.domain.Location;
import com.senior.server.domain.User;

import java.util.List;
import java.util.Set;

public interface DataFilterService {
    List<String> givePositiveInfectedPersonList();
    Set<User> givePositiveInfectedPersonSetByLocation(Location location);
    List<User> findIntersectionWithInfectedList(Location location, List<String> idList);
    List<Coordinate> getPlacesByLocation(Location location);
    boolean setPersonToBeInfected(String id);
}
