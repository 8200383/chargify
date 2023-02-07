package com.aimproxy.chargify.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aimproxy.chargify.datastore.EvStation

@Composable
fun EvStationItem(
    evStation: EvStation,
) {
    val dark = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = evStation.networkOperator,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                evStation.chargers.forEach { charger ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                    ) {
                        Text(
                            text = charger.equipment,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = " · ${charger.kW} kW",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when {
                                dark -> Color.Gray
                                else -> Color.DarkGray
                            }
                        )
                    }
                }
            }
        }
    }
}