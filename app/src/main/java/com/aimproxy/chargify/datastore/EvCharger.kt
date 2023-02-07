package com.aimproxy.chargify.datastore

data class EvCharger(
    val equipment: String,
    val power: String,
    val kW: Int,
    val amps: Int,
    val voltage: Int,
    val bays: Int,
    val operational: Boolean
)
