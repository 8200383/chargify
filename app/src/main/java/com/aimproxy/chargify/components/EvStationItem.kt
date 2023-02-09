package com.aimproxy.chargify.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aimproxy.chargify.services.EvStation
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvStationItem(
    evStation: EvStation,
) {
    val dark = isSystemInDarkTheme()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        evStation.OperatorInfo?.Title?.let { stationOperator ->
            ListItem(
                headlineText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stationOperator,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        evStation.AddressInfo?.Distance?.let { distance ->
                            evStation.AddressInfo?.DistanceUnit?.let { unit ->
                                val shortDistance = distance.roundToInt()
                                Text(
                                    text = when (unit) {
                                        2 -> "$shortDistance Km" // 2 for Km
                                        else -> "$shortDistance Miles" // 1 for Miles
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }
                },
                trailingContent = {
                    IconButton(
                        onClick = { /* doSomething() */ },
                        colors = IconButtonDefaults.outlinedIconButtonColors(),
                    ) {
                        Icon(Icons.Outlined.Power, contentDescription = null)
                    }
                },
                supportingText = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        evStation.AddressInfo?.Title?.let { town ->
                            Text(
                                text = town,
                                fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                        evStation.Connections?.forEach { connection ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                connection.ConnectionType?.FormalName?.let { conn ->
                                    Text(
                                        text = conn,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    connection.PowerKW?.let { kw ->
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
                                    connection.Amps?.let { amps ->
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
                    }
                }
            )
        }
    }
}