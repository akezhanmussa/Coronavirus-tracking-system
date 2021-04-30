package com.example.covidtracerapp.geofencing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.covidtracerapp.R
import com.example.covidtracerapp.presentation.MapActivity
import com.example.covidtracerapp.presentation.ShowBeaconsActivity.Companion.ACTION_GEOFENCE_EVENT
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

private val TAG = GeofenceBroadcastReceiver::class.simpleName
private const val NOTIFICATION_ID = 35
private const val NOTIFICATION_CHANNEL_ID = "GeofencingChannel"

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Received")
        if (intent.action == ACTION_GEOFENCE_EVENT){
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent.hasError()){
                val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }

            val geofencingTransition = geofencingEvent.geofenceTransition

            if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
                Log.v(TAG, "Geofence Entered")

                val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager

                notificationManager.sendGeofenceEnteredNotification(context)
            }
        }

    }
}
fun createGeofencingNotificationChannel(context: Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.geofence_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.setShowBadge(false)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = context.getString(R.string.geofence_notification_channel_description)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
fun NotificationManager.sendGeofenceEnteredNotification(context: Context){
    val contentIntent = Intent(context, MapActivity::class.java)
//    contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(context.getString(R.string.geofence_notification_title))
        .setContentText(context.getString(R.string.geofence_notification_text))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(contentPendingIntent)
        .setSmallIcon(R.mipmap.ic_launcher_round)

    notify(NOTIFICATION_ID, builder.build())
}