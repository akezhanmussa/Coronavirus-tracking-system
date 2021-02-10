package com.example.covidtracerapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.example.covidtracerapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import org.koin.android.viewmodel.ext.android.viewModel


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val mapViewModel: MapViewModel by viewModel()

    private var locationPermissionGranted: Boolean = false
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var gMap: GoogleMap? = null

    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val ZOOM_DEFAULT = 5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        gMap = googleMap

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
                return
            }

        }

        val location = fusedLocationProviderClient.lastLocation
        location.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentLocation = task.result as Location
                val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 25f))
                gMap?.isMyLocationEnabled = true

                val circle: Circle = gMap!!.addCircle(
                    CircleOptions()
                        .center(latLng)
                        .radius(1000.0)
                        .strokeColor(Color.RED)
                        .fillColor(Color.RED)
                )
            }
        }

        mapViewModel.getLocationsByCity(USER_CITY)

        mapViewModel.locationsState.observe(this, Observer {
            when(it){
                is Resource.Success -> {
                    addCircles(it.data)
                }
                is Resource.Error -> showToast(it.message)
                else -> {}
            }
        })
    }


    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun addCircles(data: List<com.example.covidtracerapp.presentation.model.Location>) {
        gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(data[0].longitude, data[0].latitude), 25f))

        for (i in 0..15){
            Log.d(TAG, "addCircles: adding Circle")
//            gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(data[i].longitude, data[i].latitude), 25f))
            gMap!!.addCircle(
                CircleOptions()
                    .center(LatLng(data[i].longitude, data[i].latitude))
                    .radius(data[i].radius.toDouble())
                    .strokeColor(Color.RED)
                    .fillColor(Color.RED)
            )
        }

//        for(location in data){
//
//            Log.d(TAG, "addCircles: adding Circle")
//            gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.longitude, location.latitude), 25f))
//            gMap!!.addCircle(
//                CircleOptions()
//                    .center(LatLng(location.longitude, location.latitude))
//                    .radius(location.radius.toDouble())
//                    .strokeColor(Color.RED)
//                    .fillColor(Color.RED)
//            )
//        }
    }

    companion object {
        private const val TAG = "MapActivity"
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5
    }


}