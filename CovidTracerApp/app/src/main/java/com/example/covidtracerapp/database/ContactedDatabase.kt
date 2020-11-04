package com.example.covidtracerapp.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ContactedEntity::class], version = 1, exportSchema = false)
@TypeConverters(
    DateConverter::class
)
abstract class ContactedDatabase : RoomDatabase() {
    abstract fun contactedDAO(): ContactedDAO?

    companion object {
        private const val DB_NAME = "contacted.db"
        private val LOG_TAG = ContactedDatabase::class.java.simpleName
        private var sInstance: ContactedDatabase? = null
        fun getsInstance(context: Context): ContactedDatabase? {
            if (sInstance == null) {
                synchronized(ContactedDatabase::class.java) {
                    if (sInstance == null) {
                        Log.d(
                            LOG_TAG,
                            "Creating a new database instance"
                        )
                        sInstance =
                            Room.databaseBuilder(
                                context.applicationContext,
                                ContactedDatabase::class.java,
                                DB_NAME
                            )
                                .build()
                    }
                }
            }
            Log.d(LOG_TAG, "Getting the database instance")
            return sInstance
        }
    }
}