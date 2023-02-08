package com.aimproxy.chargify.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aimproxy.chargify.components.EvStationItem
import com.aimproxy.chargify.services.EvStation
import com.aimproxy.chargify.services.OpenChargeMapService
import com.aimproxy.chargify.services.SearchEvStationsNearbyInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvStationsScreen(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    val lazyListState = rememberLazyListState()

    // OpenChargeMap
    val openChargeMapService = OpenChargeMapService()
    val evStationsList = remember { mutableStateListOf<EvStation>() }

    // Location Lookup

    fun fetchEvStations() {
        openChargeMapService.lookupEvStations(
            SearchEvStationsNearbyInput(
                maxResults = 25,
                countryCode = "us",
                latitude = 37.7749,
                longitude = -122.4194
            )
        ) { data, error ->
            Log.d("EvStations", data.toString())
            if (error == null) {
                evStationsList.clear()
                evStationsList.addAll(data!!)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Ev Stations Nearby",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { fetchEvStations() },
                icon = { Icon(Icons.Outlined.MyLocation, "Find Stations") },
                text = { Text(text = "Find Stations", fontWeight = FontWeight.Bold) },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(evStationsList) { evStation ->
                EvStationItem(evStation = evStation)
            }
        }
    }
}