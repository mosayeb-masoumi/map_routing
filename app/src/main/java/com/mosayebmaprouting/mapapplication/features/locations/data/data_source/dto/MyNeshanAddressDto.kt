package com.mosayebmaprouting.mapapplication.features.locations.data.data_source.dto

import com.google.gson.annotations.SerializedName
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.MyNeshanAddress

data class MyNeshanAddressDto(
    @SerializedName("neighbourhood")
    var neighbourhood: String? = null,
    @SerializedName("formatted_address")
    var address: String? = null,
    @SerializedName("municipality_zone")
    var municipality_zone: String? = null,
    @SerializedName("in_traffic_zone")
    var in_traffic_zone: Boolean? = null,
    @SerializedName("in_odd_even_zone")
    var in_odd_even_zone: Boolean? = null,
    @SerializedName("city")
    var city: String? = null,
    @SerializedName("state")
    var state: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("code")
    var code: Int? = null,
    @SerializedName("message")
    var message: String? = null,
)

fun MyNeshanAddressDto.toMyNeshanAddress(): MyNeshanAddress {
    return MyNeshanAddress(
       neighbourhood = neighbourhood,
        address = address,
        municipality_zone = municipality_zone,
        in_traffic_zone = in_traffic_zone,
        in_odd_even_zone = in_odd_even_zone,
        city = city ,
        state = state ,
        status = status,
        code = code ,
        message = message
    )
}
