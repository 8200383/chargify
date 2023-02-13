package com.aimproxy.chargify.datastore

import androidx.lifecycle.LiveData

class EvStationRepository(
    private val evStationDAO: EvStationDAO,
) {
    suspend fun addAllEvStations(evStations: List<EvStationEntity>) {
        return evStationDAO.addAllEvStations(evStations)
    }

    suspend fun addAllConnections(connections: List<ConnectionEntity>) {
        return evStationDAO.addAllConnections(connections)
    }

    fun getAllEvStations(): LiveData<List<EvStationWithConnections>> {
        return evStationDAO.getAllEvStations()
    }

    fun getEvStation(evStationId: Int): LiveData<EvStationEntity> {
        return evStationDAO.getEvStation(evStationId)
    }

    suspend fun updateEvStation(evStation: EvStationEntity) {
        return evStationDAO.updateEvStation(evStation)
    }
}