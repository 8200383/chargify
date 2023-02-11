package com.aimproxy.chargify.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aimproxy.chargify.components.EvStationItem
import com.aimproxy.chargify.services.SearchEvStationsNearbyInput
import com.aimproxy.chargify.viewmodels.EvStationsViewModel
import com.aimproxy.chargify.viewmodels.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvStationsScreen(
    evStationsViewModel: EvStationsViewModel,
    locationViewModel: LocationViewModel
) {
    val lazyListState = rememberLazyListState()

    val evStationsList by evStationsViewModel.evStationsList.observeAsState()
    val currentLocation by locationViewModel.location.observeAsState()

    Scaffold(
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End
            ) {
                SmallFloatingActionButton(
                    onClick = {},
                    containerColor = Color.White
                ) {
                    Icon(Icons.Outlined.NearMe, "Go")
                }
                SmallFloatingActionButton(
                    onClick = {},
                    containerColor = Color.White
                ) {
                    Icon(Icons.Outlined.Share, "Share")
                }
                SmallFloatingActionButton(
                    onClick = {},
                    containerColor = Color.White
                ) {
                    Icon(Icons.Outlined.Call, "Call")
                }
                FloatingActionButton(
                    onClick = {
                        evStationsViewModel.fetchAndSaveEvStations(
                            SearchEvStationsNearbyInput(
                                maxResults = 25,
                                countryCode = "pt",
                                latitude = currentLocation?.latitude ?: Double.MIN_VALUE,
                                longitude = currentLocation?.longitude ?: Double.MIN_VALUE
                            )
                        )
                    }
                ) {
                    Icon(Icons.Outlined.MyLocation, "Find Stations")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(evStationsList ?: listOf()) { evStation ->
                EvStationItem(evStationItem = evStation)
            }
        }
    }
}