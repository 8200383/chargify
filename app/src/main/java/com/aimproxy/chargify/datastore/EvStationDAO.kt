package com.aimproxy.chargify.datastore

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EvStationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllEvStations(evStations: List<EvStationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllConnections(connections: List<ConnectionEntity>)

    @Transaction
    @Query("SELECT * FROM ev_stations ORDER BY distance ASC")
    fun getAllEvStations(): LiveData<List<EvStationWithConnections>>

    @Transaction
    @Query("SELECT * FROM ev_stations WHERE stationId = :stationId")
    fun getEvStation(stationId: Int): LiveData<EvStationEntity>

    @Update
    suspend fun updateEvStation(evStation: EvStationEntity)
}