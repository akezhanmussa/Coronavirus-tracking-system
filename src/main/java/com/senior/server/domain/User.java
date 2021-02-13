package com.senior.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document(collection="users")
public class User {

    @Id
    private String id;
    private String phone;
    private boolean isPositive;
    private Date datePositive;
    private Location location;

    public User(String id, String phone, boolean isPositive, Date datePositive, Location location) {
        this.id = id;
        this.phone = phone;
        this.isPositive = isPositive;
        this.datePositive = datePositive;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
