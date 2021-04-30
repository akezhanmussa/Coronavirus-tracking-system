package com.example.covidtracerapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.covidtracerapp.R
import com.example.covidtracerapp.presentation.MapViewModel
import com.example.covidtracerapp.presentation.Resource
import com.example.covidtracerapp.presentation.ShowBeaconsActivity
import com.example.covidtracerapp.presentation.USER_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.android.viewmodel.ext.android.viewModel

class MapsFragment : Fragment() {

    private val mapViewModel: MapViewModel by activityViewModels()

    private var locationPermissionGranted: Boolean = false
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var gMap: GoogleMap? = null

    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val ZOOM_DEFAULT = 5f

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        gMap = googleMap

        val permissions = arrayOf<String>(FINE_LOCATION, COARSE_LOCATION)

        if (locationPermissionGranted){
            if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        context as Activity,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE
                )
                return@OnMapReadyCallback
            }

        }

        val location = fusedLocationProviderClient.lastLocation
        location.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result!=null) {
                val currentLocation = task.result as android.location.Location
                val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                gMap?.isMyLocationEnabled = true
            }
        }

        mapViewModel.getHotspotsByLocation(USER_LOCATION!!)

        mapViewModel.locationsState.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    addCircles(it.data)
                }
                is Resource.Error -> showToast(it.message)
                else -> {
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        mapViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private fun refreshHotspots(){
        mapViewModel.refreshHotspots(USER_LOCATION!!)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
    private fun addCircles(data: List<com.example.covidtracerapp.presentation.model.HotSpotCoordinate>) {
        gMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                                data[0].latitude,
                                data[0].longitude
                        ), 10f
                )
        )

        gMap?.clear()

        for (hotspot in data){
            Log.d(TAG, "addCircles: adding Circle + lat: " + hotspot.latitude + " lon: " + hotspot.longitude)
//
//           gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(data[i].longitude, data[i].latitude), 25f))
            if (hotspot.radius > 0){
                Log.d(TAG, "addCircles: FOUND RADIUS > 0 " + hotspot.radius)
                gMap!!.addCircle(
                        CircleOptions()
                                .center(LatLng(hotspot.latitude, hotspot.longitude))
                                .radius(hotspot.radius.toDouble())
                                .strokeColor(Color.RED)
                                .fillColor(Color.RED)
                )

                val markerOptions = MarkerOptions()
                markerOptions.position(LatLng(hotspot.latitude, hotspot.longitude))
                gMap?.addMarker(markerOptions.title("Cases: ${hotspot.cases ?: 0}"))
            }else{
                Log.d(TAG, "addCircles: FOUND RADIUS = 0" + hotspot.latitude + " long:" + hotspot.longitude)
            }

        }

//        showToast("Hotspots were refreshed")

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