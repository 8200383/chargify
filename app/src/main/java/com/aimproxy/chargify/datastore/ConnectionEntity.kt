package com.aimproxy.chargify.datastore

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConnectionEntity(
    @PrimaryKey(autoGenerate = false) val connectionId: Int,
    val parentEvStationId: Int,

    val formalName: String? = null,
    val amps: Int? = null,
    val voltage: Int? = null,
    val powerKw: Double? = null,
    val quantity: Int? = null
)


