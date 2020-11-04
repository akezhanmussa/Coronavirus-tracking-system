package com.example.covidtracerapp

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.covidtracerapp.App.Companion.CHANNEL_ID
import com.example.covidtracerapp.presentation.ShowBeaconsActivity
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.util.*

class BackgroundService : Service() {
    override fun onCreate() {
        super.onCreate()
        val broadcastReceiver = PowerButtonBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        broadcastReceiver?.let { registerReceiver(it, intentFilter) }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val inString = intent.getStringExtra("inputExtra")
        val sysId = intent.getStringExtra(ShowBeaconsActivity.sysIdKey)
        Log.i("myid", "system id recived $sysId")
        val notificationIntent = Intent(this, ShowBeaconsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BleBeamers")
            .setContentText(inString)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

//        if we don't start our service in foreground, our service will be killed in about one minutes.
//        for example, just try to comment startForeground line below and see on the running services via developer options menu in your android emulator/smartphone
        startForeground(Notification.FLAG_ONGOING_EVENT, notification)
        startBroadcast(sysId)
        return START_STICKY //not sticky so this service will only create one service if there is a change or modification
    }

    fun startBroadcast(sysid: String?) {
        Log.d("test_ble", "starting broadcast function")
        val beacon = Beacon.Builder()
            .setId1(sysid)
            .setId2("1") //                .setId3("2")
            .setManufacturer(0x0118) // Radius Networks.  Change this for other beacon layouts
            .setTxPower(-59)
            .setDataFields(Arrays.asList(*arrayOf(0L))) // Remove this for beacon layouts without d: fields
            .build()
        // Change the layout below for other beacon types
        val beaconParser = BeaconParser()
            .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT)
        val beaconTransmitter =
            BeaconTransmitter(applicationContext, beaconParser)
        beaconTransmitter.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                Log.e("test_ble", "Advertisement start failed with code: $errorCode")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Log.i("test_ble", "Advertisement start succeeded.")
                Log.d("test_ble", settingsInEffect.toString())
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    //this is mandatory, but this is only for binding purpose so other components can communicate by binding, but the method that matters
    // is onStartCommand()
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

class PowerButtonBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            //The screen off position
            Toast.makeText(context, "Screen is off", Toast.LENGTH_LONG).show()
            Log.d("test_bles", "screen off")
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            //The screen on position
            Toast.makeText(context, "Screen is on", Toast.LENGTH_LONG).show()
            Log.d("test_bles", "screen on")
        }
    }
}