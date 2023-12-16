package com.example.eatinggo.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.eatinggo.BuildConfig
import com.example.eatinggo.CafeInformationActivity
import com.example.eatinggo.HistoryPageActivity
import com.example.eatinggo.SearchCafeActivity
import com.example.eatinggo.databinding.FragmentHomeBinding
import com.example.eatinggo.model.Result
import com.example.eatinggo.util.ApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        // Inflate the layout for this fragment
        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(requireContext())
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()
        binding.greetUsername.text = auth.currentUser!!.displayName.toString()

        binding.searchCafe.setOnClickListener {
            startActivity(Intent(requireContext(), SearchCafeActivity::class.java))
        }

        binding.nearbyCafe.setOnClickListener {
            startActivity(Intent(requireContext(), CafeInformationActivity::class.java))
        }

        fetchLocation()

        binding.history.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryPageActivity::class.java))
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
                    binding.namaCafe.text = i.location
                    break
                }
            }
            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.d("API", t.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 42)
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                getAddress(it.latitude, it.longitude)
            }
        }
    }
}

private const val GMAPS_API_KEY = BuildConfig.PLACES_API_KEY