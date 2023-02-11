package com.aimproxy.chargify.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
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

    Scaffold(
        floatingActionButton = { EvStationsScreenActions(evStationsViewModel, locationViewModel) },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(evStationsList ?: listOf()) { evStation ->
                EvStationItem(
                    evStationItem = evStation,
                    onClick = { evStationsViewModel.setCurrentSelectedEvStation(it) }
                )
            }
        }
    }
}


@Composable
fun EvStationsScreenActions(
    evStationsViewModel: EvStationsViewModel,
    locationViewModel: LocationViewModel,
) {
    val currentEvStation = evStationsViewModel.selectedEvStation.observeAsState()
    val currentLocation by locationViewModel.location.observeAsState()

    // Intents
    val context = LocalContext.current
    val dialer = Intent(Intent.ACTION_DIAL)
    val share = Intent(Intent.ACTION_SEND)

    // Google Maps
    val maps = Intent(Intent.ACTION_VIEW)
    maps.setPackage("com.google.android.apps.maps");

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        currentEvStation.value?.latitude?.let { latitude ->
            currentEvStation.value?.longitude?.let { longitude ->
                SmallFloatingActionButton(
                    onClick = {
                        maps.data = Uri.parse("geo:0,0?q=$latitude,$longitude")
                        startActivity(context, maps, null);
                    },
                ) {
                    Icon(Icons.Outlined.NearMe, "Go")
                }
            }
        }
        currentEvStation.value?.let { evStation ->
            SmallFloatingActionButton(
                onClick = {
                    share.apply {
                        putExtra(Intent.EXTRA_TEXT, evStation.toString())
                        type = "text/plain"
                    }
                    startActivity(context, Intent.createChooser(share, evStation.toString()), null);
                },
            ) {
                Icon(Icons.Outlined.Share, "Share")
            }
        }
        currentEvStation.value?.phonePrimaryContact?.let { phone ->
            SmallFloatingActionButton(
                onClick = {
                    dialer.data = Uri.parse("tel:${phone}");
                    startActivity(context, dialer, null);
                },
            ) {
                Icon(Icons.Outlined.Call, "Call")
            }
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
}