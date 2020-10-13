package com.senior.server.repositories;

import com.senior.server.domain.User;

public interface UserRepository {
    User getUserWithId(String id);
}
