package com.mosayebmaprouting.mapapplication.features.locations.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locationModel")
    fun getLocations(): Flow<List<LocationModel>>

    @Query("SELECT * FROM locationModel WHERE id= :id")
    suspend fun getLocationById(id: Int): LocationModel?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocation(locationModel: LocationModel)

    @Delete
    suspend fun deleteLocation(locationModel: LocationModel)


    @Query("DELETE FROM locationModel")    // to clear all db
    suspend fun clearDB()
}