package com.mosayebmap.mapapplication.features.locations.domain.repository

import com.mosayebmap.mapapplication.features.locations.domain.model.LocationModel
import kotlinx.coroutines.flow.Flow

interface LocationRepository {


    fun getLocations(): Flow<List<LocationModel>>
    suspend fun getLocationById(id: Int): LocationModel?
    suspend fun addLocation(locationModel: LocationModel)
    suspend fun deleteLocation(locationModel: LocationModel)
    suspend fun clearDB()
}