package com.aimproxy.chargify.datastore

import androidx.room.*

@Dao
interface EvStationDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addEvStation(evStation: EvStation)

    @Query("SELECT * FROM ev_stations")
    fun getAllEvStations(): List<EvStation>

    @Update
    suspend fun updateEvStation(evStation: EvStation)
}