package com.mosayebmaprouting.mapapplication.features.locations.domain.use_case

import com.mosayebmaprouting.mapapplication.core.Resource
import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.dto.MyNeshanAddressDto
import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.dto.toMyNeshanAddress
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.MyNeshanAddress
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.NeshanAddress
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.NeshanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.neshan.common.model.LatLng
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NeshanUseCase @Inject constructor(private val repository: NeshanRepository) {


    operator fun invoke(location: LatLng): Flow<Resource<MyNeshanAddress?>> = flow {

        try {
            emit(Resource.Loading<MyNeshanAddress?>())
            val result = repository.getNeshanAddress(location).toMyNeshanAddress()
            emit(Resource.Success<MyNeshanAddress?>(result))
        } catch (e: IOException) {
            emit(
                Resource.Error<MyNeshanAddress?>(
                    e.localizedMessage ?: "check your internet connection!!", null
                )
            )
        } catch (e: HttpException) {
            emit(Resource.Error<MyNeshanAddress?>(e.localizedMessage ?: "an error occured!!", null))

        }

    }


}