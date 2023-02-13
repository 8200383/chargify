package com.aimproxy.chargify.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.EvStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aimproxy.chargify.firestore.RatingsAggregation
import com.aimproxy.chargify.viewmodels.EvStationsViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvStationRateDialog(
    evStationsViewModel: EvStationsViewModel = viewModel(),
    openDialog: MutableState<Boolean>
) {
    val currentEvStation = evStationsViewModel.selectedEvStation.observeAsState()
    val currentEvStationRating = evStationsViewModel.selectedEvStationRating.observeAsState()
    var sliderPosition by remember { mutableStateOf(0f) }

    val ratingsService = RatingsAggregation(Firebase.firestore)

    AlertDialog(
        onDismissRequest = { openDialog.value = false },
        icon = { Icon(Icons.Outlined.EvStation, contentDescription = null) },
        title = {
            Text(
                text = "Rate ${currentEvStation.value?.addressInfo ?: "this station!"}",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        val badgeNumber =
                            String.format("%.1f", currentEvStationRating.value?.avgRating)
                        Text(
                            text = badgeNumber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        val badgeNumber = currentEvStationRating.value?.numRatings
                        Text(
                            text = "$badgeNumber Reviews",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Text(
                    text = "Power up your ride and spread the joy of electric driving by rating your favorite EV charging stations!",
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = sliderPosition.roundToInt().toString(),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 0f..5f,
                    onValueChangeFinished = {
                        // launch some business logic update with the state you hold
                        // viewModel.updateSelectedSliderValue(sliderPosition)
                    },
                    steps = 4
                )
            }
        },
        confirmButton = {
            currentEvStation.value?.stationId?.let {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        ratingsService.addRating(it, sliderPosition)
                    }
                ) {
                    Text(text = "Give my rate!", fontWeight = FontWeight.SemiBold)
                }
            }
        },
    )
}