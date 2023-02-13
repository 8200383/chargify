package com.aimproxy.chargify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aimproxy.chargify.datastore.*
import com.aimproxy.chargify.firestore.RatingsAggregation
import com.aimproxy.chargify.firestore.RatingsAggregation.EvStationRating
import com.aimproxy.chargify.services.OpenChargeMapService
import com.aimproxy.chargify.services.SearchEvStationsNearbyInput
import kotlinx.coroutines.launch

class EvStationsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val openChargeMapService = OpenChargeMapService()
    private val ratingsAggregation = RatingsAggregation()
    private val evStationRepository: EvStationRepository

    init {
        val evStationDatabase = EvStationRoomDatabase.getInstance(application)
        val evStationDAO = evStationDatabase.evStationDAO()
        evStationRepository = EvStationRepository(evStationDAO)
    }

    val evStationsList: LiveData<List<EvStationWithConnections>>
        get() = evStationRepository.getAllEvStations()

    private val _selectedEvStation = MutableLiveData<EvStationEntity>()
    val selectedEvStation: LiveData<EvStationEntity>
        get() = _selectedEvStation

    private val _selectedEvStationRating = MutableLiveData<EvStationRating>()
    val selectedEvStationRating: LiveData<EvStationRating>
        get() = _selectedEvStationRating

    fun setCurrentSelectedEvStation(stationId: Int) {
        evStationRepository.getEvStation(stationId).observeForever { evStation ->
            _selectedEvStation.value = evStation
        }
    }

    fun fetchEvStationRating(stationId: Int) {
        viewModelScope.launch {
            val rating = ratingsAggregation.getRatingsById(stationId.toString())
            _selectedEvStationRating.value = rating ?: EvStationRating()
        }
    }

    fun fetchAndSaveEvStations(
        params: SearchEvStationsNearbyInput,
    ) {
        openChargeMapService.lookupEvStations(params) { evStations, _ ->
            viewModelScope.launch {
                evStations?.mapIndexed { i, evStation ->
                    EvStationEntity(
                        stationId = evStation.ID ?: i,
                        phonePrimaryContact = evStation.OperatorInfo?.PhonePrimaryContact,
                        contactEmail = evStation.OperatorInfo?.ContactEmail,
                        operatorInfo = evStation.OperatorInfo?.Title,
                        isOperational = evStation.StatusType?.IsOperational ?: true,
                        usageCost = evStation.UsageCost,
                        addressInfo = evStation.AddressInfo?.Title,
                        addressLine1 = evStation.AddressInfo?.AddressLine1,
                        addressLine2 = evStation.AddressInfo?.AddressLine2,
                        town = evStation.AddressInfo?.Town,
                        stateOrProvince = evStation.AddressInfo?.StateOrProvince,
                        postcode = evStation.AddressInfo?.Postcode,
                        latitude = evStation.AddressInfo?.Latitude,
                        longitude = evStation.AddressInfo?.Longitude,
                        distance = evStation.AddressInfo?.Distance,
                        distanceUnit = evStation.AddressInfo?.DistanceUnit,
                        numberOfPoints = evStation.NumberOfPoints
                    )
                }?.let {
                    evStationRepository.addAllEvStations(it)
                }
            }

            viewModelScope.launch {
                evStations?.forEachIndexed { i, evStation ->
                    evStation.Connections?.mapIndexed { k, conn ->
                        ConnectionEntity(
                            connectionId = conn.ID ?: k,
                            stationId = evStation.ID ?: i,
                            isOperational = conn.StatusType?.IsOperational,
                            formalName = conn.ConnectionType?.FormalName,
                            amps = conn.Amps,
                            voltage = conn.Voltage,
                            powerKw = conn.PowerKw,
                            quantity = conn.Quantity
                        )
                    }?.let {
                        evStationRepository.addAllConnections(it)
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "EvStationsViewModel"
    }
}