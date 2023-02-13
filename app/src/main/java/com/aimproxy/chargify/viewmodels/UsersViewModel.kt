package com.aimproxy.chargify.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aimproxy.chargify.firestore.UsersAggregation
import kotlinx.coroutines.launch

class UsersViewModel(application: Application) : AndroidViewModel(application) {
    private val usersService = UsersAggregation()
    private val context = application.applicationContext

    fun bookmarkEvStation(evStationId: Int) {
        viewModelScope.launch {
            usersService.bookmark(evStationId.toString())
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "You just saved that Ev Station!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Sorry, I failed to save that Ev Station!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.w(TAG, e.message, e)
                }
        }
    }

    companion object {
        private val TAG = "UsersViewModel"
    }
}