
package com.mosayebmaprouting.mapapplication.features.locations.data.data_source

import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.dto.MyNeshanAddressDto
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.NeshanAddress
import org.neshan.servicessdk.direction.model.NeshanDirectionResult
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface NeshanService {
    // TODO: replace "YOUR_API_KEY" with your api key
    @Headers("Api-Key: service.kREahwU7lND32ygT9ZgPFXbwjzzKukdObRZsnUAJ")
    @GET("/v2/reverse")
    fun getReverse(@Query("lat") lat: Double?, @Query("lng") lng: Double?): Call<NeshanAddress>

    @Headers("Api-Key: service.kREahwU7lND32ygT9ZgPFXbwjzzKukdObRZsnUAJ")
    @GET("/v2/reverse")
    suspend fun getAddress(@Query("lat") lat: Double?, @Query("lng") lng: Double?): MyNeshanAddressDto


}