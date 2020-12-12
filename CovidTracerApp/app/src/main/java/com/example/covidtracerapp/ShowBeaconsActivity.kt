package com.example.covidtracerapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covidtracerapp.Utils.generateUidNamespace
import com.example.covidtracerapp.database.ContactedDAO
import com.example.covidtracerapp.database.ContactedDatabase
import com.example.covidtracerapp.database.ContactedEntity
import kotlinx.android.synthetic.main.activity_main.beaconsRecyclerView
import kotlinx.android.synthetic.main.activity_main.currentUserInfoTv
import kotlinx.android.synthetic.main.beacons_list_item.view.beacon_distance
import kotlinx.android.synthetic.main.beacons_list_item.view.beacon_id
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.RangeNotifier
import org.altbeacon.beacon.Region
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


const val REQUEST_ENABLE_BT = 1
const val ALT_BEACON = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"
private const val TAG = "MainActivity"
const val USER_ID = "USER_ID"

class ShowBeaconsActivity : AppCompatActivity(), BeaconConsumer {

    private val viewModel : ShowBeaconsViewModel by viewModel()

    private val beaconManager = BeaconManager.getInstanceForApplication(this)
    var remoteBeaconsIds: MutableSet<MyBeacon> = mutableSetOf()
    private var adapter: BeaconsAdapter = BeaconsAdapter(listOf())

    private lateinit var contactedDatabase: ContactedDatabase
    private lateinit var contactedDao: ContactedDAO
    private val timedSimulator = TimedBeaconSimulator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contactedDatabase = ContactedDatabase.getsInstance(this)
        contactedDao = contactedDatabase.contactedDAO()
        val currentUser = intent.getSerializableExtra("USER") as? User
        currentUserInfoTv.text =currentUser.toString()

        beaconsRecyclerView.layoutManager = LinearLayoutManager(this)
        beaconsRecyclerView.adapter = adapter

        verifyBluetooth()
        checkPermission()
        val uid = getsystemID()
        startserviceBroadcast(uid)
        viewModel.startTracing()

        viewModel.listOfPositive.observe(this, androidx.lifecycle.Observer {
            Toast.makeText(applicationContext, "" + it, Toast.LENGTH_SHORT).show()
        })

        //simulator code
        BeaconManager.setBeaconSimulator(timedSimulator)
        timedSimulator.createBasicSimulatedBeacons()
        timedSimulator.beacons.get(0).toMyBeacon()
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
            Log.d(TAG, "onBeaconServiceConnect: RANGING")
            if (beacons.isNotEmpty()) {
                remoteBeaconsIds.clear()
                Log.d(TAG, "onBeaconServiceConnect: TOTAL: ${beacons.size}")
                for (beacon in beacons){
                    val myb = beacon.toMyBeacon()
                    if (remoteBeaconsIds.contains(myb)){
                        remoteBeaconsIds.remove(myb)
                    }
                    remoteBeaconsIds.add(myb)
                    contactedDao.insertContacted(ContactedEntity(Calendar.getInstance().time))
                    Log.d(TAG, "Inserted: " + myb.id1.toString())
                }



                adapter.updateList(remoteBeaconsIds.toMutableList())
            }else{
                Log.d(TAG, "onBeaconServiceConnect: ISEMPTY")
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
    private var beacons: List<MyBeacon>) : RecyclerView.Adapter<BeaconsAdapter.BeaconsViewHolder>() {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<MyBeacon>() {

            override fun areItemsTheSame(oldItem: MyBeacon, newItem: MyBeacon): Boolean {
                return oldItem.id1 == newItem.id1
            }

            override fun areContentsTheSame(oldItem: MyBeacon, newItem: MyBeacon): Boolean {
                return oldItem.distance == newItem.distance
            }
        }
    }

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
        holder.beaconId.text = currentItem.id1.toString().substring(2)
        holder.beaconDistance.text = String.format("%.1f", currentItem.distance) + " cm"
    }

    override fun getItemCount(): Int {
        return beacons.size
    }

    fun updateList(newList: List<MyBeacon>){
        beacons = newList.sortedBy { it.id1 }
        notifyDataSetChanged()
    }
}


