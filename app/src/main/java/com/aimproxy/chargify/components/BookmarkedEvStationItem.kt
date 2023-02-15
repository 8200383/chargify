package com.aimproxy.chargify.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aimproxy.chargify.firestore.BookmarksAggregation.*
import com.aimproxy.chargify.viewmodels.BookmarksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkedEvStationItem(
    bookmarksViewModel: BookmarksViewModel = viewModel(),
    bookmarkedEvStation: BookmarkedEvStation
) {
    // Google Maps
    val context = LocalContext.current
    val maps = Intent(Intent.ACTION_VIEW)

    ListItem(
        headlineText = {
            bookmarkedEvStation.addressInfo?.let {
                Text(it, fontWeight = FontWeight.Bold)
            }
        },
        supportingText = {
            bookmarkedEvStation.operatorInfo?.let {
                Text(it, fontWeight = FontWeight.Bold)
            }
        },
        leadingContent = {
            FilledTonalIconButton(onClick = {
                bookmarksViewModel.bookmarkEvStation(bookmarkedEvStation)
            }) {
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Saved EvStation")
            }
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                bookmarkedEvStation.latitude?.let { latitude ->
                    bookmarkedEvStation.longitude?.let { longitude ->
                        FilledTonalIconButton(onClick = {
                            maps.apply {
                                data = Uri.parse("geo:0,0?q=$latitude,$longitude")
                            }
                            ContextCompat.startActivity(context, maps, null)
                        }) {
                            Icon(
                                Icons.Outlined.NearMe,
                                contentDescription = "Go to",
                            )
                        }
                    }
                }
            }
        }
    )
}