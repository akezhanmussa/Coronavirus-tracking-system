package com.example.covidtracerapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.provider.Contacts
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.covidtracerapp.Utils.generateUidNamespace
import kotlinx.android.synthetic.main.activity_main.beaconsRecyclerView
import kotlinx.android.synthetic.main.activity_main.currentUserInfoTv
import kotlinx.android.synthetic.main.beacons_list_item.view.beacon_distance
import kotlinx.android.synthetic.main.beacons_list_item.view.beacon_id
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.RangeNotifier
import org.altbeacon.beacon.Region
import java.util.*


const val REQUEST_ENABLE_BT = 1
const val ALT_BEACON = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"
private const val TAG = "MainActivity"
const val USER_ID = "USER_ID"

class ShowBeaconsActivity : AppCompatActivity(), BeaconConsumer {
    private val beaconManager = BeaconManager.getInstanceForApplication(this)
    var remoteBeaconsIds: MutableSet<Beacon> =
        HashSet()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentUser = intent.getSerializableExtra("USER") as? User
        currentUserInfoTv.text =currentUser.toString()

        verifyBluetooth()
        checkPermission()
        val uid = getsystemID()
        startserviceBroadcast(uid)
    }

    private fun getsystemID(): String {
        val sharedPref =
            getPreferences(Context.MODE_PRIVATE)
        var systemID = sharedPref.getString(sysIdKey, null)
        if (systemID == null) {
            systemID = generateUidNamespace()
        }
        val editor = sharedPref.edit()
        editor.putString(sysIdKey, systemID)
        editor.commit()
        return systemID
    }

    private fun startserviceBroadcast(uid: String) {
        val serviceIntent = Intent(this, BackgroundService::class.java)
        serviceIntent.putExtra(sysIdKey, uid)

//        application.enableMonitoring();
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        val application = this.applicationContext as App
        beaconManager.bind(this)
        application.setMonitoringActivity(this)
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        val builder =
                            AlertDialog.Builder(this)
                        builder.setTitle("This app needs background location access")
                        builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener {
                            requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                PERMISSION_REQUEST_BACKGROUND_LOCATION
                            )
                        }
                        builder.show()
                    } else {
                    }
                }
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        PERMISSION_REQUEST_FINE_LOCATION
                    )
                } else {
                    val builder =
                        AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
            }
        }
        return false
    }

    private fun verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Bluetooth not enabled")
                builder.setMessage("Please enable bluetooth in settings and restart this application.")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    //finish();
                    //System.exit(0);
                }
                builder.show()
            }
        } catch (e: RuntimeException) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Bluetooth LE not available")
            builder.setMessage("Sorry, this device does not support Bluetooth LE.")
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setOnDismissListener {
                //finish();
                //System.exit(0);
            }
            builder.show()
        }
    }

    fun updateLog(log: String?) {
        Log.i("testing_log_adapater", log!!)
    }

    override fun onBeaconServiceConnect() {
        val rangeNotifier = RangeNotifier { beacons, region ->
            if (beacons.isNotEmpty()) {
                Log.d(
                    "test_ble",
                    "didRangeBeaconsInRegion called with beacon count:  " + beacons.size
                )
                val firstBeacon = beacons.iterator().next()
                val beaconLog =
                    "The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.distance + " meters away."
                Log.d("test_ble_Id1", firstBeacon.id1.toString())
                val beaconID = firstBeacon.id1.toString()
                remoteBeaconsIds.add(firstBeacon)

                //                    logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                Log.d("test_ble", beaconLog)
                val nearByBeaconsArray =
                    remoteBeaconsIds.toTypedArray()
                Arrays.sort(nearByBeaconsArray)
                Log.d(
                    "totalnearbybeacons",
                    "" + (nearByBeaconsArray.size - 1).toString()
                )
                val adapter = BeaconsAdapter(
                    remoteBeaconsIds.toList()
                )
                beaconsRecyclerView?.adapter = adapter
            }
        }
        try {
            beaconManager.startRangingBeaconsInRegion(
                Region(
                    "myRangingUniqueId",
                    null,
                    null,
                    null
                )
            )
            beaconManager.addRangeNotifier(rangeNotifier)
        } catch (e: RemoteException) {
        }
    }

    companion object {
        const val CHANNEL_ID = "exampleForegroundServiceChannel"
        private const val PERMISSION_REQUEST_FINE_LOCATION = 1
        private const val PERMISSION_REQUEST_BACKGROUND_LOCATION = 2
        const val sysIdKey = "sharedPrefKey"
    }
}

