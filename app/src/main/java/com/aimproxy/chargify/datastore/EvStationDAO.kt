package com.aimproxy.chargify.datastore

import androidx.room.*

@Dao
interface EvStationDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addEvStation(evStation: EvStationEntity)

    @Query("SELECT * FROM ev_stations")
    fun getAllEvStations(): List<EvStationEntity>

    @Update
    suspend fun updateEvStation(evStation: EvStationEntity)
}