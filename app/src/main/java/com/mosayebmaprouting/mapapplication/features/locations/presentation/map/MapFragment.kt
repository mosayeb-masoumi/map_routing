package com.mosayebmaprouting.mapapplication.features.locations.presentation.map

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation

import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.material.snackbar.Snackbar

import com.mosayebmaprouting.mapapplication.core.Permission
import com.mosayebmaprouting.mapapplication.core.PermissionManager

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.mosayebmaprouting.mapapplication.R
import com.mosayebmaprouting.mapapplication.databinding.FragmentMapBinding
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import kotlinx.coroutines.flow.collectLatest

import java.io.IOException


@AndroidEntryPoint
class MapFragment : Fragment() , OnMapReadyCallback {


    private lateinit var binding: FragmentMapBinding
    private val viewModel by activityViewModels<MapViewModel>()

    private lateinit var googleMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val REQUEST_CODE = 123
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000


    private val permissionManager = PermissionManager.from(this)




    private var markerAddress = ""


    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        binding.mapView.onCreate(savedInstanceState)
//        binding.mapView.onResume()
//        binding.mapView.getMapAsync(this)
    }






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()
        binding.mapView.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgCurrentLocation.setOnClickListener {
            if (isLocationEnabled()) {
                getCurrentLocation()
            } else {
                warningSnackbar(getString(R.string.turn_on_gps))
            }
        }



        binding.imgSave.setOnClickListener {

//            latestLocation?.let {
//                val location = LocationModel(
//                    latestLocation!!.latitude,
//                    latestLocation!!.longitude,
//                    "Address"
//                )
//                addAddressToDB(location)
//                return@setOnClickListener
//            }
            warningSnackbar(getString(R.string.enter_destination))
        }

        binding.imgList.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mapFragment_to_locationLostFragment)
        }
    }


    private fun warningSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .withColor(Color.parseColor("#B00020"))
            .show()
    }

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    suspend fun getAddressFromLatLng(latitude: Double, longitude: Double) {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(requireContext())
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {

                    viewModel.createAdress(addresses[0])
                    viewModel.getAddress.collectLatest {
                        markerAddress = it
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                markerAddress = ""
            }
        }
    }

    private fun addAddressToDB(location: LocationModel) {
        viewModel.saveLocation(location)
        // wait for observing
        val saveItem = viewModel.saveLocationResponse
        lifecycleScope.launch {
            saveItem.collectLatest {
                when {
                    it.isLoading -> {
                        Log.i("loading", "loading")
                    }

                    it.error != "" -> {
                        Log.i("error", "error")
                    }

                    it.data != null -> {
                        Toast.makeText(context, "address saved successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    }


    private fun getCurrentLocation() {

        permissionManager
            // Check one permission at a time
            .request(Permission.Location)
            .rationale("We need permission to see the map")
            .checkPermission { granted ->
                if (granted) {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            location?.let {
                                val latitude = location.latitude
                                val longitude = location.longitude

                                var a = 5;
//
//                                currentLocation = LatLng(latitude, longitude)
//
//                                addUserMarker(currentLocation, true)
//                                binding.map.moveCamera(currentLocation, .5f)
//                                binding.map.setZoom(15f, 1f)
                                // Use the latitude and longitude values as needed
                            }
                        }

                } else {
                    warningSnackbar("We couldn't access the location permission!!! try again.")
//                    binding.btn.visibility = View.VISIBLE
                }
            }
    }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map

        // Set up initial map settings and markers
        val initialLatLng = LatLng(35.7219, 51.3347) // Example coordinates (San Francisco)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 12f))


        googleMap.setOnMapLongClickListener {
            addMarker(it)

            // to get the location address
            lifecycleScope.launch {
                getAddressFromLatLng(it.latitude , it.longitude)
            }

        }
//        addMarker(initialLatLng)
    }


    private fun addMarker(initialLatLng: LatLng) {
        val markerOptions = MarkerOptions()
            .position(initialLatLng)
//            .title("Marker Title")
//            .snippet("Marker Snippet")
        googleMap.addMarker(markerOptions)
    }



}