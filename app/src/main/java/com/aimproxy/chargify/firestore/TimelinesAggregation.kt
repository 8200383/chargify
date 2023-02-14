package com.aimproxy.chargify.firestore

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class TimelinesAggregation(
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    data class LastKnownEvStation(
        @DocumentId internal var stationId: Int? = 0,
        internal var timestamp: Timestamp? = Timestamp.now(),
        internal var addressInfo: String? = null,
    )

    private val currentUser = auth.currentUser?.uid ?: ""

    val timeline: Flow<List<LastKnownEvStation>> =
        firestore.collection(USER_COLLECTION)
            .document(currentUser)
            .collection(TIMELINE_COLLECTION)
            .snapshots()
            .map { snapshot -> snapshot.toObjects() }

    suspend fun save(evStation: LastKnownEvStation) {
        firestore.collection(USER_COLLECTION)
            .document(currentUser)
            .collection(TIMELINE_COLLECTION)
            .add(evStation)
            .await()
    }

    companion object {
        private const val TAG = "TimelinesAggregation"
        private const val USER_COLLECTION = "users"
        private const val TIMELINE_COLLECTION = "timeline"
    }
}