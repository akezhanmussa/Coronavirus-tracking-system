package com.example.covidtracerapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.covidtracerapp.R
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
                    Log.d("TAGGA", "onCreate: HERERERE")
                    tvPresentInLocalList.text = it.data.toString()
                }
                else -> {
                }
            }
        })

    }

    private fun showToast(id: String?) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
    }
}