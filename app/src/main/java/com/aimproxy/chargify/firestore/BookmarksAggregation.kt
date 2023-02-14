package com.aimproxy.chargify.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class BookmarksAggregation(
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    data class BookmarkedEvStation(
        @DocumentId val stationId: String = "",
        val operatorInfo: String? = null,
        val addressInfo: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
    )

    private val currentUser = auth.currentUser?.uid ?: ""

    val bookmarks: Flow<List<BookmarkedEvStation>> =
        firestore.collection(USER_COLLECTION)
            .document(currentUser)
            .collection(BOOKMARKED_EV_STATIONS)
            .snapshots()
            .map { snapshot -> snapshot.toObjects() }

    suspend fun isBookmarked(stationId: String): Boolean =
        firestore.collection(USER_COLLECTION)
            .document(currentUser)
            .collection(BOOKMARKED_EV_STATIONS)
            .document(stationId)
            .get().await().exists()

    suspend fun get(stationId: String): BookmarkedEvStation? =
        firestore.collection(USER_COLLECTION)
            .document(currentUser)
            .collection(BOOKMARKED_EV_STATIONS)
            .document(stationId)
            .get().await().toObject()


    suspend fun save(evStation: BookmarkedEvStation) {
        firestore.collection(USER_COLLECTION)
            .document(currentUser)
            .collection(BOOKMARKED_EV_STATIONS)
            .add(evStation)
            .await()
    }

    suspend fun delete(stationId: String) {
        firestore.collection(USER_COLLECTION)
            .document(currentUser)
            .collection(BOOKMARKED_EV_STATIONS)
            .document(stationId)
            .delete()
            .await()
    }

    companion object {
        private const val TAG = "UsersAggregation"
        private const val USER_COLLECTION = "users"
        private const val BOOKMARKED_EV_STATIONS = "bookmarks"
    }
}