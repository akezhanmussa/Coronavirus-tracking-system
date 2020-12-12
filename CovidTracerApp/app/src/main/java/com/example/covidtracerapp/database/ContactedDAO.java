package com.example.covidtracerapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

@Dao
public interface ContactedDAO {
    @Insert
    void insertContacted(ContactedEntity contactedEntity);
    @Delete
    void deleteContacted(ContactedEntity contactedEntity);
}
