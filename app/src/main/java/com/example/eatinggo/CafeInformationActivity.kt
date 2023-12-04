package com.example.eatinggo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatinggo.data.ControllerDatabase
import com.example.eatinggo.databinding.CafeInformationBinding
import com.example.eatinggo.model.CafeDisplay
import com.example.eatinggo.model.Result
import com.example.eatinggo.util.ApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CafeInformationActivity : AppCompatActivity() {
    private lateinit var database: ControllerDatabase
    private lateinit var binding: CafeInformationBinding
    private lateinit var firedb: DatabaseReference
    private val PERMISSION_ID = 42
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var data = ArrayList<CafeDisplay>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CafeInformationBinding.inflate(layoutInflater)
        firedb = Firebase.database.reference
        setContentView(binding.root)
        database = ControllerDatabase.getDatabase(baseContext)
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = CafeListAdapter(data)
        recyclerview.adapter = adapter

        getLastLocation()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        println(location.latitude + location.longitude)
                        getAddress(location.latitude, location.longitude)
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

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            mLastLocation.distanceTo(mLastLocation)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_ID
        )
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getAddress(latitude: Double, longitude: Double) {
        //val baseurl = "https://plus.codes/api?address=$latitude,$longitude&ekey=$KEY&email=$EMAIL"
        //val reserveGeoCoding = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=$KEY"
        val client = ApiClient.getInstance()
        val address = ArrayList<String>()
        val lat = "$latitude,$longitude"
        val response = client.getLocation(lat, GMAPS_API_KEY)
        response.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                for (i in response.body()?.data!!) {
                    address.add(i.location)
                    binding.myLocation.text = i.location
                    break
                }
            }
            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.d("API", t.toString())
            }
        })
        println(address)
    }
}

private const val GMAPS_API_KEY = "AIzaSyBg6QdmQM8zWIdmXk7ova_5Xj8B3KO4xcY"