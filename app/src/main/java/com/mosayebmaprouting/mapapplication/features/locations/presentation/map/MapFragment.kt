package com.mosayebmaprouting.mapapplication.features.locations.presentation.map

import android.content.Context
import android.graphics.BitmapFactory
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
import com.carto.styles.AnimationStyle
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.MarkerStyleBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient

import com.google.android.material.snackbar.Snackbar

import com.mosayebmaprouting.mapapplication.core.Permission
import com.mosayebmaprouting.mapapplication.core.PermissionManager

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.neshan.common.model.LatLng
import org.neshan.common.utils.PolylineEncoding
import org.neshan.mapsdk.model.Marker
import org.neshan.mapsdk.model.Polyline
import org.neshan.servicessdk.direction.NeshanDirection
import org.neshan.servicessdk.direction.model.NeshanDirectionResult
import org.neshan.servicessdk.direction.model.Route
import com.carto.styles.*
import com.mosayebmaprouting.mapapplication.R
import com.mosayebmaprouting.mapapplication.databinding.FragmentMapBinding
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.NeshanAddress
import com.mosayebmaprouting.mapapplication.network.ReverseService
import kotlinx.coroutines.flow.collectLatest
import org.neshan.common.network.RetrofitClientInstance

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


@AndroidEntryPoint
class MapFragment : Fragment() {


    private lateinit var binding: FragmentMapBinding
    private lateinit var animSt: AnimationStyle
    private val viewModel by activityViewModels<MapViewModel>()


    // User's current location
    private var userLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationRequest: LocationRequest
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var locationCallback: LocationCallback? = null
    private var lastUpdateTime: String? = null

    private val REQUEST_CODE = 123
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000

    // boolean flag to toggle the ui
    private var mRequestingLocationUpdates: Boolean? = null
    private var latestMarker: Marker? = null
    private var currentmarker: Marker? = null

    private val permissionManager = PermissionManager.from(this)


    private lateinit var currentLocation: LatLng
    private var latestLocation: LatLng? = null


    // we save decoded Response of routing encoded string because we don't want request every time we clicked toggle buttons
    private var routeOverviewPolylinePoints: ArrayList<LatLng>? = null
    private var decodedStepByStepPath: ArrayList<LatLng>? = null

    // value for difference mapSetZoom
    private var overview = false

    // Marker that will be added on map
    private lateinit var marker: Marker

    // List of created markers
    private val markers: ArrayList<Marker> = ArrayList()

    // drawn path of route
    private var onMapPolyline: Polyline? = null


    private var markerAddress = ""

