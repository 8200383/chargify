package com.aimproxy.chargify.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class RatingsAggregation(
    private val firestore: FirebaseFirestore = Firebase.firestore
) {

    data class EvStationRating(
        @DocumentId internal var stationId: Int = 0,
        internal var avgRating: Double = 0.0,
        internal var numRatings: Int = 0
    )

    fun getRatingsByIds(stationIds: List<Int>): Task<QuerySnapshot> {
        return firestore.collection("ratings")
            .whereIn("stationId", stationIds.toList())
            .get()
    }

    fun addRating(evStationId: Int, rating: Float): Task<Void> {
        val evStationRef = firestore.collection(RATINGS_COLLECTION).document(evStationId.toString())

        return firestore.runTransaction { transaction ->

            // In a transaction, add the new rating and update the aggregate totals
            val evStation =
                transaction.get(evStationRef).toObject<EvStationRating>()
                    ?: EvStationRating(evStationId, 0.0, 0)

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
        private const val TAG = "RatingsAggregation"
        private const val RATINGS_COLLECTION = "ratings"
    }
}