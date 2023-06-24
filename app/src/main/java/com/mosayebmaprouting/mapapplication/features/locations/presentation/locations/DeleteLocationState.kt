package com.mosayebmaprouting.mapapplication.features.locations.presentation.locations

import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel


data class DeleteLocationState(
    val isLoading:Boolean = false,
    val data: LocationModel?=null,
    var error:String = ""
)
