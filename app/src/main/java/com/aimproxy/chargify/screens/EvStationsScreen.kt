package com.aimproxy.chargify.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aimproxy.chargify.components.EvStationItem
import com.aimproxy.chargify.components.EvStationRateDialog
import com.aimproxy.chargify.components.EvStationsScreenActions
import com.aimproxy.chargify.viewmodels.EvStationsViewModel
import com.aimproxy.chargify.viewmodels.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvStationsScreen(
    evStationsViewModel: EvStationsViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel()
) {
    val lazyListState = rememberLazyListState()
    val evStationsList by evStationsViewModel.evStationsList.observeAsState(listOf())
    val selectedEvStation = evStationsViewModel.selectedEvStation.observeAsState()

    val openRateDialog = remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            EvStationsScreenActions(
                locationViewModel = locationViewModel,
                onClickStarRate = { openRateDialog.value = true }
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
            items(evStationsList) { station ->
                val selected = selectedEvStation.value?.stationId
                EvStationItem(
                    evStationItem = station,
                    isSelected = selected?.equals(station.evStation.stationId) ?: false,
                    onClick = { evStationsViewModel.setCurrentSelectedEvStation(it) }
                )
            }
        }
    }

    when {
        openRateDialog.value -> {
            EvStationRateDialog(
                openDialog = openRateDialog
            )
        }
    }
}