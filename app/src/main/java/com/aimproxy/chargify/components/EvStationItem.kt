package com.aimproxy.chargify.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aimproxy.chargify.datastore.EvStationWithConnections
import com.aimproxy.chargify.viewmodels.EvStationsViewModel
import com.aimproxy.chargify.viewmodels.EvStationsViewModel.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvStationItem(
    evStationsViewModel: EvStationsViewModel = viewModel(),
    evStationItem: EvStationWithConnections,
    isSelected: Boolean = false,
    onClick: (stationId: Int) -> Unit
) {
    val dark = isSystemInDarkTheme()
    val currentEvStationRating = evStationsViewModel.selectedEvStationRating.observeAsState()

    ListItem(
        modifier = Modifier.clickable { onClick(evStationItem.evStation.stationId) },
        headlineText = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                evStationItem.evStation.operatorInfo?.let { operatorInfo ->
                    Text(
                        text = operatorInfo,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                evStationItem.evStation.distance?.let { distance ->
                    evStationItem.evStation.distanceUnit?.let { unit ->
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            val badgeNumber = String.format("%.2f", distance)
                            Text(
                                text = when (unit) {
                                    2 -> "$badgeNumber Km" // 2 for Km
                                    else -> "$badgeNumber Miles" // 1 for Miles
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        },
        leadingContent = {
            FilledTonalIconButton(onClick = {
                onClick(evStationItem.evStation.stationId)
            }) {
                Icon(
                    when {
                        isSelected -> Icons.Outlined.Done
                        else -> Icons.Outlined.EvStation
                    },
                    contentDescription = "Nearby EvStation",
                )
            }
        },
        overlineText = {
            evStationItem.evStation.addressInfo?.let { addressInfo ->
                Text(
                    text = addressInfo,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        },
        supportingText = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                evStationItem.connections?.forEach { connection ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        connection.formalName?.let { conn ->
                            Text(
                                text = conn,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            connection.powerKw?.let { kw ->
                                Text(
                                    text = "$kw kW",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when {
                                        dark -> Color.Gray
                                        else -> Color.DarkGray
                                    },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                            connection.amps?.let { amps ->
                                Text(
                                    text = "$amps A",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when {
                                        dark -> Color.Gray
                                        else -> Color.DarkGray
                                    },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }
                }
                evStationItem.evStation.phonePrimaryContact?.let { phone ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                    ) {
                        Text(
                            text = phone,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
                if (isSelected) {
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            val badge = when {
                                evStationItem.evStation.isOperational -> "Operational"
                                else -> "Broken"
                            }
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Icon(
                                Icons.Filled.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    currentEvStationRating.value?.let {
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ) {
                                val badgeNumber = String.format("%.1f", it.avgRating)
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
                                val badgeNumber = it.numRatings
                                Text(
                                    text = "$badgeNumber Reviews",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}