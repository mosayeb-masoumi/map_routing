package com.mosayebmap.mapapplication.features.map


import android.content.Context
import android.location.LocationManager
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {


    // a good replacement for liveData keeps the latest value and here we can have operators like filter , map and so on
//    private val _stateFlow = MutableStateFlow("Hello World")
//    val stateFlow = _stateFlow.asStateFlow()


    private val _getGpsState = MutableSharedFlow<Boolean>()
    val getGpsState = _getGpsState.asSharedFlow()

//    fun isLocationON(){
//        viewModelScope.launch {
//
//                      _getGpsState.emit(true)
//        }
//    }



}