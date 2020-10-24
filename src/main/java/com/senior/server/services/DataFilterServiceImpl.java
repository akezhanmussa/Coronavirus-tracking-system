package com.senior.server.services;
import com.senior.server.domain.User;
import com.senior.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataFilterServiceImpl implements DataFilterService{

    private UserRepository userRepository;

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
}
