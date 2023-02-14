package com.aimproxy.chargify.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aimproxy.chargify.firestore.BookmarksAggregation.BookmarkedEvStation
import com.aimproxy.chargify.firestore.TimelinesAggregation.LastKnownEvStation
import com.aimproxy.chargify.services.SearchEvStationsNearbyInput
import com.aimproxy.chargify.viewmodels.BookmarksViewModel
import com.aimproxy.chargify.viewmodels.EvStationsViewModel
import com.aimproxy.chargify.viewmodels.LocationViewModel
import com.aimproxy.chargify.viewmodels.TimelineViewModel

@Composable
fun EvStationsScreenActions(
    bookmarksViewModel: BookmarksViewModel = viewModel(),
    evStationsViewModel: EvStationsViewModel = viewModel(),
    timelineViewModel: TimelineViewModel = viewModel(),
    locationViewModel: LocationViewModel,
    onClickStarRate: () -> Unit
) {
    val currentEvStation = evStationsViewModel.selectedEvStation.observeAsState()
    val currentLocation by locationViewModel.location.observeAsState()

    // Intents
    val context = LocalContext.current
    val dialer = Intent(Intent.ACTION_DIAL)
    val share = Intent(Intent.ACTION_SEND)

    // Google Maps
    val maps = Intent(Intent.ACTION_VIEW)
    maps.setPackage("com.google.android.apps.maps")

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        currentEvStation.value?.latitude?.let { latitude ->
            currentEvStation.value?.longitude?.let { longitude ->
                SmallFloatingActionButton(
                    onClick = {
                        maps.data = Uri.parse("geo:0,0?q=$latitude,$longitude")
                        ContextCompat.startActivity(context, maps, null)
                        timelineViewModel.wasHere(
                            LastKnownEvStation(
                                stationId = currentEvStation.value?.stationId,
                                addressInfo = currentEvStation.value?.addressInfo
                            )
                        )
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
                    ContextCompat.startActivity(
                        context,
                        Intent.createChooser(share, evStation.toString()),
                        null
                    )
                },
            ) {
                Icon(Icons.Outlined.Share, "Share")
            }
        }

        currentEvStation.value?.phonePrimaryContact?.let { phone ->
            SmallFloatingActionButton(
                onClick = {
                    dialer.data = Uri.parse("tel:${phone}")
                    ContextCompat.startActivity(context, dialer, null)
                },
            ) {
                Icon(Icons.Outlined.Call, "Call")
            }
        }

        currentEvStation.value?.let {
            SmallFloatingActionButton(
                onClick = { onClickStarRate() },
            ) {
                Icon(Icons.Outlined.StarBorder, "Rate")
            }
        }

        currentEvStation.value?.let { evStation ->
            SmallFloatingActionButton(
                onClick = {
                    bookmarksViewModel.bookmarkEvStation(
                        BookmarkedEvStation(
                            stationId = evStation.stationId.toString(),
                            operatorInfo = evStation.operatorInfo,
                            addressInfo = evStation.addressInfo,
                            latitude = evStation.latitude,
                            longitude = evStation.longitude
                        )
                    )
                },
            ) {
                Icon(Icons.Outlined.BookmarkBorder, "Bookmark")
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