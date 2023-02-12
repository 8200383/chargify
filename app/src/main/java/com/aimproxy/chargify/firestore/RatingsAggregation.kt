package com.aimproxy.chargify.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject

class RatingsAggregation(private val db: FirebaseFirestore) {

    data class EvStation(
        internal var stationId: Int = 0,
        internal var avgRating: Double = 0.0,
        internal var numRatings: Int = 0
    )

    fun getRatings(stationIds: Array<Int>): Task<QuerySnapshot> {
        val stationRef = db.collection("ratings")
        return stationRef
            .whereIn("stationId", stationIds.toList())
            .get()
    }

    private fun addRating(evStationRef: DocumentReference, rating: Float): Task<Void> {
        // Create reference for new rating, for use inside the transaction
        val ratingRef = evStationRef.collection("ratings").document()

        // In a transaction, add the new rating and update the aggregate totals
        return db.runTransaction { transaction ->
            val evStation = transaction.get(evStationRef).toObject<EvStation>()!!

            // Compute new number of ratings
            val newNumRatings = evStation.numRatings + 1

            // Compute new average rating
            val oldRatingTotal = evStation.avgRating * evStation.numRatings
            val newAvgRating = (oldRatingTotal + rating) / newNumRatings

            // Set new restaurant info
            evStation.numRatings = newNumRatings
            evStation.avgRating = newAvgRating

            // Update restaurant
            transaction.set(evStationRef, evStation)

            // Update rating
            val data = hashMapOf<String, Any>(
                "rating" to rating
            )
            transaction.set(ratingRef, data, SetOptions.merge())

            null
        }
    }
}