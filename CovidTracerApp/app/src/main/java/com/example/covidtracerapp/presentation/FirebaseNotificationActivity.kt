package com.example.covidtracerapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.example.covidtracerapp.R
import kotlinx.android.synthetic.main.activity_firebase_notification.*
import org.koin.android.viewmodel.ext.android.viewModel

class FirebaseNotificationActivity : AppCompatActivity() {

    private val viewModel: FirebaseNotificationViewModel by viewModel()
    private val TAG = FirebaseNotificationActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_notification)

        var id = intent.getStringExtra("id")

        tvReceivedId.text = id

        viewModel.checkLocalList(id!!)

        viewModel.localListState.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    textView3.isVisible = true
                    textView4.isVisible = true
                    Log.v(TAG, "Resource.Success")
                    tvLocationLat.text = it.data.lat.toString()
                    tvLocationLon.text = it.data.lon.toString()
                }
                else -> {
                    Log.v(TAG, "Resource.Fail")
                    textView3.isVisible = false
                    textView4.isVisible = false
                }
            }
        })

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}