package com.example.covidtracerapp.database;

import android.content.Context;
import android.util.Log;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {ContactedEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class ContactedDatabase extends RoomDatabase {

    public abstract ContactedDAO contactedDAO();

    private static final String DB_NAME = "contacted.db";
    private static final String LOG_TAG = ContactedDatabase.class.getSimpleName();
    private static ContactedDatabase sInstance;

    public static ContactedDatabase getsInstance(Context context) {
        if (sInstance == null){
            synchronized (ContactedDatabase.class){
                if (sInstance == null) {
                    Log.d(LOG_TAG, "Creating a new database instance");
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), ContactedDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }
}
