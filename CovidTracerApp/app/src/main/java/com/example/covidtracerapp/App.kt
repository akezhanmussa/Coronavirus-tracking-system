package com.example.covidtracerapp

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.covidtracerapp.di.authModule
import com.example.covidtracerapp.di.covidModule
import com.example.covidtracerapp.presentation.ShowBeaconsActivity
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.powersave.BackgroundPowerSaver
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application(), BootstrapNotifier {
    private var regionBootstrap: RegionBootstrap? = null
    private var backgroundPowerSaver: BackgroundPowerSaver? = null
    private var haveDetectedBeaconsSinceBoot = false
    private var monitoringActivity: ShowBeaconsActivity? = null
    var log = ""
        private set

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(covidModule, authModule)
        }

        val beaconManager =
            BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers
            .add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT))
        BeaconManager.setDebug(true)
        val builder = Notification.Builder(this)
        builder.setSmallIcon(R.drawable.ic_launcher_background)
        builder.setContentTitle("Scanning for Beacons")
        val intent = Intent(this, ShowBeaconsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "My Notification Channel ID",
                "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "My Notification Channel Description"
            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channel.id)
        }
        beaconManager.enableForegroundServiceScanning(builder.build(), 456)
        beaconManager.setEnableScheduledScanJobs(false)
        //        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.backgroundScanPeriod = 200
        beaconManager.backgroundBetweenScanPeriod = 200L
        beaconManager.foregroundScanPeriod = 100
        //        beaconManager.setForegroundBetweenScanPeriod(0);
        val region = Region(
            "backgroundRegion",
            null, null, null
        )
        regionBootstrap = RegionBootstrap(this, region)

        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        backgroundPowerSaver = BackgroundPowerSaver(this)
        createNotificationChannel()
        enableMonitoring()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    fun disableMonitoring() {
        if (regionBootstrap != null) {
            regionBootstrap!!.disable()
            regionBootstrap = null
        }
    }

    fun enableMonitoring() {
        val region = Region(
            "backgroundRegion",
            null, null, null
        )
        regionBootstrap = RegionBootstrap(this, region)
    }

    override fun didEnterRegion(arg0: Region) {
        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        Log.d(TAG, "did enter region.")
        if (!haveDetectedBeaconsSinceBoot) {
            Log.d(TAG, "auto launching MainActivity")

            // The very first time since boot that we detect an beacon, we launch the
            // MainActivity
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Important:  make sure to add android:launchMode="singleInstance" in the manifest
            // to keep multiple copies of this activity from getting created if the user has
            // already manually launched the app.
//            this.startActivity(intent);
            haveDetectedBeaconsSinceBoot = true
        } else {
            if (monitoringActivity != null) {
                // If the Monitoring Activity is visible, we log info about the beacons we have
                // seen on its display
                logToDisplay("I see a beacon again")
            } else {
                // If we have already seen beacons before, but the monitoring activity is not in
                // the foreground, we send a notification to the user on subsequent detections.
                Log.d(TAG, "Sending notification.")
                sendNotification()
            }
        }
    }

    override fun didExitRegion(region: Region) {
        logToDisplay("I no longer see a beacon.")
    }

    override fun didDetermineStateForRegion(
        state: Int,
        region: Region
    ) {
        logToDisplay("Current region state is: " + if (state == 1) "INSIDE" else "OUTSIDE ($state)")
    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this)
            .setContentTitle("Beacon Reference Application")
            .setContentText("An beacon is nearby.")
            .setSmallIcon(R.drawable.ic_launcher_background)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(Intent(this, ShowBeaconsActivity::class.java))
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(resultPendingIntent)
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }

    fun setMonitoringActivity(activity: ShowBeaconsActivity?) {
        monitoringActivity = activity
    }

    private fun logToDisplay(line: String) {
        log += """
            $line
            
            """.trimIndent()
        if (monitoringActivity != null) {
            monitoringActivity?.updateLog(log)
        }
    }

    companion object {
        const val CHANNEL_ID = "exampleForegroundServiceChannel"
        const val TAG = "BeaconReferenceApp"
    }
}