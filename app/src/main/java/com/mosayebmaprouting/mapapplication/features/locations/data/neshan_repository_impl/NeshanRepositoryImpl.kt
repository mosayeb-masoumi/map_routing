package com.mosayebmaprouting.mapapplication.features.locations.data.neshan_repository_impl

import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.NeshanService
import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.dto.MyNeshanAddressDto
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.NeshanAddress
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.NeshanRepository
import org.neshan.common.model.LatLng
import org.neshan.common.network.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class NeshanRepositoryImpl @Inject constructor(private val api: NeshanService) : NeshanRepository {

    override suspend fun getNeshanAddress(loc: LatLng): MyNeshanAddressDto {
        return api.getAddress(loc.latitude , loc.longitude)
    }


}