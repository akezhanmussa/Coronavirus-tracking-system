package com.example.covidtracerapp.database

import android.media.Image
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ContactedEntity::class], version = 1, exportSchema = false
)
@TypeConverters(
    DateConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactedDao(): ContactedDAO
}