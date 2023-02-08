package com.aimproxy.chargify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material.icons.outlined.EvStation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aimproxy.chargify.services.EvStation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvStationItem(
    evStation: EvStation,
) {
    val dark = isSystemInDarkTheme()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        evStation.OperatorInfo?.Title?.let { station ->
            ListItem(
               /*leadingContent = {
                    Icon(
                        Icons.Outlined.EvStation,
                        contentDescription = null,
                    )
                }, */
                headlineText = {
                    Text(
                        text = station,
                        fontWeight = FontWeight.Bold,
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = { /* doSomething() */ },
                        colors = IconButtonDefaults.outlinedIconButtonColors(),
                    ) {
                        Icon(Icons.Outlined.Egg, contentDescription = null)
                    }
                },
                supportingText = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        evStation.Connections?.forEach { connection ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                connection.ConnectionType?.FormalName?.let { c ->
                                    Text(
                                        text = c,
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