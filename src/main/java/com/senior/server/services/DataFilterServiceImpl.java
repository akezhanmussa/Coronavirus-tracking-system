package com.senior.server.services;
import com.senior.server.controllers.UserVerificationController;
import com.senior.server.domain.Location;
import com.senior.server.domain.User;
import com.senior.server.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataFilterServiceImpl implements DataFilterService{

    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataFilterServiceImpl.class);

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public List<User> givePositiveInfectedPersonListByLocation(Location location) {
        logger.info("GOT LOCATION : " + "COUNTRY - " + location.getCountry() + " CITY - " + location.getCity());
        List<User> userList = userRepository.getByLocation(location, true);
        return userList;
    }
}
