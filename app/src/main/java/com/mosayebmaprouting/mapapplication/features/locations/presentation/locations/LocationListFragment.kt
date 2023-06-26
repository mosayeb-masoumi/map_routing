package com.mosayebmaprouting.mapapplication.features.locations.presentation.locations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mosayebmaprouting.mapapplication.R
import com.mosayebmaprouting.mapapplication.databinding.FragmentLocationListBinding
import com.mosayebmaprouting.mapapplication.features.locations.adapter.LocationAdapter
import com.mosayebmaprouting.mapapplication.features.locations.adapter.LocationItemInteraction
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LocationListFragment : Fragment(), LocationItemInteraction {

    private lateinit var binding: FragmentLocationListBinding
    private val viewModel by activityViewModels<LocationsViewModel>()

    @Inject
    lateinit var adapter: LocationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getLocationsFromDb()
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun getLocationsFromDb() {

        viewModel.getLocationsFromDb()
        val getNotesLiveData = viewModel.getLocationResponse
        lifecycleScope.launch {
            getNotesLiveData.collectLatest {
                when {
                    it.isLoading -> {}
                    it.data != null -> {
                        if (it.data.isEmpty()) {
                            binding.txtNoResult.visibility = View.VISIBLE
                        } else {
                            binding.txtNoResult.visibility = View.GONE
                            setRecyClerView(it.data)
                        }
                    }
                    it.error != "" -> {}
                }
            }
        }
    }

    private fun setRecyClerView(data: List<LocationModel?>) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerview.layoutManager = layoutManager
        adapter.setListener(this)
        adapter.submitList(data)
        binding.recyclerview.adapter = adapter
    }

    override fun markerItemOnclick(location: LocationModel) {

        val bundle = Bundle()
        bundle.putDouble("lat", location.lat)
        bundle.putDouble("lng", location.lng)
        view?.let {
            Navigation.findNavController(it)
                .navigate(R.id.action_locationLostFragment_to_mapFragment, bundle)
        }
    }


}