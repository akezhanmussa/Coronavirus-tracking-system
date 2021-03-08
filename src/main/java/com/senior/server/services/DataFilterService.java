package com.senior.server.services;
import com.senior.server.domain.Coordinate;
import com.senior.server.domain.Location;
import com.senior.server.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface DataFilterService extends UserDetailsService {
    List<String> givePositiveInfectedPersonList();
    Set<User> givePositiveInfectedPersonSetByLocation(Location location);
    List<User> findIntersectionWithInfectedList(Location location, List<String> idList);
    List<Coordinate> getPlacesByLocation(Location location, Integer limit);
    boolean setPersonToBeInfected(String id);
}
