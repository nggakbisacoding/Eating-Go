package com.example.eatinggo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.eatinggo.databinding.SearchCafeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.MapsInitializer.Renderer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places


class CafeMap : FragmentActivity(), OnMapReadyCallback, OnMapsSdkInitializedCallback {
    private lateinit var binding: SearchCafeBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var currentLocations: LatLng = LatLng(20.5, 78.9)
    private val pERMISSION_ID = 42
    private val permissionCode = 101
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext, Renderer.LEGACY, this)
        binding = SearchCafeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiKey = "AIzaSyBg6QdmQM8zWIdmXk7ova_5Xj8B3KO4xcY"
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this@CafeMap)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.myLoc.setOnClickListener {
            fetchLocation()
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                currentLocation = it
                Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" +
                        currentLocation.longitude, Toast.LENGTH_SHORT).show()
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.myMap) as
                        SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@CafeMap)
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocations, 3f))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
        googleMap.addMarker(markerOptions)
        googleMap.setOnMarkerClickListener { marker ->
            val markers = MarkerOptions().position(marker.position).title("Clicked!")
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 5f))
            googleMap.addMarker(markers)
            false
        }
        mMap = googleMap

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocations = LatLng(location.latitude, location.longitude)
                        mMap.clear()
                        mMap.addMarker(MarkerOptions().position(currentLocations))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocations, 16F))
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setMaxUpdates(1)
            .setIntervalMillis(0)
            .build()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    // If current location could not be located, use last location
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            currentLocations = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    // function to check if GPS is on
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Check if location permissions are
    // granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Request permissions if not granted before
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            pERMISSION_ID
        )
    }

    // What must happen when permission is granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == pERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    override fun onMapsSdkInitialized(p0: Renderer) {
        when (p0) {
            Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }
}