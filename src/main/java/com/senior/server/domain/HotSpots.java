package com.senior.server.domain;

import java.util.List;
import java.util.Map;

public class HotSpots {
    Map<String, Object> country;
    List<Map<String, Object>> cities;
    List<Map<String, Object>> places;

    public Map<String, Object> getCountry() {
        return country;
    }

    public void setCountry(Map<String, Object> country) {
        this.country = country;
    }

    public List<Map<String, Object>> getCities() {
        return cities;
    }

    public void setCities(List<Map<String, Object>> cities) {
        this.cities = cities;
    }

    public List<Map<String, Object>> getPlaces() {
        return places;
    }

    public void setPlaces(List<Map<String, Object>> places) {
        this.places = places;
    }
}
