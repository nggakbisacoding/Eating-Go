package com.example.eatinggo

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatinggo.databinding.SearchCafeBinding
import com.example.eatinggo.model.CafeResult
import com.example.eatinggo.util.Api
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

class SearchCafeActivity : AppCompatActivity(), OnMapsSdkInitializedCallback{
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>
    private lateinit var binding: SearchCafeBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var fireDB: DatabaseReference
    private var latlng: String? = null
    //private lateinit var responseView: TextView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = SearchCafeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Places.initialize(applicationContext, BuildConfig.PLACES_API_KEY)
        recyclerView = findViewById(R.id.rv_cafe_item)
        manager = LinearLayoutManager(this@SearchCafeActivity)
        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(baseContext)
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        fireDB = Firebase.database.reference
        placesClient = Places.createClient(this)
        //responseView = findViewById(R.id.autocomplete_response_content)
        //val fields = listOf(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        setupAutocompleteSupportFragment()
        val currentDate = LocalDateTime.now()
        val date = "${currentDate.monthValue}/${currentDate.dayOfMonth}/${currentDate.year}"
        val minute = if(currentDate.minute>=10) currentDate.minute else "0${currentDate.minute}"
        val time = "${currentDate.hour}:$minute"
        binding.inputDate.setText(date)
        binding.inputTime.setText(time)

        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.inputDate.setText(sdf.format(cal.time))
        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minutes)

            val myFormat = "HH:mm"
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
                true).show()
        }

        binding.inputLocation.setOnClickListener {
            startAutocompleteActivity()
            binding.inputLocation.isClickable = false
            /**
             * [fetchLocation]
             */
        }

        binding.searchBtn.setOnClickListener {
            if(binding.inputCafe.text != null || binding.inputLocation.text != null) {
                binding.mainContent.visibility = View.GONE
                binding.searchContent.visibility = View.VISIBLE
                queryCafe(binding.inputCafe.text.toString(), latlng!!)
            }
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
    }override fun onMapsSdkInitialized(p0: MapsInitializer.Renderer) {
        when (p0) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }

    private fun setupAutocompleteSupportFragment() {
        val autocompleteSupportFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_support_fragments) as AutocompleteSupportFragment?
        autocompleteSupportFragment?.setOnPlaceSelectedListener(placeSelectionListener)
        autocompleteSupportFragment?.setCountries(listOf("ID"))
        autocompleteSupportFragment?.setActivityMode(AutocompleteActivityMode.OVERLAY)
    }

    private val placeSelectionListener: PlaceSelectionListener
        get() = object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                binding.inputLocation.setText(place.address)
            }

            override fun onError(status: Status) {
                binding.inputLocation.setText(status.statusMessage)
            }
        }

    private val fieldSelector = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.CURRENT_OPENING_HOURS, Place.Field.LAT_LNG, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS)
    private fun startAutocompleteActivity() {
        val autocompleteIntent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldSelector)
            .setCountries(listOf("ID"))
            .build(this)
        autocompleteLauncher.launch(autocompleteIntent)
    }

    private var autocompleteLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        when (result.resultCode) {
            AutocompleteActivity.RESULT_OK -> {
                val data: Intent? = result.data
                if (data != null) {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    binding.inputLocation.setText(place.address)
                    latlng = place.latLng?.latitude.toString()+","+ place.latLng?.longitude.toString()
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(intent)
                binding.inputLocation.setText(status.statusMessage)
            }
            AutocompleteActivity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }
    }

    private fun queryCafe(name: String, lokasi: String) {
        Api.retrofitService.getnearby(name, latlng!!, BuildConfig.PLACES_API_KEY).enqueue(object:
            Callback<CafeResult> {
            override fun onResponse(call: Call<CafeResult>, response: Response<CafeResult>) {
                if(response.isSuccessful){
                    if(response.body()!!.data != null) {
                        recyclerView.apply{
                            myAdapter = CafeListAdapter(response.body()!!.data!!)
                            layoutManager = manager
                            adapter = myAdapter
                        }
                    } else {
                        Toast.makeText(applicationContext, "Query tidak menemukan cafe disekitar $lokasi", Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(call: Call<CafeResult>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}