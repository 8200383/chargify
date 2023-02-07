package com.aimproxy.chargify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aimproxy.chargify.designsystem.ChargifyTheme
import com.aimproxy.chargify.features.bookmarks.BookmarksScreen
import com.aimproxy.chargify.features.chargers.ChargersScreen
import com.aimproxy.chargify.features.stations.EvStationsScreen
import com.aimproxy.chargify.features.timeline.TimelineScreen

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChargifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = { ChargifyNavigationBar(navController) }
                    ) { innerPadding ->
                        ChargifyNavigationHost(navController, innerPadding)
                    }
                }
            }
        }
    }
}

@Composable
fun ChargifyNavigationHost(
    navHostController: NavHostController,
    innerPadding: PaddingValues,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.EvStations.route,
        Modifier.padding(innerPadding)
    ) {
        composable(Screens.EvStations.route) { EvStationsScreen(navHostController) }
        composable(Screens.Bookmarks.route) { BookmarksScreen(navHostController) }
        composable(Screens.Chargers.route) { ChargersScreen(navHostController) }
        composable(Screens.Timeline.route) { TimelineScreen(navHostController) }

    }
}

@Composable
fun ChargifyNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        Screens.values().forEachIndexed { _, screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.label) },
                label = {
                    Text(
                        text = screen.label,
                        fontWeight = when {
                            selected -> FontWeight.ExtraBold
                            else -> FontWeight.Bold
                        }
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

enum class Screens(val route: String, val icon: ImageVector, val label: String) {
    EvStations("ev_stations", Icons.Outlined.EvStation, "Ev Stations"),
    Bookmarks("bookmarks", Icons.Outlined.BookmarkBorder, "Bookmarks"),
    Chargers("chargers", Icons.Outlined.Power, "Chargers"),
    Timeline("timeline", Icons.Outlined.Timeline, "Timeline"),
}