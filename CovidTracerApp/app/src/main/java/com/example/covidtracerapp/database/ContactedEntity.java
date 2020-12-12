package com.example.covidtracerapp.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "contacted")
public class ContactedEntity {

    @PrimaryKey(autoGenerate = true)
    private int  id;
    private Date    contactedAt;
    @Ignore
    public ContactedEntity(int id, Date contactedAt) {
        this.id = id;
        this.contactedAt = contactedAt;
    }
    public ContactedEntity(Date contactedAt) {
        this.contactedAt = contactedAt;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getContactedAt() {
        return contactedAt;
    }

    public void setContactedAt(Date contactedAt) {
        this.contactedAt = contactedAt;
    }
}
