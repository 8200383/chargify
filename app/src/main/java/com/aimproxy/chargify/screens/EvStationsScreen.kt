package com.aimproxy.chargify.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aimproxy.chargify.components.EvStationItem
import com.aimproxy.chargify.datastore.evStationsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvStationsScreen(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(evStationsList) { evStation ->
            EvStationItem(evStation = evStation)
        }
    }
}