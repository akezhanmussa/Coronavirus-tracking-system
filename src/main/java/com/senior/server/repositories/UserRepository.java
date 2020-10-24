package com.senior.server.repositories;

import com.senior.server.domain.User;

import java.util.List;

public interface UserRepository {
    User getUserWithId(String id);
    List<User> getAllUsers();
}
