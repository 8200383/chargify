package com.aimproxy.chargify.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.aimproxy.chargify.firestore.TimelinesAggregation.*
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineListItem(
    lastKnownEvStation: LastKnownEvStation,
) {

    ListItem(
        headlineText = {
            lastKnownEvStation.addressInfo?.let {
                Text(it, fontWeight = FontWeight.Bold)
            }
        },
        supportingText = {
            lastKnownEvStation.timestamp?.let {
                TimelineTicker(it)
            }
        },
        leadingContent = {
            FilledTonalIconButton(onClick = {}) {
                Icon(Icons.Outlined.History, contentDescription = "Saved EvStation")
            }
        },
    )
}

@Composable
fun TimelineTicker(
    timestamp: Timestamp
) {
    var elapsedStr by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val elapsedTimeMs = Date().time - timestamp.toDate().time
            val elapsedSeconds = (elapsedTimeMs / 1000).toInt()
            val elapsedMinutes = (elapsedSeconds / 60)
            val elapsedHours = (elapsedMinutes / 60)
            val elapsedDays = (elapsedHours / 24)

            elapsedStr = when {
                elapsedDays > 0 -> "$elapsedDays days ago"
                elapsedHours > 0 -> "$elapsedHours hours ago"
                elapsedMinutes > 0 -> "$elapsedMinutes minutes ago"
                else -> "$elapsedSeconds seconds ago"
            }
            delay(1000L)
        }
    }

    Text(elapsedStr, fontWeight = FontWeight.Bold)
}