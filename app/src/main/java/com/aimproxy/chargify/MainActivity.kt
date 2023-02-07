package com.aimproxy.chargify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aimproxy.chargify.designsystem.ChargifyTheme
import com.aimproxy.chargify.screens.BookmarksScreen
import com.aimproxy.chargify.screens.EvChargersScreen
import com.aimproxy.chargify.screens.EvStationsScreen
import com.aimproxy.chargify.screens.TimelineScreen
import com.aimproxy.chargify.services.ChargifyLocationService

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val locationServiceIntent by lazy {
        Intent(this, ChargifyLocationService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ensureChargifyPermissions()
        startService(locationServiceIntent)

        setContent {
            ChargifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    Scaffold(
                        topBar = { ChargifyTopBar(navController) },
                        bottomBar = { ChargifyNavigationBar(navController) }
                    ) { innerPadding ->
                        ChargifyNavigationHost(navController, innerPadding)
                    }
                }
            }
        }
    }

    private fun ensureChargifyPermissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(locationServiceIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargifyTopBar(navController: NavHostController) {
    TopAppBar(title = {
        Text(
            text = "Chargify",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    })
}

@Composable
fun ChargifyNavigationHost(
    navHostController: NavHostController,
    innerPadding: PaddingValues,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.EvStations.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screens.EvStations.route) { EvStationsScreen(navHostController, innerPadding) }
        composable(Screens.Bookmarks.route) { BookmarksScreen(navHostController, innerPadding) }
        composable(Screens.Chargers.route) { EvChargersScreen(navHostController) }
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
                        fontWeight = FontWeight.ExtraBold
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