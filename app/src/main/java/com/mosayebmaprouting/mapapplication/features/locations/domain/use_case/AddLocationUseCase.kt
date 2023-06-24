package com.mosayebmaprouting.mapapplication.features.locations.domain.use_case

import com.mosayebmaprouting.mapapplication.core.Resource
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

data class
AddLocationUseCase(private val repository: LocationRepository) {


    operator fun invoke(location: LocationModel): Flow<Resource<LocationModel?>> = flow {

        try {
            emit(Resource.Loading<LocationModel?>())
            repository.addLocation(location)
            emit(Resource.Success<LocationModel?>(location))
        }catch (e: IOException){
            emit(Resource.Error<LocationModel?>(e.localizedMessage ?: "an error occurred!!", null))
        }
    }

}
