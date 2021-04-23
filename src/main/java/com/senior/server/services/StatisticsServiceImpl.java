package com.senior.server.services;

import com.senior.server.configurations.StatisticsConfiguration;
import com.senior.server.domain.CovidCases;
import com.senior.server.domain.Location;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@Service
public class StatisticsServiceImpl implements StatisticsService{
    private Document document;
    private StatisticsConfiguration statisticsConfiguration;
    private Map<String, String> cityMapper;
    private static final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Autowired
    public void setStatisticsConfiguration(StatisticsConfiguration statisticsConfiguration) {
        this.statisticsConfiguration = statisticsConfiguration;
    }

    @PostConstruct
    public void init() {
        cityMapper = new HashMap<>();
        cityMapper.put("Нур-Султан", "Astana");
        cityMapper.put("Алматы", "Almaty");

        try {
            this.document = Jsoup.connect(this.statisticsConfiguration.getUrlSource()).get();
        } catch (java.io.IOException ioException) {
            this.document = null;
        }
    }

    @Override
    public CovidCases retrieveCovidCases(Location location) {
        if (this.document == null) {
            logger.warn("Could not load document from " + this.statisticsConfiguration.getUrlSource());
            return null;
        } else {
            CovidCases result = new CovidCases();

            Elements elements = this.document.select("div.table_info_cont").first().select("tbody").select("tr");
            Integer headerNumToSkip = 3;
            Integer cityLimit = 2;
            Integer index = 0;
            String message = elements.last().select("td").get(3).text();

            for (Element rowElement: elements) {
                if (index >= headerNumToSkip + cityLimit) {
                    break;
                }
                if (index >= headerNumToSkip) {
                    Elements colElements = rowElement.select("td");
                    Integer iterIndex = 0;
                    String cityName = colElements.get(iterIndex++).text().substring(3);

                    if (location.getCity().equals(cityMapper.get(cityName))) {
                        Integer infectedPlus = Integer.parseInt(colElements.get(iterIndex++).text());
                        Integer healthyPlus = Integer.parseInt(colElements.get(iterIndex++).text());
                        Integer diedPlus = !colElements.get(iterIndex).text().equals("-") ? Integer.parseInt(colElements.get(iterIndex).text()) : 0;
                        iterIndex+=2;
                        Integer infectedNeg = !colElements.get(iterIndex).text().equals("-") ? Integer.parseInt(colElements.get(iterIndex).text()) : 0;
                        iterIndex+=1;
                        Integer healthyNeg = !colElements.get(iterIndex).text().equals("-") ? Integer.parseInt(colElements.get(iterIndex).text()) : 0;
                        iterIndex+=1;
                        Integer diedNeg = !colElements.get(iterIndex).text().equals("-") ? Integer.parseInt(colElements.get(iterIndex).text()) : 0;

                        result.setGotSickNumPCRplus(infectedPlus);
                        result.setRecoveredNumPCRplus(healthyPlus);
                        result.setDiedNumPCRplus(diedPlus);

                        result.setGotSickNumPCRminus(infectedNeg);
                        result.setRecoveredNumPCRminus(healthyNeg);
                        result.setDiedNumPCRminus(diedNeg);

                        result.setMessage(message);

                        return result;
                    }
                }
                index += 1;
            }
            logger.info("No found statistics for " + location.toString());
            return null;
        }
    }
}
