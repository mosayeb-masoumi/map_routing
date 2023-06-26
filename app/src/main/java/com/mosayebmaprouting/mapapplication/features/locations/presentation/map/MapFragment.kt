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
import com.google.android.gms.location.LocationServices
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


private const val ARG_PARAM1 = "lat"
private const val ARG_PARAM2 = "lng"

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {


    private lateinit var binding: FragmentMapBinding
    private val viewModel by activityViewModels<MapViewModel>()

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val permissionManager = PermissionManager.from(this)
    private var latestAddress = ""

    private lateinit var latestLocation: LatLng

    private var latArgument: Double? = null
    private var lngArgument: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latArgument = it.getDouble(ARG_PARAM1)
            lngArgument = it.getDouble(ARG_PARAM2)
        }
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.imgCurrentLocation.setOnClickListener {
            if (::googleMap.isInitialized) {
                if (isLocationEnabled()) {
                    getCurrentLocation()
                } else {
                    warningSnackbar(getString(R.string.turn_on_gps))
                }
            }
        }


        binding.btnSave.setOnClickListener {

            if (::latestLocation.isInitialized) {
                val location = LocationModel(
                    latestLocation.latitude,
                    latestLocation.longitude,
                    latestAddress
                )
                addAddressToDB(location)
            } else {
                warningSnackbar("Please long click on map!")
            }

        }


        binding.btnLocationList.setOnClickListener {
            Navigation.findNavController(view)
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
                        latestAddress = it
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                latestAddress = ""
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


                                latestLocation = LatLng(latitude, longitude)
                                addMarker(latestLocation)
                                moveCameraToLocation(latestLocation, 16f)

                                lifecycleScope.launch {
                                    getAddressFromLatLng(latitude, longitude)
                                }
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

        if (latArgument != null && lngArgument != null) {
            latestLocation = LatLng(
                latArgument!!,
                lngArgument!!
            ) // set Marker on map when location list item clicked
            moveCameraToLocation(latestLocation, 18f)
            addMarker(latestLocation)
        } else {
            // Set up initial map settings and markers
            val initLocation = LatLng(35.7219, 51.3347) // Initial location (Tehran)
            moveCameraToLocation(initLocation, 11f)
        }


        googleMap.setOnMapLongClickListener {
            addMarker(it)
            // to get the location address
            latestAddress = ""
            latestLocation = it
            lifecycleScope.launch {
                getAddressFromLatLng(it.latitude, it.longitude)
            }
        }

    }


    private fun addMarker(initialLatLng: LatLng) {
        val markerOptions = MarkerOptions()
            .position(initialLatLng)
        googleMap.addMarker(markerOptions)
    }


    private fun moveCameraToLocation(latLng: LatLng, zoom: Float) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
        googleMap.animateCamera(cameraUpdate)
    }





}