package com.aimproxy.chargify.components

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.aimproxy.chargify.firestore.TimelinesAggregation.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineListItem(
    lastKnownEvStation: LastKnownEvStation,
) {
    Log.d("Timeline", lastKnownEvStation.stationId.toString())
    ListItem(
        headlineText = {
            lastKnownEvStation.addressInfo?.let {
                Text(it, fontWeight = FontWeight.Bold)
            }
        },
        supportingText = {
            lastKnownEvStation.timestamp?.let {
                val elapsedTimeMs = Date().time - it.toDate().time
                val elapsedSeconds = (elapsedTimeMs / 1000).toInt()
                val elapsedMinutes = (elapsedSeconds / 60)
                val elapsedHours = (elapsedMinutes / 60)
                val elapsedDays = (elapsedHours / 24)

                val elapsedStr = when {
                    elapsedDays > 0 -> "$elapsedDays days ago"
                    elapsedHours > 0 -> "$elapsedHours hours ago"
                    elapsedMinutes > 0 -> "$elapsedMinutes minutes ago"
                    else -> "$elapsedSeconds seconds ago"
                }
                Text(elapsedStr, fontWeight = FontWeight.Bold)
            }
        },
        leadingContent = {
            FilledTonalIconButton(onClick = {}) {
                Icon(Icons.Outlined.History, contentDescription = "Saved EvStation")
            }
        },
    )
}