    private val getDataService: ReverseService =
        RetrofitClientInstance.getRetrofitInstance().create(ReverseService::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        // move camera on tehran
        binding.map.moveCamera(LatLng(35.7219, 51.3347), .5f)
        binding.map.setZoom(10.5f, 1f)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        onMapPolyline = null
        latestMarker = null
        currentmarker = null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // when long clicked on map, a marker is added in clicked location
        binding.map.setOnMapLongClickListener {
            val loc = LatLng(it.latitude, it.longitude)
            addUserMarker(loc, false)

            lifecycleScope.launch {
//                getAddressFromLatLng(it.latitude, it.longitude)  // get address from google
                getAddressFromNeshan(loc)      // get address from neshan
            }
        }

        binding.imgCurrentLocation.setOnClickListener {
            if (isLocationEnabled()) {
                getCurrentLocation()
            } else {
                warningSnackbar(getString(R.string.turn_on_gps))
            }


            onMapPolyline?.let {
                binding.map.removePolyline(onMapPolyline)
            }


        }

        binding.imgDirection.setOnClickListener {

            if (latestMarker != null) {
                neshanRoutingApi(currentmarker, latestMarker)
                return@setOnClickListener
            }
            warningSnackbar(getString(R.string.enter_destination))
        }

        binding.imgSave.setOnClickListener {

            latestLocation?.let {
                val location = LocationModel(
                    latestLocation!!.latitude,
                    latestLocation!!.longitude,
                    markerAddress
                )
                addAddressToDB(location)
                return@setOnClickListener
            }
            warningSnackbar(getString(R.string.enter_destination))
        }

        binding.imgList.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mapFragment_to_locationLostFragment)
        }
    }


    private fun addUserMarker(loc: LatLng, isUserCurrentLocation: Boolean) {

        // AnimationStyle
        val animStBl = AnimationStyleBuilder()
        animStBl.fadeAnimationType = AnimationType.ANIMATION_TYPE_SMOOTHSTEP
        animStBl.sizeAnimationType = AnimationType.ANIMATION_TYPE_SPRING
        animStBl.phaseInDuration = 0.5f
        animStBl.phaseOutDuration = 0.5f
        animSt = animStBl.buildStyle()

        val markStCr = MarkerStyleBuilder()
        markStCr.size = 30f
        markStCr.bitmap = com.carto.utils.BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ic_marker
            )
        )

        markStCr.animationStyle = animSt
        val markSt = markStCr.buildStyle()

        if (isUserCurrentLocation) {
            currentmarker?.let {
                binding.map.removeMarker(currentmarker)
            }
            currentmarker = Marker(loc, markSt)
            binding.map.addMarker(currentmarker)

            latestMarker?.let {
                showDirectionButton()
            }

        } else {
            //remove latestMarker marker from map
            latestMarker?.let {
                binding.map.removeMarker(latestMarker)
            }
            latestMarker = Marker(loc, markSt)
            binding.map.addMarker(latestMarker)
            latestLocation = loc

            currentmarker?.let {
                showDirectionButton()
            }
        }
    }

    private fun showDirectionButton() {
        // to show direction button when both currentMarker and destinationMarker added on map
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                binding.imgDirection.visibility = View.VISIBLE
            }
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

    private fun neshanRoutingApi(currentmarker: Marker?, latestMarker: Marker?) {
        NeshanDirection.Builder(
            "service.1086dbd8409845568a76f21114606dae",
            currentmarker!!.latLng,
            latestMarker!!.latLng
        )
            .build().call(object : Callback<NeshanDirectionResult?> {
                override fun onResponse(
                    call: Call<NeshanDirectionResult?>,
                    response: Response<NeshanDirectionResult?>
                ) {

                    // two type of routing
                    if (response.body() != null && response.body()!!.routes != null &&
                        !response.body()!!.routes.isEmpty()
                    ) {

                        // to clear the former route

                        onMapPolyline?.let {
                            binding.map.removePolyline(onMapPolyline)
                        }

                        val route: Route = response.body()!!.routes[0]
                        routeOverviewPolylinePoints = java.util.ArrayList(
                            PolylineEncoding.decode(
                                route.overviewPolyline.encodedPolyline
                            )
                        )
                        decodedStepByStepPath = java.util.ArrayList()

                        // decoding each segment of steps and putting to an array
                        for (step in route.legs[0].directionSteps) {
                            decodedStepByStepPath!!.addAll(PolylineEncoding.decode(step.encodedPolyline))
                        }
                        onMapPolyline = Polyline(routeOverviewPolylinePoints, getLineStyle())
                        //draw polyline between route points
                        binding.map.addPolyline(onMapPolyline)
                        // focusing camera on first point of drawn line
                        mapSetPosition(overview)
                    } else {
                        Toast.makeText(context, "مسیری یافت نشد", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(call: Call<NeshanDirectionResult?>, t: Throwable) {
                    var a = 5
                }
            })
    }

    // In this method we create a LineStyleCreator, set its features and call buildStyle() method
    // on it and return the LineStyle object (the same routine as crating a marker style)
    private fun getLineStyle(): LineStyle {
        val lineStCr = LineStyleBuilder()
        lineStCr.color = com.carto.graphics.Color(
            2.toShort(), 119.toShort(), 189.toShort(),
            190.toShort()
        )
        lineStCr.width = 10f
        lineStCr.stretchFactor = 0f
        return lineStCr.buildStyle()
    }

    // for overview routing we zoom out and review hole route and for stepByStep routing we just zoom to first marker position
    private fun mapSetPosition(overview: Boolean) {

//        markers[0] = currentmarker;
//        markers[1] = latestMarker;

        val centerFirstMarkerX = currentmarker!!.latLng.latitude
        val centerFirstMarkerY = currentmarker!!.latLng.longitude
        if (overview) {
            val centerFocalPositionX = (centerFirstMarkerX + latestMarker!!.latLng.latitude) / 2
            val centerFocalPositionY = (centerFirstMarkerY + latestMarker!!.latLng.longitude) / 2
            binding.map.moveCamera(LatLng(centerFocalPositionX, centerFocalPositionY), 0.5f)
            binding.map.setZoom(14f, 0.5f)
        } else {
            binding.map.moveCamera(LatLng(centerFirstMarkerX, centerFirstMarkerY), 0.5f)
            binding.map.setZoom(14f, 0.5f)
        }
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


    private fun getAddressFromNeshan(currentLocation: LatLng) {
        getDataService.getReverse(currentLocation.latitude, currentLocation.longitude)
            .enqueue(object : Callback<NeshanAddress> {
                override fun onResponse(
                    call: Call<NeshanAddress>,
                    response: Response<NeshanAddress>
                ) {
                    val address: String? = response.body()!!.address
                    if (address != null && !address.isEmpty()) {
                        markerAddress = address
                    } else {
                        markerAddress = "معبر بی‌نام"
                    }
                }

                override fun onFailure(call: Call<NeshanAddress>, t: Throwable) {
                    markerAddress = "معبر بی‌نام"
                }
            })
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

                                currentLocation = LatLng(latitude, longitude)

                                addUserMarker(currentLocation, true)
                                binding.map.moveCamera(currentLocation, .5f)
                                binding.map.setZoom(15f, 1f)
                                // Use the latitude and longitude values as needed
                            }
                        }

                } else {
                    warningSnackbar("We couldn't access the location permission!!! try again.")
//                    binding.btn.visibility = View.VISIBLE
                }
            }
    }

}