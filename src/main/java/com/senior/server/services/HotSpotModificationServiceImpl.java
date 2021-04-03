package com.senior.server.services;

import com.senior.server.domain.HotSpot;
import com.senior.server.domain.LineSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HotSpotModificationServiceImpl implements HotSpotModificationService {
    private Integer GRID_LEN = 18;
    private HotSpot[][] grid;
    private LineSpace latitudes;
    private LineSpace longtitudes;
    private static final Logger logger = LoggerFactory.getLogger(HotSpotModificationServiceImpl.class);

    @PostConstruct
    public void init() {
        grid = new HotSpot[GRID_LEN][GRID_LEN];
        latitudes = new LineSpace(51.024901, 51.227157, GRID_LEN + 1);
        longtitudes = new LineSpace(71.297808, 71.592204, GRID_LEN + 1);
        latitudes.process();
        longtitudes.process();
        this.gridInit();
    }

    @Override
    public HotSpot addCase(Double latitude, Double longitude) {
        Integer latitudeIndex = latitudes.getIndexFor(latitude);
        Integer longitudeIndex = longtitudes.getIndexFor(longitude);
        if (latitudeIndex == -1 || longitudeIndex == -1) {
            logger.info("Case with latitude:" + latitude + ", longitude:" + longitude + " is invalid for Astana grid");
        }
        HotSpot targetHotSpot = this.grid[latitudeIndex][longitudeIndex];
        targetHotSpot.increaseRadius();
        logger.info("HotSpot with latitude index:" + latitudeIndex + ", longitude index:" + longitudeIndex + " increased its radiues");
        return targetHotSpot;
    }

    @Override
    public List<HotSpot> getHotSpots() {
        List<HotSpot> hotSpots = new ArrayList<>();
        for (HotSpot[] spots : grid) {
            for (HotSpot spot : spots) {
                Collections.addAll(hotSpots, spot);
            }
        }
        return hotSpots;
    }


    private void gridInit() {
        List<Double> latitudeMidList = latitudes.getMidList();
        List<Double> longtitudeMidList = longtitudes.getMidList();

        for (int latInd = 0; latInd < grid.length; latInd++) {
            for (int longInd = 0; longInd < grid[latInd].length; longInd++) {
                HotSpot hotSpot = new HotSpot();
                hotSpot.setLatitude(latitudeMidList.get(latInd));
                hotSpot.setLongitude(longtitudeMidList.get(longInd));
                hotSpot.setRadius(0);
                hotSpot.setCases(0);
                grid[latInd][longInd] = hotSpot;
            }
        }
    }
}
