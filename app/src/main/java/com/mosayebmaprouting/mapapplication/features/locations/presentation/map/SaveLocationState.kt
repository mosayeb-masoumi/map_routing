package com.mosayebmaprouting.mapapplication.features.locations.presentation.map

import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel

data class SaveLocationState(

    val isLoading: Boolean = false,
    val data: LocationModel? = null,
    val error: String = ""
)
