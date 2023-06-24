package com.mosayebmaprouting.mapapplication.features.locations.data.locations_repository_impl

import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.LocationDao
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class LocationRepositoryImpl(private val dio:LocationDao) : LocationRepository {

    override fun getLocations(): Flow<List<LocationModel>> {
        return dio.getLocations()
    }

    override suspend fun getLocationById(id: Int): LocationModel? {
        return dio.getLocationById(id)
    }

    override suspend fun addLocation(locationModel: LocationModel) {
        return dio.addLocation(locationModel)
    }

    override suspend fun deleteLocation(locationModel: LocationModel) {
       return dio.deleteLocation(locationModel)
    }

    override suspend fun clearDB() {
       return dio.clearDB()
    }
}