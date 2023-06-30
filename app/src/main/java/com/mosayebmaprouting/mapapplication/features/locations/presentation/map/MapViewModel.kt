package com.mosayebmaprouting.mapapplication.features.locations.presentation.map


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mosayebmaprouting.mapapplication.core.Resource
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.LocationUseCases
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.NeshanDirectionUseCase
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.NeshanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.model.Marker
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val useCase: LocationUseCases,
    private val neshanUseCase: NeshanUseCase,
    private val neshanDirectionUseCase: NeshanDirectionUseCase
) : ViewModel() {


    private val _saveLocationResponse = MutableSharedFlow<SaveLocationState>()
    val saveLocationResponse = _saveLocationResponse.asSharedFlow()


    private val _getAddress = MutableSharedFlow<NeshanAddressState>()
    val getAddress = _getAddress.asSharedFlow()


    private val _getDirection = MutableStateFlow(NeshanDirectionState())
    val getDirection = _getDirection.asStateFlow()


    fun saveLocation(location: LocationModel) {

        useCase.addLocationUseCase(location).onEach {

            when (it) {

                is Resource.Loading -> {

                    _saveLocationResponse.emit(SaveLocationState(isLoading = true))
//                    _saveLocationResponse.value = SaveLocationState(isLoading = true)
                }

                is Resource.Success -> {
//                    _saveLocationResponse.value = SaveLocationState(data = it.data)
                    _saveLocationResponse.emit(SaveLocationState(data = it.data))
                }

                is Resource.Error -> {
//                    _saveLocationResponse.value = SaveLocationState(error = "An unexpected error occured")
                    _saveLocationResponse.emit(SaveLocationState(error = "An unexpected error occured"))
                }
            }

        }.launchIn(viewModelScope)

    }


    fun getAddress(loc: LatLng) {

        neshanUseCase(loc).onEach {
            when (it) {
                is Resource.Loading -> {
                    _getAddress.emit(NeshanAddressState(isLoading = true))
                }

                is Resource.Success -> {
                    _getAddress.emit(NeshanAddressState(data = it.data))
                }

                is Resource.Error -> {
                    _getAddress.emit(NeshanAddressState(error = "an error occurred!!"))
                }
            }
        }.launchIn(viewModelScope)

    }

    fun getDirection(firstMarker: Marker?, secondMarker: Marker?) {

        neshanDirectionUseCase(firstMarker!!, secondMarker!!).onEach {
            when (it) {
                is Resource.Loading -> {
                    _getDirection.value = NeshanDirectionState(isLoading = true)
                }

                is Resource.Success -> {
                    _getDirection.value = NeshanDirectionState(data = it.data)
                }

                is Resource.Error -> {
                    _getDirection.value = NeshanDirectionState(error = "an error occurred!!")
                }
            }
        }.launchIn(viewModelScope)
    }

}