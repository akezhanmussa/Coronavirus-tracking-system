package com.senior.server.repositories;

import com.senior.server.domain.Location;
import com.senior.server.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserRepository{
    User getUserWithId(String id);
    List<User> getAllUsers();
    List<User> getByLocation(Location location, Boolean isPositive);
    boolean setPersonToBeInfectedWithId(String id);
}
