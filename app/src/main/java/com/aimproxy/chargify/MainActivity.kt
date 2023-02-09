package com.aimproxy.chargify

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.aimproxy.chargify.viewmodels.LocationViewModel
import com.google.android.gms.location.*
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val locationViewModel: LocationViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var listeningToUpdates = false

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.locations.first()
            locationViewModel.updateLocation(location)
            Log.d("Location", locationResult.locations.toString())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startUpdatingLocation()

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
                        ChargifyNavigationHost(navController, innerPadding, locationViewModel)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 0)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startUpdatingLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnSuccessListener {
            listeningToUpdates = true
        }.addOnFailureListener { e ->
            Log.d("Location", "Unable to get location", e)
        }
    }

    override fun onStop() {
        super.onStop()
        if (listeningToUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate()
        }
    }
}

@Composable
fun ChargifyNavigationHost(
    navHostController: NavHostController,
    innerPadding: PaddingValues,
    locationViewModel: LocationViewModel
) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.EvStations.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screens.EvStations.route) { EvStationsScreen(navHostController, innerPadding, locationViewModel) }
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