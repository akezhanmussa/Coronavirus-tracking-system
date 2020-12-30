package com.example.covidtracerapp.presentation

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covidtracerapp.App
import com.example.covidtracerapp.BackgroundService
import com.example.covidtracerapp.R
import com.example.covidtracerapp.TimedBeaconSimulator
import com.example.covidtracerapp.Utils.generateUidNamespace
import com.example.covidtracerapp.database.ContactedEntity
import com.example.covidtracerapp.presentation.model.MyBeacon
import com.example.covidtracerapp.presentation.model.User
import com.example.covidtracerapp.presentation.model.toMyBeacon
import kotlinx.android.synthetic.main.activity_login.loaderLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.beacons_list_item.view.*
import org.altbeacon.beacon.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


const val REQUEST_ENABLE_BT = 1
const val ALT_BEACON = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"
private const val TAG = "MainActivity"
var USER_ID = "USER_ID"
var USER_CITY : String = ""
var USER_COUNTRY : String = ""

class ShowBeaconsActivity : AppCompatActivity(), BeaconConsumer {

    private val viewModel : ShowBeaconsViewModel by viewModel()
    private val timedSimulator = TimedBeaconSimulator()

    private val beaconManager = BeaconManager.getInstanceForApplication(this)
    var remoteBeaconsIds: MutableSet<MyBeacon> = mutableSetOf()
    private var adapter: BeaconsAdapter =
        BeaconsAdapter(listOf())
    private var contactedSet: MutableSet<String> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentUser = intent.getSerializableExtra("USER") as? User

        currentUserInfoTv.text = Html.fromHtml("<b>" + "User ID: " + "</b>" + currentUser?.id.toString() + "<br/>" + "<b>" + "Telephone: " + "</b>" + currentUser?.phone  +
                                                        "<br/>" + "<b>" + "Country: " + "</b>" + currentUser?.country + "<br/>" + "<b>" + "City: " + "</b>" + currentUser?.city +
                                                         "<br/>" + "<b>" + "Status: " + "</b>" + currentUser?.positive)
        USER_CITY = currentUser!!.city
        USER_COUNTRY = currentUser!!.country
        USER_ID = currentUser!!.id

        beaconsRecyclerView.layoutManager = LinearLayoutManager(this)
        beaconsRecyclerView.adapter = adapter

        verifyBluetooth()
        checkPermission()
        val uid = currentUser!!.id
        startserviceBroadcast(uid)

        sendData.setOnClickListener {
            viewModel.startTracing(
                USER_CITY,
                USER_COUNTRY
            )
        }

        selfReveal.setOnClickListener {
            viewModel.selfReveal(
                USER_ID
            )
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.updateUser(USER_ID)
            swipeRefresh.isRefreshing = false
        }

        var simulate = false
        if(simulate) {
            BeaconManager.setBeaconSimulator(timedSimulator)
            timedSimulator.createBasicSimulatedBeacons()
            timedSimulator.beacons.get(0).toMyBeacon()
        }

        viewModel.userState.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    updateUserUi(it.data)
                }

                is Resource.Error -> showError(it.message)
            }
        })

        viewModel.listOfPositive.observe(this, androidx.lifecycle.Observer {
            var users = ""
            for (user in it){
                users = users + user + "\n\n"
            }
            Toast.makeText(applicationContext, users, Toast.LENGTH_LONG).show()
        })

        viewModel.intersection.observe(this, androidx.lifecycle.Observer {
            var users = ""
            for (user in it){
                users = users + user + "\n\n"
            }
            Toast.makeText(applicationContext, users, Toast.LENGTH_LONG).show()
        })
    }

    private fun updateUserUi(currentUser: User){
        USER_CITY = currentUser.city
        USER_COUNTRY = currentUser.country
        currentUserInfoTv.text = Html.fromHtml("<b>" + "User ID: " + "</b>" + currentUser?.id.toString() + "<br/>" + "<b>" + "Telephone: " + "</b>" + currentUser?.phone  +
            "<br/>" + "<b>" + "Country: " + "</b>" + currentUser?.country + "<br/>" + "<b>" + "City: " + "</b>" + currentUser?.city +
            "<br/>" + "<b>" + "Status: " + "</b>" + currentUser?.positive)
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
        val zeros = "00000000"
        serviceIntent.putExtra(sysIdKey, uid + zeros)

//        application.enableMonitoring();
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        val application = this.applicationContext as App
        beaconManager.bind(this)
        application.setMonitoringActivity(this)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
                    var myb = beacon.toMyBeacon()
                    if (remoteBeaconsIds.contains(myb)){
                        remoteBeaconsIds.remove(myb)
                    }
                    if(myb.distance < 3 && !contactedSet.contains(myb.id1.toString())) {
                        contactedSet.add(myb.id1.toString())
                        viewModel.insertContacted(ContactedEntity(myb.id1.toString().substring(2,14), Calendar.getInstance().time))
                        Log.d(TAG, "Inserted: " + myb.id1.toString().substring(2,14))
                    }
                    remoteBeaconsIds.add(myb)
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
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.beacons_list_item,
            parent, false)
        return BeaconsViewHolder(
            itemView
        )
    }

    override fun onBindViewHolder(holder: BeaconsViewHolder, position: Int) {
        val currentItem = beacons[position]
        holder.beaconId.text = currentItem.id1.toString().substring(2, 14)
        holder.beaconDistance.text = String.format("%.1f", currentItem.distance) + " m"
    }

    override fun getItemCount(): Int {
        return beacons.size
    }

    fun updateList(newList: List<MyBeacon>){
        beacons = newList.sortedBy { it.id1 }
        notifyDataSetChanged()
    }
}


