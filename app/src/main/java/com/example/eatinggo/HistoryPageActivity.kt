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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatinggo.databinding.HistoryPageBinding
import com.example.eatinggo.model.CafeDisplay
import com.example.eatinggo.model.CafeResult
import com.example.eatinggo.util.Api
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.maps.android.SphericalUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HistoryPageActivity : AppCompatActivity() {
    private lateinit var binding: HistoryPageBinding
    private val permissionid = 42
    private lateinit var firedb: DatabaseReference
    private var latlng = ""
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HistoryPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firedb = Firebase.database.reference
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        recyclerView = findViewById(R.id.rv_histori)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        manager = LinearLayoutManager(this)

        getLastLocation()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        latlng = "${location.latitude},${location.longitude}"
                        //checkDistance(location)
                        val myloc = LatLng(location.latitude, location.longitude)
                        getDistance(myloc, latlng)
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

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            permissionid
        )
    }
    /*
    private fun checkDistance(location: Location) {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val loc = snapshot.child("location").value.toString().split(",")
                val targetLocation = LatLng(loc[0].toDouble(), loc[1].toDouble())
                val currentLoc = LatLng(location.latitude, location.longitude)
                val distance = SphericalUtil.computeDistanceBetween(currentLoc, targetLocation)
                if(distance / 1000 <= 50) {
                    Toast.makeText(applicationContext, "Distance between your location and ${snapshot.child("name").value.toString()} is \n " + String.format("%.2f", distance / 1000) + "km", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        firedb.child("cafe").addChildEventListener(childEventListener)
    }
     */
    private fun getDistance(location: LatLng, latlng: String){
        val dataInRange = ArrayList<CafeDisplay>()
        Api.retrofitService.getAllData(latlng, BuildConfig.PLACES_API_KEY).enqueue(object:
            Callback<CafeResult> {
            override fun onResponse(call: Call<CafeResult>, response: Response<CafeResult>) {
                if(response.isSuccessful){
                    for(i in response.body()!!.data!!) {
                        val target = LatLng(i.geo.latlng.lat, i.geo.latlng.lng)
                        val distance = SphericalUtil.computeDistanceBetween(location, target)
                        if(distance <= 100) {
                            dataInRange.add(i)
                            Toast.makeText(applicationContext, "Distance between your location and ${i.name.toString()} is \n " + String.format("%.2f", distance / 1000) + "km", Toast.LENGTH_SHORT).show()
                        }
                    }
                    recyclerView.apply{
                        myAdapter = HistoryListAdapter(dataInRange)
                        layoutManager = manager
                        adapter = myAdapter
                    }
                }
            }
            override fun onFailure(call: Call<CafeResult>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}