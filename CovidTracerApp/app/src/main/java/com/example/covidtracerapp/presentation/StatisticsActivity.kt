package com.example.covidtracerapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.covidtracerapp.R
import com.example.covidtracerapp.presentation.model.User
import kotlinx.android.synthetic.main.activity_statistics.*
import org.koin.android.viewmodel.ext.android.viewModel

class StatisticsActivity : AppCompatActivity(){

    private val statsViewModel: StatisticsViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        val currentUser = intent.getSerializableExtra("USER") as? User
        currentUser?.let {
            statsViewModel.getCovidCasesByLocation(currentUser.location.city,
                currentUser.location.country)
        }

        statsViewModel.stats.observe(this, Observer {
                userCityTv.text = currentUser?.location?.city
                infectedPlusTv.text = it.gotSickNumPCRplus.toString()
                infectedMinusTv.text = it.gotSickNumPCRminus.toString()
                recoveredPlusTv.text = it.recoveredNumPCRplus.toString()
                recoveredMinusTv.text = it.recoverdNumPCRminus.toString()
        })

    }
}
