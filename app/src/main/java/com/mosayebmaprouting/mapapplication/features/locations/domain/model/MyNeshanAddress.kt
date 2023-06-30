package com.mosayebmaprouting.mapapplication.features.locations.domain.model

import com.google.gson.annotations.SerializedName

data class MyNeshanAddress(
    var neighbourhood: String? = null,
    var address: String? = null,
    var municipality_zone: String? = null,
    var in_traffic_zone: Boolean? = null,
    var in_odd_even_zone: Boolean? = null,
    var city: String? = null,
    var state: String? = null,
    var status: String? = null,
    var code: Int? = null,
    var message: String? = null,
)