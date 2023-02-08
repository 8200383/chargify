package com.aimproxy.chargify.viewmodels

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aimproxy.chargify.services.EvStation
import com.aimproxy.chargify.services.OpenChargeMapService
import com.aimproxy.chargify.services.SearchEvStationsNearbyInput
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EvStationsViewModel : ViewModel() {
    private val openChargeMapService = OpenChargeMapService()
    val evStationsList = mutableStateListOf<EvStation>()

   // private val _state = mutableStateOf(EvStationsState())
  //  val uiState: StateFlow<EvStationsState> = _state.asStateFlow()

    /*init {
        viewModelScope.launch {
            fetchEvStations()
        }
    }*/

    fun fetchEvStations() {
        openChargeMapService.lookupEvStations(
            SearchEvStationsNearbyInput(
                maxResults = 25,
                countryCode = "us",
                latitude = 37.7749,
                longitude = -122.4194
            )
        ) { data, error ->
            Log.d("EvStations", data.toString())
            if (error == null) {
                evStationsList.clear()
                evStationsList.addAll(data!!)
            }
        }
    }
}

data class EvStationsState(val itemList: List<EvStation> = listOf())
