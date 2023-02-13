package com.aimproxy.chargify.datastore

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

data class EvStationWithConnections(
    @Embedded /* The parent */
    val evStation: EvStationEntity,

    @Relation(
        entity = ConnectionEntity::class,
        parentColumn = "stationId", /* The column in the @Embedded class (parent) */
        entityColumn = "stationId", /* The column in the @Relation class (child) */
    )
    val connections: List<ConnectionEntity>?
)

@Entity(tableName = "ev_stations")
data class EvStationEntity(
    @PrimaryKey(autoGenerate = false) val stationId: Int,

    val phonePrimaryContact: String? = null,
    val contactEmail: String? = null,
    val operatorInfo: String? = null,

    val isOperational: Boolean = false,
    val usageCost: String? = null,

    val addressInfo: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val town: String? = null,
    val stateOrProvince: String? = null,
    val postcode: String? = null,

    val latitude: Double? = null,
    val longitude: Double? = null,
    val distance: Double? = null,
    val distanceUnit: Int? = null,

    val numberOfPoints: Int? = null,
) {
    override fun toString(): String {
        return "${operatorInfo ?: ""}\n" +
                "${addressLine1 ?: ""}\n" +
                "${addressLine2 ?: ""}\n" +
                Uri.parse("https://www.google.com/maps/place/${latitude},${longitude}")
    }
}

@Entity
data class ConnectionEntity(
    @PrimaryKey(autoGenerate = false) val connectionId: Int,
    val stationId: Int,

    val isOperational: Boolean? = null,
    val formalName: String? = null,
    val amps: Int? = null,
    val voltage: Int? = null,
    val powerKw: Double? = null,
    val quantity: Int? = null
)