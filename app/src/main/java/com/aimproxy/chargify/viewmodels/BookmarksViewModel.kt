package com.aimproxy.chargify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aimproxy.chargify.firestore.BookmarksAggregation
import com.aimproxy.chargify.firestore.BookmarksAggregation.BookmarkedEvStation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BookmarksViewModel(application: Application) : AndroidViewModel(application) {
    private val aggregation = BookmarksAggregation()

    val bookmarks: Flow<List<BookmarkedEvStation>> = aggregation.bookmarks

    fun bookmarkEvStation(evStation: BookmarkedEvStation) {
        viewModelScope.launch {
            val documentId = evStation.stationId
            if (!aggregation.isBookmarked(documentId)) {
                aggregation.save(evStation)
            } else {
                aggregation.delete(documentId)
            }
        }
    }

    companion object {
        private val TAG = "UsersViewModel"
    }
}