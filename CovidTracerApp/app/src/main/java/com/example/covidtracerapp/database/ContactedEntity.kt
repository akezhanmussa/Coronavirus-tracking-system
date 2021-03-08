package com.example.covidtracerapp.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*
import kotlinx.android.parcel.Parcelize

const val ContactedTable = "contacted"
@Entity(tableName = ContactedTable)
data class ContactedEntity(@PrimaryKey val id: String, val contactedAt: Date)