package com.example.covidtracerapp.presentation

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.covidtracerapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import java.io.IOException


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
    }

    override fun onMapReady(googleMap: GoogleMap?) {

    }


    private fun getLatLngOfCity(query: String?): LatLng? {
        val geocoder = Geocoder(this)
        var list: List<Address> = ArrayList()
        try {
            list = geocoder.getFromLocationName(query, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (list.size != 0) {
            val address: Address = list[0]
            val lat: Double = address.latitude
            val lon: Double = address.longitude
            return LatLng(lat, lon)
        }
        return null
    }


}