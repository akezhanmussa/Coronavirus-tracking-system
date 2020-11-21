package com.senior.server.services;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.senior.server.domain.Location;
import com.senior.server.domain.User;
import com.senior.server.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DataFilterServiceImpl implements DataFilterService{

    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataFilterServiceImpl.class);
    private LoadingCache<Location, Set<User>> cacheOnInfectedPersonsByLocation;

    @PostConstruct
    public void init() {
        this.cacheOnInfectedPersonsByLocation = Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats()
                .build(location -> new HashSet(userRepository.getByLocation(location, true)));
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean setPersonToBeInfected(String id) {
        boolean wasUpdated = userRepository.setPersonToBeInfectedWithId(id);
        return wasUpdated;
    }

    @Override
    public List<String> givePositiveInfectedPersonList() {
        List<String> infectedPersonList = new ArrayList();
        List<User> allUsers = userRepository.getAllUsers();
        for (User user: allUsers) {
            if (user.isPositive()) {
                infectedPersonList.add(user.getId());
            }
        }
        return infectedPersonList;
    }


    @Override
    public Set<User> givePositiveInfectedPersonSetByLocation(Location location) {
        logger.info("GOT LOCATION : " + "COUNTRY - " + location.getCountry() + " CITY - " + location.getCity());
        return this.cacheOnInfectedPersonsByLocation.get(location);
    }

    @Override
    public List<User> findIntersectionWithInfectedList(Location location, List<String> idList) {
        logger.info("REQUESTED LIST: " + idList);
        Set<User> positiveInfectedList = givePositiveInfectedPersonSetByLocation(location);
        List<User> intersectionList = new ArrayList();
        Set<String> idSet = new HashSet(idList);
        logger.info("INFECTED: " + positiveInfectedList);
        for (String id: idSet) {
            for (User positiveInfected: positiveInfectedList) {
                if (positiveInfected.getId().equals(id)) {
                    intersectionList.add(positiveInfected);
                    break;
                }
            }
        }
        logger.info("INTERSECTION LIST: " + intersectionList);
        return intersectionList;
    }
}
