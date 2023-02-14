package com.aimproxy.chargify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aimproxy.chargify.firestore.TimelinesAggregation
import com.aimproxy.chargify.firestore.TimelinesAggregation.LastKnownEvStation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TimelineViewModel(application: Application) : AndroidViewModel(application) {
    private val aggregation = TimelinesAggregation()

    val timeline: Flow<List<LastKnownEvStation>> = aggregation.timeline

    fun wasHere(evStation: LastKnownEvStation) {
        viewModelScope.launch {
            aggregation.save(evStation)
        }
    }
}