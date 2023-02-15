package com.aimproxy.chargify.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.aimproxy.chargify.components.BookmarkedEvStationItem
import com.aimproxy.chargify.components.BookmarksEmptyState
import com.aimproxy.chargify.viewmodels.BookmarksViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BookmarksScreen(
    bookmarksViewModel: BookmarksViewModel = viewModel(),
) {
    val lazyListState = rememberLazyListState()
    val bookmarks by bookmarksViewModel.bookmarks.collectAsState(emptyList())

    when {
        bookmarks.isEmpty() -> BookmarksEmptyState()
        else -> {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookmarks) {
                    BookmarkedEvStationItem(bookmarkedEvStation = it)
                }
            }
        }
    }
}