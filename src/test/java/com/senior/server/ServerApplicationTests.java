package com.senior.server;

import com.senior.server.domain.CovidCases;
import com.senior.server.domain.HotSpot;
import com.senior.server.domain.HotSpots;
import com.senior.server.domain.LineSpace;
import com.senior.server.domain.Location;
import com.senior.server.services.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.senior.server.services.HotSpotModificationService;

import java.util.List;
import java.util.Objects;

@SpringBootTest
class ServerApplicationTests {

    @Autowired
    public HotSpotModificationService hotSpotModificationService;
    @Autowired
    public StatisticsService statisticsService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testLineSpaceGetIndexFor() {
        Double yUp = 51.227157;
        Double xBot = 71.297808;
        Double xUp = 71.592204;
        Double yBot = 51.024901;
        LineSpace lineSpace = new LineSpace(116.17944444, 116.58888889, 18);
        lineSpace.process();
        Integer result = lineSpace.getIndexFor(116.17944445);
    }

    @Test
    public void testHotSpotModificationService() {
        HotSpot hotSpot = hotSpotModificationService.addCase(51.1666679, 71.4333344);
        List<HotSpot> coordinates = hotSpotModificationService.getHotSpots();
        for (HotSpot coordiate: coordinates) {
            if (coordiate.getRadius() > 0) {
                assert Objects.equals(coordiate.getRadius(), 20);
            }
        }
        hotSpotModificationService.clearHotSpots();
        List<HotSpot> hotSpots = hotSpotModificationService.getHotSpots();
    }

    @Test
    public void testStatisticsService() {
        CovidCases covidCases = this.statisticsService.retrieveCovidCases(new Location("Kazakhstan", "Almaty"));
        System.out.println(covidCases);
    }
}
