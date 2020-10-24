package com.senior.server.repositories;

import com.senior.server.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRepositoryImpl implements UserRepository{

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
}
