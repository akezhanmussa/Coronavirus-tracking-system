package com.senior.server.services;

import com.senior.server.domain.Coordinate;
import com.senior.server.domain.HotSpot;

import java.util.List;

public interface HotSpotModificationService {
    public HotSpot addCase(Double latitude, Double longitude);
    public List<HotSpot> getHotSpots();
    public void clearHotSpots();
}
