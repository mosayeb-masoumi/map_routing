package com.mosayebmaprouting.mapapplication.features.locations.domain.use_case

import com.mosayebmaprouting.mapapplication.core.Resource
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.NeshanDirectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.neshan.mapsdk.model.Marker
import org.neshan.servicessdk.direction.model.NeshanDirectionResult
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NeshanDirectionUseCase @Inject constructor(private val repository: NeshanDirectionRepository) {


    operator fun invoke(
        firstMarker: Marker,
        secondMarker: Marker
    ): Flow<Resource<NeshanDirectionResult?>> = flow {

        try {
            emit(Resource.Loading<NeshanDirectionResult?>())
            val result = repository.getNeshanDirection(firstMarker, secondMarker)
            emit(Resource.Success<NeshanDirectionResult?>(result))
        } catch (e: IOException) {
            emit(
                Resource.Error<NeshanDirectionResult?>(
                    e.localizedMessage ?: "check your internet connection!!", null
                )
            )
        } catch (e: HttpException) {
            emit(
                Resource.Error<NeshanDirectionResult?>(
                    e.localizedMessage ?: "an error occured!!",
                    null
                )
            )

        }

    }
}
