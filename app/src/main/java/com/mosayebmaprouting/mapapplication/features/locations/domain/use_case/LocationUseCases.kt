package com.mosayebmaprouting.mapapplication.features.locations.domain.use_case

data class LocationUseCases(
    val addLocationUseCase: AddLocationUseCase,
    val clearDbUseCase: ClearDbUseCase,
    val deleteLocationUseCase: DeleteLocationUseCase,
    val getLocationsUseCase: GetLocationsUseCase,
    val getLocationUseCase: GetLocationUseCase
)
