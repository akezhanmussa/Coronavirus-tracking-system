package com.senior.server.services;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DataFilterService {
    List<String> givePositiveInfectedPersonList();
}
