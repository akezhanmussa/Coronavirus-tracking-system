package com.senior.server.domain;

public class HotSpot {
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private Integer cases;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public void setCases(Integer cases) {
        this.cases = cases;
    }

    public Integer getCases() {
        return cases;
    }

    public void increaseRadius() {
        this.cases += 1;
        this.radius += 20;
    }
}
