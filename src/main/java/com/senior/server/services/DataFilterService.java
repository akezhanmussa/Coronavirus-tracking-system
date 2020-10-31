package com.senior.server.services;
import com.senior.server.domain.Location;
import com.senior.server.domain.User;

import java.util.List;

public interface DataFilterService {
    List<String> givePositiveInfectedPersonList();
    List<User> givePositiveInfectedPersonListByLocation(Location location);
}
