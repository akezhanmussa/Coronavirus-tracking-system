package com.example.covidtracerapp.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.Image
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covidtracerapp.App
import com.example.covidtracerapp.BackgroundService
import com.example.covidtracerapp.R
import com.example.covidtracerapp.TimedBeaconSimulator
import com.example.covidtracerapp.Utils
import com.example.covidtracerapp.Utils.generateUidNamespace
import com.example.covidtracerapp.database.ContactedEntity
import com.example.covidtracerapp.geofencing.GeofenceBroadcastReceiver
import com.example.covidtracerapp.geofencing.createGeofencingNotificationChannel
import com.example.covidtracerapp.presentation.model.Location
import com.example.covidtracerapp.presentation.model.MyBeacon
import com.example.covidtracerapp.presentation.model.User
import com.example.covidtracerapp.presentation.model.toMyBeacon
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.ktx.messaging
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
var USER_LOCATION : Location? = null

class ShowBeaconsActivity : AppCompatActivity(), BeaconConsumer {

    private val mapViewModel: MapViewModel by viewModel()
    private val viewModel : ShowBeaconsViewModel by viewModel()
    private val timedSimulator = TimedBeaconSimulator()

    private var locationPermissionGranted: Boolean = false
    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val LOCATION_PERMISSION_REQUEST_CODE = 1


    private val beaconManager = BeaconManager.getInstanceForApplication(this)
    var remoteBeaconsIds: MutableSet<MyBeacon> = mutableSetOf()
    private var adapter: BeaconsAdapter =
        BeaconsAdapter(listOf())
    private var contactedSet: MutableSet<String> = mutableSetOf()

    //ActivityResultAPI
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            Log.i(TAG, "Permission is Granted.")
        }
        else
            Log.i(TAG, "Permission is not Granted.")
    }

    //Geofencing
    private var hotspots = listOf<com.example.covidtracerapp.presentation.model.HotSpotCoordinate>()
    private lateinit var geofencingClient: GeofencingClient
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            var id = intent.extras?.getString("id")
            Log.d(TAG, "onReceive: RECEIVED $id")
            createNotification(id)
        }
    }

    private fun createNotification(id: String?) {

        createNotificationChannel()

        val intent = Intent(this, FirebaseNotificationActivity::class.java)
        intent.putExtra("id", id)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_coronavirus_24)
            .setContentTitle("Covid Notification")
            .setContentText("Someone with id $id got infected")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_11"
            val descriptionText = "our channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter("FirebaseNotification"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentUser = intent.getSerializableExtra("USER") as? User

        currentUserInfoTv.text = Html.fromHtml("<b>" + "User ID: " + "</b>" + currentUser?.id.toString() + "<br/>" + "<b>" + "Telephone: " + "</b>" + currentUser?.phone  +
                                                        "<br/>" + "<b>" + "Country: " + "</b>" + currentUser?.location!!.country + "<br/>" + "<b>" + "City: " + "</b>" + currentUser?.location!!.city +
                                                         "<br/>" + "<b>" + "Status: " + "</b>" + currentUser?.positive)
        USER_CITY = currentUser?.location!!.city
        USER_COUNTRY = currentUser?.location!!.country
        USER_ID = currentUser!!.id
        USER_LOCATION = currentUser!!.location

        beaconsRecyclerView.layoutManager = LinearLayoutManager(this)
        beaconsRecyclerView.adapter = adapter

        verifyBluetooth()
        checkPermission()
        val uid = currentUser!!.id
        startserviceBroadcast(uid)

        //Geofencing TODO: Geofences should be added in the background, not in onCreate
        createGeofencingNotificationChannel(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        mapViewModel.getHotspotsByLocation(USER_LOCATION!!)
        mapViewModel.locationsState.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    hotspots = it.data
                    addGeofences(hotspots)
                }
                is Resource.Error -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                else -> {
                }
            }
        })
        if (!checkBackgroundLocationPermission()) requestBackgroundLocationPermission()

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

        imgMap.setOnClickListener{
            var intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        var simulate = false
        if(simulate) {
            BeaconManager.setBeaconSimulator(timedSimulator)
            timedSimulator.createBasicSimulatedBeacons()
            timedSimulator.beacons[0].toMyBeacon()
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



        Firebase.messaging.subscribeToTopic(Utils.ALL_DEVICES_TOPIC)
            .addOnCompleteListener { task ->
                var msg = getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                    msg = getString(R.string.msg_subscribe_failed)
                }
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        activityResultLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }
    private fun checkBackgroundLocationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return true
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("InlinedApi")
    private fun requestBackgroundLocationPermission(){
        Snackbar.make(
                findViewById(android.R.id.content),
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE
        )
                .setAction(R.string.settings) {
                    // Displays App settings screen.
//                startActivity(Intent().apply {
//                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                })
                    activityResultLauncher.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }.show()
    }
    @SuppressLint("MissingPermission")
    private fun addGeofences(listOfHotspots : List<com.example.covidtracerapp.presentation.model.HotSpotCoordinate>){
        // TODO: Check for both fine and background ?
        if (!checkLocationPermissions()) return
        val listOfGeofences = mutableListOf<Geofence>()

        for (hotspot in listOfHotspots){
            Log.d(TAG, "addGeofences: adding Geofence" + hotspot.latitude + " - " + hotspot.longitude)
            val geofence = Geofence.Builder()
                    .setRequestId(LatLng(hotspot.latitude, hotspot.longitude).toString())
                    .setCircularRegion(hotspot.latitude, hotspot.longitude, hotspot.radius.toFloat())
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build()
            listOfGeofences.add(geofence)
        }

        val geofenceRequest = GeofencingRequest.Builder()
                .addGeofences(listOfGeofences)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build()

        geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.i(TAG, "addGeofences: Success")
            }
            addOnFailureListener {
                Log.i(TAG, "addGeofences: Failure")
            }
        }
    }
    private fun updateUserUi(currentUser: User){
        USER_CITY = currentUser?.location!!.city
        USER_COUNTRY = currentUser?.location!!.country
        currentUserInfoTv.text = Html.fromHtml("<b>" + "User ID: " + "</b>" + currentUser?.id.toString() + "<br/>" + "<b>" + "Telephone: " + "</b>" + currentUser?.phone  +
            "<br/>" + "<b>" + "Country: " + "</b>" + USER_COUNTRY + "<br/>" + "<b>" + "City: " + "</b>" + USER_CITY +
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
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestLocationPermissions()
            } else {
                val builder =
                    AlertDialog.Builder(this)
                builder.setTitle("Functionality limited")
                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.")
                builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                    requestLocationPermissions()
                }
                builder.setOnDismissListener { }
                builder.show()
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

    fun getCurrentLocation(): LatLng?{

        val permissions = arrayOf<String>(FINE_LOCATION, COARSE_LOCATION)
        if (locationPermissionGranted){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }

        }

        val location = fusedLocationProviderClient?.lastLocation
        var latLng: LatLng? = null
        location?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentLocation = task.result as android.location.Location
                LatLng(currentLocation.latitude, currentLocation.longitude).also { latLng = it }
            }
        }

        return latLng
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

                        val contactedLocation = getCurrentLocation()
                        val lat = contactedLocation?.latitude
                        val lon = contactedLocation?.longitude

                        viewModel.insertContacted(ContactedEntity(myb.id1.toString().substring(2,14),lat, lon, Calendar.getInstance().time))
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
        internal const val ACTION_GEOFENCE_EVENT = "ACTION_GEOFENCE_EVENT"
        const val NOTIFICATION_ID = 999
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


