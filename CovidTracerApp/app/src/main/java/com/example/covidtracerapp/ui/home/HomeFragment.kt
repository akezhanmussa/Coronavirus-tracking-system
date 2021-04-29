package com.example.covidtracerapp.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covidtracerapp.R
import com.example.covidtracerapp.presentation.ShowBeaconsActivity
import com.example.covidtracerapp.presentation.ShowBeaconsViewModel
import com.example.covidtracerapp.presentation.StatisticsViewModel
import com.example.covidtracerapp.presentation.model.MyBeacon
import com.example.covidtracerapp.presentation.model.User
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.table_row_layout.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel : ShowBeaconsViewModel by activityViewModels()
    private var adapter: BeaconsAdapter = BeaconsAdapter(listOf())
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var USER_ID = "User ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.user.observe(viewLifecycleOwner, Observer {
            val currentUser = it
            userIdTv.text = currentUser?.id
            USER_ID = currentUser.id
            userPhoneTv.text = currentUser?.phone
            userCountryTv.text = currentUser?.location?.country
            userCityTv.text = currentUser?.location?.city
            userStatusTv.text = when(currentUser?.positive){
                true -> {
                    userStatusTv.setTextColor(Color.parseColor("#FF0000"))
                    userStatusTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coronavirus, 0, 0, 0);
                    "Infected"
                }
                false -> {
                    userStatusTv.setTextColor(Color.parseColor("#8BC34A"))
                    userStatusTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_healthy, 0, 0, 0);
                    "Not infected"
                }
                null -> throw UnsupportedOperationException()
            }
        })

        setListeners()

        beaconsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        beaconsRecyclerView.adapter = adapter
        homeViewModel.beacons.observe(viewLifecycleOwner, Observer {
            Log.v(HomeFragment::class.java.simpleName, "IT: ${it.size}")
            adapter.updateList(it.toMutableList())
        })
    }
    private fun setListeners(){

        selfReveal.setOnClickListener {
            viewModel.selfReveal(USER_ID)
            viewModel.updateUser(USER_ID)
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.updateUser(USER_ID)
            swipeRefresh.isRefreshing = false
        }
    }
}

class BeaconsAdapter (private var beacons: List<MyBeacon>) : RecyclerView.Adapter<BeaconsAdapter.BeaconsViewHolder>() {

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
        val beaconId : TextView = itemView.userIdTv
        val beaconDistance: TextView = itemView.userDistanceTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeaconsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.table_row_layout,
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