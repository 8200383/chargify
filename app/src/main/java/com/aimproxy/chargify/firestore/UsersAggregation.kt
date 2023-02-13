package com.aimproxy.chargify.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class UsersAggregation(
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    data class UserDocument(
        internal val bookmarkedEvStations: MutableList<String> = mutableListOf()
    )

    fun bookmark(evStationId: String): Task<Void> {
        val currentUser = auth.currentUser?.uid ?: ""
        val userDocumentRef = firestore.collection(USER_COLLECTION).document(currentUser)

        return firestore.runTransaction { transaction ->
            val userDocument = transaction.get(userDocumentRef).toObject<UserDocument>()!!

            // Current Users Must exists
            val bookmarked = userDocument.bookmarkedEvStations

            if (!bookmarked.contains(evStationId)) {
                // Atomically add a new station to the "stations" array field.
                transaction.update(
                    userDocumentRef,
                    BOOKMARKED_EV_STATIONS,
                    FieldValue.arrayUnion(evStationId)
                )
            } else {
                // Atomically remove a station from the "stations" array field.
                transaction.update(
                    userDocumentRef,
                    BOOKMARKED_EV_STATIONS,
                    FieldValue.arrayRemove(evStationId)
                )
            }

            // Success
            null
        }
    }

    companion object {
        private const val TAG = "UsersAggregation"
        private const val USER_COLLECTION = "users"
        private const val BOOKMARKED_EV_STATIONS = "bookmarkedEvStations"
    }
}