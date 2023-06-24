package com.mosayebmaprouting.mapapplication.features.locations.presentation.locations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mosayebmaprouting.mapapplication.core.Resource
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.LocationUseCases
import com.mosayebmaprouting.mapapplication.features.locations.presentation.map.SaveLocationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(private val useCases: LocationUseCases): ViewModel() {

//    private val _getLocationResponse = MutableLiveData<SaveLocationState>()
//    val saveLocationResponse: LiveData<SaveLocationState> get() = _saveLocationResponse


    private val _getLocationResponse = MutableStateFlow(GetLocationsState())
    val getLocationResponse = _getLocationResponse.asStateFlow()


    private val _deleteLocationResponse = MutableSharedFlow<DeleteLocationState>()
    val deleteLocationResponse = _deleteLocationResponse.asSharedFlow()

    fun getLocationsFromDb() {
        useCases.getLocationsUseCase().onEach {
            _getLocationResponse.value = GetLocationsState(data = it)
        }.launchIn(viewModelScope)
    }



    fun deleteLocation(location: LocationModel) {
        useCases.deleteLocationUseCase(location).onEach { result ->
            when (result){
                is Resource.Loading -> {
                    _deleteLocationResponse.emit(DeleteLocationState(isLoading = true))
                }

                is Resource.Success -> {
                    _deleteLocationResponse.emit(DeleteLocationState(data = result.data))
                }

                is Resource.Error -> {
                    _deleteLocationResponse.emit(DeleteLocationState(error = "An unexpected error occured"))
                }

            }

        }.launchIn(viewModelScope)
    }
}