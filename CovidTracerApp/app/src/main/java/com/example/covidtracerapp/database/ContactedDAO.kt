package com.example.covidtracerapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactedDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacted(contactedEntity: ContactedEntity?)

    @Delete
    suspend fun deleteContacted(contactedEntity: ContactedEntity?)

    @Query("SELECT * FROM $ContactedTable")
    suspend fun getAllContacted() : List<ContactedEntity>

    @Query("SELECT id FROM $ContactedTable")
    suspend fun getAllContactedIds() : List<String>
}