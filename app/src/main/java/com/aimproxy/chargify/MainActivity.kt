package com.aimproxy.chargify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.aimproxy.chargify.designsystem.ChargifyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChargifyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChargifyNavigationBar()
                }
            }
        }
    }
}

@Composable
fun ChargifyNavigationBar() {
    var selectedItem by remember { mutableStateOf(NavigationItems.EvStations) }

    NavigationBar {
        NavigationItems.values().forEachIndexed { _, item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedItem == item,
                onClick = {
                    selectedItem = item
                }
            )
        }
    }
}

enum class NavigationItems(val screen: String, val icon: ImageVector, val label: String) {
    EvStations("ev_stations", Icons.Outlined.EvStation, "Ev Stations"),
    Bookmarks("bookmarks", Icons.Outlined.BookmarkBorder, "Bookmarks"),
    Chargers("chargers", Icons.Outlined.Power, "Chargers"),
    Timeline("timeline", Icons.Outlined.Timeline, "Timeline"),
}