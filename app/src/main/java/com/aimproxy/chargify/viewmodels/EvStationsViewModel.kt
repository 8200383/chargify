package com.aimproxy.chargify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.aimproxy.chargify.datastore.*
import com.aimproxy.chargify.services.OpenChargeMapService
import com.aimproxy.chargify.services.SearchEvStationsNearbyInput
import kotlinx.coroutines.launch

class EvStationsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val openChargeMapService = OpenChargeMapService()
    private val evStationRepository: EvStationRepository

    init {
        val evStationDatabase = EvStationRoomDatabase.getInstance(application)
        val evStationDAO = evStationDatabase.evStationDAO()
        evStationRepository = EvStationRepository(evStationDAO)
    }

    val evStationsList: LiveData<List<EvStationWithConnectionsList>> =
        evStationRepository.getAllEvStations();

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
                        town = evStation.AddressInfo?.Town,
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
}