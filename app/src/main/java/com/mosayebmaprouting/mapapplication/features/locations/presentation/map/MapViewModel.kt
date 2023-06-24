package com.mosayebmaprouting.mapapplication.features.locations.presentation.map


import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mosayebmaprouting.mapapplication.core.Resource
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.AddLocationUseCase
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.LocationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val useCase: LocationUseCases) : ViewModel() {


    private val _saveLocationResponse = MutableSharedFlow<SaveLocationState>()
    val saveLocationResponse = _saveLocationResponse.asSharedFlow()


    // a good replacement for liveData keeps the latest value and here we can have operators like filter , map and so on
//    private val _getAddress = MutableStateFlow("")
//    val getAddress = _getAddress.asStateFlow()

    lateinit var buildAddress: String

    //
    private val _getAddress = MutableSharedFlow<String>()
    val getAddress = _getAddress.asSharedFlow()


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

    fun createAdress(address: Address?) {
        address?.let {

            viewModelScope.launch {
                val state = address.adminArea ?: ""// Get the state
                val city = address.locality ?: "" // Get the city
                val street = address.thoroughfare ?: ""// Get the street address

                if (state.isEmpty() && city.isEmpty() && street.isEmpty()) {
                    buildAddress = ""
                } else if (state.isNotEmpty() && city.isEmpty() && street.isEmpty()) {
                    buildAddress = state
                } else if (state.isNotEmpty() && city.isNotEmpty() && street.isEmpty()) {
                    buildAddress = "$state ,$city"
                } else {
                    buildAddress = "$state ,$city ,$street"
                }

                _getAddress.emit(buildAddress)
            }
        }

    }


}