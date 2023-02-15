package com.aimproxy.chargify

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aimproxy.chargify.designsystem.ChargifyTheme
import com.aimproxy.chargify.screens.BookmarksScreen
import com.aimproxy.chargify.screens.EvStationsScreen
import com.aimproxy.chargify.screens.TimelineScreen
import com.aimproxy.chargify.viewmodels.LocationViewModel
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // Location
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var listeningToUpdates = false

    companion object {
        const val APP_NAME = "Chargify"
        const val CHANNEL_NAME = "Chargify Channel"
        const val CHANNEL_ID = "chargify_channel"
        const val NOTIFICATION_ID = 12345
        const val REQUEST_CODE_LOCATION = 0
        const val REQUEST_CODE_NOTIFICATION = 1
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.locations.first()
            locationViewModel.updateLocation(location)
            Log.d("Location#Callback", location.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startUpdatingLocation()

        // Firebase Auth
        firebaseAuth = Firebase.auth

        setContent {
            ChargifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    Scaffold(
                        topBar = { ChargifyTopBar(navController, firebaseAuth) },
                        bottomBar = { ChargifyNavigationBar(navController) }
                    ) { innerPadding ->
                        ChargifyNavigationHost(
                            navController,
                            innerPadding,
                            locationViewModel
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
        if (checkSelfPermission(POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION)
        }
        createNotificationChannel()
    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        if (listeningToUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

        // Notification Alert
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(APP_NAME)
            .setContentText("Thank you for using Chargify!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargifyTopBar(
    navController: NavHostController,
    firebaseAuth: FirebaseAuth
) {
    // NavController#findTitle
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val topBarLabel = Screens.values().firstOrNull { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }?.topBarLabel

    // GoogleSignInActivity
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    val intent = Intent(context, GoogleSignInActivity::class.java)

    TopAppBar(
        title = {
            Text(text = topBarLabel ?: "Chargify", fontWeight = FontWeight.ExtraBold)
        },
        actions = {
            FilledTonalIconButton(onClick = {
                firebaseAuth.signOut()
                startActivity(context, intent, null)
                activity?.finish()
            }) {
                Icon(Icons.Outlined.DoorBack, contentDescription = "Sign Out")
            }
        }
    )
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
        composable(Screens.Bookmarks.route) { BookmarksScreen() }
        composable(Screens.EvStations.route) {
            EvStationsScreen(
                locationViewModel = locationViewModel
            )
        }
        composable(Screens.Timeline.route) { TimelineScreen() }
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
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.navigationLabel
                    )
                },
                label = {
                    Text(
                        text = screen.navigationLabel,
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

enum class Screens(
    val route: String,
    val icon: ImageVector,
    val navigationLabel: String,
    val topBarLabel: String
) {
    Bookmarks("bookmarks", Icons.Outlined.BookmarkBorder, "Bookmarks", "Saved Stations"),
    EvStations("ev_stations", Icons.Outlined.EvStation, "Ev Stations", "Ev Stations Nearby"),
    Timeline("timeline", Icons.Outlined.Timeline, "Timeline", "Timeline"),
}