class BeaconsAdapter (
    private var beacons: List<Beacon>) : RecyclerView.Adapter<BeaconsAdapter.BeaconsViewHolder>() {

    class BeaconsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val beaconId : TextView = itemView.beacon_id
        val beaconDistance: TextView = itemView.beacon_distance
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeaconsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.beacons_list_item,
            parent, false)
        return BeaconsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BeaconsViewHolder, position: Int) {
        val currentItem = beacons[position]
        holder.beaconId.text = currentItem.id1.toString()
        holder.beaconDistance.text = currentItem.distance.toString()
    }

    override fun getItemCount(): Int {
        return beacons.size
    }

    fun updateData(newBeacons : List<Beacon> ){
        beacons = newBeacons
        notifyDataSetChanged()
    }
}




//class MainActivity : AppCompatActivity(), BeaconConsumer {
//
//    private val beaconManager: BeaconManager = BeaconManager.getInstanceForApplication(this)
//    lateinit var bluetoothManager: BluetoothManager
//    lateinit var bluetoothAdapter: BluetoothAdapter
//    private var beaconSet: Set<String>? = HashSet()
//    private val receiver = object : BroadcastReceiver() {
//
//        override fun onReceive(context: Context, intent: Intent) {
//            Log.d(TAG, "ActionName: ${intent.action}")
//            when(intent.action) {
//                BluetoothDevice.ACTION_FOUND -> {
//                    // Discovery has found a device. Get the BluetoothDevice
//                    // object and its info from the Intent.
//                    val device: BluetoothDevice? =
//                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                    val deviceName = device?.name
//                    val deviceHardwareAddress = device?.address // MAC address
//                    Log.d(TAG, "onReceiveBluetooth: $deviceName , $deviceHardwareAddress")
//                }
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(ALT_BEACON))
//
//
//        requestPermissions()
//        if (bluetoothAdapter.isDiscovering) {
//            bluetoothAdapter.cancelDiscovery()
//        }
//
//        beaconManager.bind(this)
//
//        var myUid : String  = getsystemID()
//        startServiceBroadcast(myUid);
//
//    }
//
//    private fun startServiceBroadcast(uid: String) {
//        val serviceIntent = Intent(this, BackgroundService::class.java)
//        serviceIntent.putExtra(USER_ID, uid)
//        ContextCompat.startForegroundService(this, serviceIntent)
//    }
//
//    private fun requestPermissions() {
//
//        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled){
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBtIntent,
//                REQUEST_ENABLE_BT
//            )
//        }
//
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//
//            // Permission is not granted
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//            ) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//                    1
//                )
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Don't forget to unregister the ACTION_FOUND receiver.
//        unregisterReceiver(receiver)
//    }
//
//    override fun onBeaconServiceConnect() {
//        var myRegion = Region("myBeacons", null, null)
//
//        beaconManager.addMonitorNotifier(object:MonitorNotifier{
//            override fun didDetermineStateForRegion(p0: Int, p1: Region?) {
//
//            }
//
//            override fun didEnterRegion(region: Region?) {
//                Log.d(TAG, "didEnterRegion: Beacon Found: ${region?.id1}")
//                beaconManager.startRangingBeaconsInRegion(region!!)
//            }
//
//            override fun didExitRegion(region: Region?) {
//                beaconManager.stopRangingBeaconsInRegion(region!!);
//            }
//        })
//
//        beaconManager.addRangeNotifier { beacons, region ->
//            if (beacons!=null && !beacons.isEmpty()){
//
//            }
//        }
//    }
//
//    private fun getsystemID(): String {
//        val sharedPref: SharedPreferences =
//            getPreferences(Context.MODE_PRIVATE)
//        var systemID: String? = sharedPref.getString(USER_ID, null)
//        if (systemID == null) {
//            systemID = generateUidNamespace()
//        }
//        val editor: SharedPreferences.Editor = sharedPref.edit()
//        editor.putString(USER_ID, systemID)
//        editor.commit()
//        return systemID
//    }
//}