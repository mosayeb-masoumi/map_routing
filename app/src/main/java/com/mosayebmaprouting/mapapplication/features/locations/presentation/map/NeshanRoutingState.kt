package com.mosayebmaprouting.mapapplication.features.locations.presentation.map

import org.neshan.servicessdk.direction.model.NeshanDirectionResult

data class NeshanDirectionState(
    val isLoading: Boolean = false,
    val data: NeshanDirectionResult? = null,
    val error: String = ""
)