package com.aimproxy.chargify.datastore


data class EvStation(
    val networkOperator: String,
    val chargers: List<EvCharger>
)

val evStationsList = listOf(
    EvStation(
        networkOperator = "EDP",
        chargers = listOf(
            EvCharger(
                equipment = "CHAdeMO",
                power = "DC",
                kW = 50,
                amps = 120,
                voltage = 500,
                bays = 2,
                operational = true
            )
        )
    ),
    EvStation(
        networkOperator = "Galp",
        chargers = listOf(
            EvCharger(
                equipment = "CCS (Type 2)",
                power = "DC",
                kW = 50,
                amps = 125,
                voltage = 500,
                bays = 1,
                operational = true
            ),
            EvCharger(
                equipment = "Type 2 (Tethered Connector)",
                power = "AC (Three-Phase)",
                kW = 43,
                amps = 63,
                voltage = 400,
                bays = 1,
                operational = true
            )
        )
    ),
    EvStation(
        networkOperator = "Efacec",
        chargers = listOf(
            EvCharger(
                equipment = "Type 2 (Tethered Connector)",
                power = "AC (Three-Phase)",
                kW = 43,
                amps = 63,
                voltage = 400,
                bays = 1,
                operational = true
            )
        )
    ),
)