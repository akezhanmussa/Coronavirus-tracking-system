package com.senior.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="users")
public class User {

    @Id
    private String id;
    private String phone;
    private boolean isPositive;
    private Date datePositive;
    private String country;
    private String city;


    public User(String phone, boolean isPositive, Date datePositive, String country, String city) {
        this.phone = phone;
        this.isPositive = isPositive;
        this.datePositive = datePositive;
        this.country = country;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    public Date getDatePositive() {
        return datePositive;
    }

    public void setDatePositive(Date datePositive) {
        this.datePositive = datePositive;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", isPositive=" + isPositive +
                ", datePositive=" + datePositive +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
