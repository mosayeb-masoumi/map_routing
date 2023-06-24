package com.mosayebmaprouting.mapapplication.features.locations.domain.use_case

import com.mosayebmaprouting.mapapplication.core.Resource
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class GetLocationsUseCase(private val repository: LocationRepository) {

    // because we used flow in getlist in dao then suspend is redundant
    operator fun invoke(): Flow<List<LocationModel>> {
        return repository.getLocations()
    }
}
