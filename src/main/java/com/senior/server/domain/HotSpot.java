package com.senior.server.domain;

public class HotSpot {
    private Double latitude;
    private Double longitude;
    private Double radius;
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

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public void setCases(Integer cases) {
        this.cases = cases;
    }

    public void increaseRadius() {
        this.cases += 1;
        this.radius += 20;
    }
}
