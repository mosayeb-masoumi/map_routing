package com.mosayebmaprouting.mapapplication.features.locations.domain.use_case

import com.mosayebmaprouting.mapapplication.core.Resource
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class DeleteLocationUseCase(private val repository: LocationRepository) {

    operator fun invoke(location: LocationModel): Flow<Resource<LocationModel?>> = flow {

        try {
            repository.deleteLocation(location)
            emit(Resource.Success<LocationModel?>(location))
        }catch (e: IOException){
            emit(Resource.Error<LocationModel?>(e.localizedMessage ?: "check your internet connection", null))
        }
    }
}
