package com.aimproxy.chargify.datastore

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "ev_stations")
data class EvStation(
    @PrimaryKey(autoGenerate = false) val stationId: Int,

    val phonePrimaryContact: String? = null,
    val contactEmail: String? = null,
    val operatorInfo: String? = null,

    val isOperational: Boolean = false,
    val usageCost: String? = null,

    val addressInfo: String? = null,
    val town: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distance: Double? = null,
    val distanceUnit: Int? = null,

    val numberOfPoints: Int? = null,

    @Relation(
        parentColumn = "stationId",
        entityColumn = "parentEvStationId"
    )
    val connections: List<EvStationConnection>

)