package com.example.covidtracerapp

import org.altbeacon.beacon.AltBeacon
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.simulator.BeaconSimulator
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Created by Matt Tyler on 4/18/14.
 */
class TimedBeaconSimulator : BeaconSimulator {
    private var beacons: MutableList<Beacon> = ArrayList()

    private val TAG = "TimedBeaconSimulator"
    var USE_SIMULATED_BEACONS = true

    /*
     * You may simulate detection of beacons by creating a class like this in your project.
     * This is especially useful for when you are testing in an Emulator or on a device without BluetoothLE capability.
     *
     * Uncomment the lines in BeaconReferenceApplication starting with:
     *     // If you wish to test beacon detection in the Android Emulator, you can use code like this:
     * Then set USE_SIMULATED_BEACONS = true to initialize the sample code in this class.
     * If using a Bluetooth incapable test device (i.e. Emulator), you will want to comment
     * out the verifyBluetooth() in MonitoringActivity.java as well.
     *
     * Any simulated beacons will automatically be ignored when building for production.
     */

    /**
     * Required getter method that is called regularly by the Android Beacon Library.
     * Any beacons returned by this method will appear within your test environment immediately.
     */
    override fun getBeacons(): List<Beacon> {
        return beacons
    }

    /**
     * Creates simulated beacons all at once.
     */
    fun createBasicSimulatedBeacons() {
        if (USE_SIMULATED_BEACONS) {
            val beacon1 = AltBeacon.Builder().setId1("010101000002")
                    .setId2("1").setId3("1").setRssi(getInt()).setTxPower(-55).build()
            val beacon2 = AltBeacon.Builder().setId1("010101000004")
                    .setId2("1").setId3("2").setRssi(getInt()).setTxPower(-55).build()
            val beacon3 = AltBeacon.Builder().setId1("010101000001")
                    .setId2("1").setId3("3").setRssi(getInt()).setTxPower(-55).build()
            val beacon4 = AltBeacon.Builder().setId1("010101000003")
                    .setId2("1").setId3("4").setRssi(getInt()).setTxPower(-55).build()
            beacons.add(beacon1)
            beacons.add(beacon2)
            beacons.add(beacon3)
            beacons.add(beacon4)
        }
    }
    private fun getInt(): Int{
        return -Random.nextInt(72,120)
    }

}