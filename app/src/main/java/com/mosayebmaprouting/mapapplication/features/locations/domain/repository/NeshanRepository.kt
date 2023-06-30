package com.mosayebmaprouting.mapapplication.features.locations.domain.repository

import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.dto.MyNeshanAddressDto
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.NeshanAddress
import org.neshan.common.model.LatLng
import retrofit2.Call

interface NeshanRepository {
    suspend fun getNeshanAddress(loc: LatLng): MyNeshanAddressDto
}