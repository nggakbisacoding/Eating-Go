package com.example.eatinggo

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.eatinggo.databinding.SearchCafeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

class SearchCafeActivity : AppCompatActivity(), OnMapsSdkInitializedCallback{
    private lateinit var binding: SearchCafeBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
        binding = SearchCafeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(baseContext)
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val currentDate = LocalDateTime.now()
        val date = "${currentDate.dayOfMonth},${currentDate.monthValue},${currentDate.year}"
        val minute = if(currentDate.minute>=10) currentDate.minute else "0${currentDate.minute}"
        val time = "${currentDate.hour}:$minute"
        binding.inputDate.setText(date)
        binding.inputTime.setText(time)

        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.inputDate.setText(sdf.format(cal.time))
        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minutes)
            cal.set(Calendar.AM_PM, 0)

            val myFormat = "hh:mm a"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            binding.inputTime.setText(sdf.format(cal.time))
        }

        binding.inputDate.setOnClickListener {
            DatePickerDialog(this@SearchCafeActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.inputTime.setOnClickListener {
            TimePickerDialog(this@SearchCafeActivity, timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false).show()
        }

        binding.inputLocation.setOnClickListener {
            binding.map.visibility = View.VISIBLE
            fetchLocation()
        }

        binding.searchBtn.setOnClickListener {
            binding.map.visibility = View.GONE
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 42)
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                val myloc = "${it.latitude},${it.longitude}"
                binding.inputLocation.setText(myloc)
            }
        }
    }

    override fun onMapsSdkInitialized(p0: MapsInitializer.Renderer) {
        when (p0) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }
}