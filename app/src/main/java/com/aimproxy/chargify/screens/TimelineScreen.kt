package com.aimproxy.chargify.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aimproxy.chargify.components.TimelineEmptyState
import com.aimproxy.chargify.components.TimelineListItem
import com.aimproxy.chargify.viewmodels.TimelineViewModel

@Composable
fun TimelineScreen(
    timelineViewModel: TimelineViewModel = viewModel()
) {
    val lazyListState = rememberLazyListState()
    val timeline by timelineViewModel.timeline.collectAsState(emptyList())

    when {
        timeline.isEmpty() -> TimelineEmptyState()
        else -> {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(timeline) {
                    TimelineListItem(lastKnownEvStation = it)
                }
            }
        }
    }
}