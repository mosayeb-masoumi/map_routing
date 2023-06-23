package com.mosayebmap.mapapplication.features.locations.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationModel(
    @ColumnInfo(name = "lat") var lat: String,
    @ColumnInfo(name = "lng") var lng: String,
    @ColumnInfo(name = "address") var address: String,

){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null  // important to equal null  for working increment
}

class InvalidNoteException(message: String) : Exception(message)

