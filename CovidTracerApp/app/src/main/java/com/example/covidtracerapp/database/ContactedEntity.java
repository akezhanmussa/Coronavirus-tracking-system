package com.example.covidtracerapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "contacted")
public class ContactedEntity {

    @PrimaryKey
    private String  id;
    private Date    contactedAt;

    public ContactedEntity(String id, Date contactedAt) {
        this.id = id;
        this.contactedAt = contactedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getContactedAt() {
        return contactedAt;
    }

    public void setContactedAt(Date contactedAt) {
        this.contactedAt = contactedAt;
    }
}
