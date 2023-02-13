package com.aimproxy.chargify.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject

class RatingsAggregation(private val db: FirebaseFirestore) {

    data class EvStationRating(
        @DocumentId internal var stationId: Int = 0,
        internal var avgRating: Double = 0.0,
        internal var numRatings: Int = 0
    )

    fun getRatingsByIds(stationIds: List<Int>): Task<QuerySnapshot> {
        return db.collection("ratings")
            .whereIn("stationId", stationIds.toList())
            .get()
    }

    fun addRating(evStationId: Int, rating: Float): Task<Void> {
        val evStationRef = db.collection("ratings").document(evStationId.toString())

        return db.runTransaction { transaction ->

            // In a transaction, add the new rating and update the aggregate totals
            var evStation = transaction.get(evStationRef).toObject<EvStationRating>()
            if (evStation == null) {
                evStation = EvStationRating(evStationId, 0.0, 0)
            }

            // Compute new number of ratings
            val newNumRatings = evStation.numRatings + 1

            // Compute new average rating
            val oldRatingTotal = evStation.avgRating * evStation.numRatings
            val newAvgRating = (oldRatingTotal + rating) / newNumRatings

            // Set new restaurant info
            evStation.numRatings = newNumRatings
            evStation.avgRating = newAvgRating

            transaction.set(evStationRef, evStation, SetOptions.merge())

            // Success
            null
        }
    }

    companion object {
        private val TAG = "RatingsAggregation"
    }
}