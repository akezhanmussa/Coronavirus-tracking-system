package com.senior.server.repositories;

import com.senior.server.domain.Location;
import com.senior.server.domain.User;
import com.senior.server.services.DataFilterServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepositoryImpl implements UserRepository{

    private static final String cityAttribute = "City";
    private static final String countryAttribute = "Country";
    private static final String isPositiveAttribute = "isPositive";

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public User getUserWithId(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        List<User> possibleCandidate = this.mongoTemplate.find(query, User.class);
        return possibleCandidate.isEmpty() ? null : possibleCandidate.get(0);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = this.mongoTemplate.findAll(User.class);
        return allUsers;
    }

    @Override
    public List<User> getByLocation(Location location, Boolean isPositive) {
        if (location == null || location.getCity() == null || location.getCountry() == null) {
            logger.info("LOCATION ATTRIBUTES ARE NULL");
            return new ArrayList<>();
        };

        String country = location.getCountry();
        String city = location.getCity();

        Query query = new Query(Criteria.where(cityAttribute).is(city).and(countryAttribute).is(country).and(isPositiveAttribute).is(isPositive));
        List<User> candidates = this.mongoTemplate.find(query, User.class);
        if (candidates.isEmpty())
            logger.info("USER CANDIDATES WERE NOT FOUND");
        else
            logger.info("USER CANDIDATES WERE FOUND");

        return candidates.isEmpty() ? new ArrayList() : candidates;
    }
}
