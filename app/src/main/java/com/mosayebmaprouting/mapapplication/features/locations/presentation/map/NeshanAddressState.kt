package com.mosayebmaprouting.mapapplication.features.locations.presentation.map


import com.mosayebmaprouting.mapapplication.features.locations.domain.model.MyNeshanAddress

data class NeshanAddressState(
    val isLoading: Boolean = false,
    val data: MyNeshanAddress? = null,
    val error: String = ""
)
