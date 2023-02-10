package com.aimproxy.chargify.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                onClick = {
                    evStationsViewModel.fetchAndSaveEvStations(
                        SearchEvStationsNearbyInput(
                            maxResults = 25,
                            countryCode = "pt",
                            latitude = currentLocation?.latitude ?: Double.MIN_VALUE,
                            longitude = currentLocation?.longitude ?: Double.MIN_VALUE
                        )
                    )
                },
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
            items(evStationsList ?: listOf()) { evStation ->
                EvStationItem(evStationItem = evStation)
            }
        }
    }
}