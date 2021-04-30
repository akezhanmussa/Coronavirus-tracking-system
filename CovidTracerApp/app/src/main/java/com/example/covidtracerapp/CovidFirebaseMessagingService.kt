package com.example.covidtracerapp

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.covidtracerapp.presentation.ShowBeaconsActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CovidFirebaseMessagingService : FirebaseMessagingService() {

    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        // TODO: If the received id is the same as this user's id -> DO NOT SEND NOTIFICATION
        if (remoteMessage.data.isNotEmpty() && remoteMessage.data["id"] != ShowBeaconsActivity.USER_ID) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            val intent = Intent("FirebaseNotification")
            intent.putExtra("id", remoteMessage.data["id"])
            Log.d(TAG, "onMessageReceived: BROADCASTING" + remoteMessage.data["id"] )
            broadcaster?.sendBroadcast(intent)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

    }


    companion object {

        private const val TAG = "CovidMessagingService"
    }


}