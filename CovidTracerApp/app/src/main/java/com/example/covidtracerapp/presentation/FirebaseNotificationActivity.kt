package com.example.covidtracerapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.covidtracerapp.R
import kotlinx.android.synthetic.main.activity_firebase_notification.tvLocationLat
import kotlinx.android.synthetic.main.activity_firebase_notification.tvLocationLon
import kotlinx.android.synthetic.main.activity_firebase_notification.tvPresentInLocalList
import kotlinx.android.synthetic.main.activity_firebase_notification.tvReceivedId
import org.koin.android.viewmodel.ext.android.viewModel

class FirebaseNotificationActivity : AppCompatActivity() {

    private val viewModel: FirebaseNotificationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_notification)

        var id = intent.getStringExtra("id")

        tvReceivedId.text = id

        viewModel.checkLocalList(id!!)

        viewModel.localListState.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.data!=null){
                        tvPresentInLocalList.text = "PRESENT in your list"
                        tvLocationLat.text = it.data.lat.toString()
                        tvLocationLon.text = it.data.lon.toString()
                    }else{
                        tvPresentInLocalList.text = "NOT PRESENT in your list"
                    }
                }
                else -> {
                    showToast("error")
                }
            }
        })

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}