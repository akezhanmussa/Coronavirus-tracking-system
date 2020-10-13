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

    public User(String phone, boolean isPositive, Date datePositive) {
        this.phone = phone;
        this.isPositive = isPositive;
        this.datePositive = datePositive;
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", isPositive=" + isPositive +
                ", datePositive=" + datePositive +
                '}';
    }
}
