package com.mosayebmaprouting.mapapplication.features.locations.presentation.locations

import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel

data class GetLocationsState(
    val isLoading:Boolean = false,
    val data:List<LocationModel?> = emptyList(),
    val error:String =""
)
