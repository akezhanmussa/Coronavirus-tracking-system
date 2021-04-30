package com.example.covidtracerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.covidtracerapp.R
import com.example.covidtracerapp.presentation.ShowBeaconsActivity
import com.example.covidtracerapp.presentation.ShowBeaconsViewModel
import com.example.covidtracerapp.presentation.StatisticsViewModel
import com.example.covidtracerapp.presentation.model.User
import com.example.covidtracerapp.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.activity_statistics.*
import kotlinx.android.synthetic.main.activity_statistics.infectedMinusTv
import kotlinx.android.synthetic.main.activity_statistics.infectedPlusTv
import kotlinx.android.synthetic.main.activity_statistics.recoveredMinusTv
import kotlinx.android.synthetic.main.activity_statistics.recoveredPlusTv
import kotlinx.android.synthetic.main.activity_statistics.userCityTv
import kotlinx.android.synthetic.main.fragment_statistics.*
import org.koin.android.viewmodel.compat.ScopeCompat
import org.koin.android.viewmodel.ext.android.viewModel

class StatisticsFragment : Fragment() {
    private val statsViewModel: StatisticsViewModel by viewModel()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.user.observe(viewLifecycleOwner, Observer {
            val currentUser = it
            statsViewModel.getCovidCasesByLocation(currentUser.location.city, currentUser.location.country)
            userCityTv.text = "My Location: ${currentUser?.location?.city}, ${currentUser?.location?.country}"
        })
//        currentUser?.let {
//            statsViewModel.getCovidCasesByLocation(currentUser.location.city,
//                currentUser.location.country)
//        }

        statsViewModel.stats.observe(viewLifecycleOwner, Observer {
//            userCityTv.text = currentUser?.location?.city
            infectedPlusTv.text = it.gotSickNumPCRplus.toString()
            infectedMinusTv.text = it.gotSickNumPCRminus.toString()
            recoveredPlusTv.text = it.recoveredNumPCRplus.toString()
            recoveredMinusTv.text = it.recoverdNumPCRminus.toString()
            covid_date.text = it.message.removePrefix("*данные на ")

            vaccinated.text = it.vaccinatedNum.toString()
            double_vaccinated.text = it.doubleVaccinatedNum.toString()
            vaccinated_date.text = it.vaccinatedMessage.removePrefix("*данные на ")
        })
    }
}