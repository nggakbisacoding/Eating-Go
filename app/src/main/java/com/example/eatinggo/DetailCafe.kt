package com.example.eatinggo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eatinggo.databinding.CafeSeatBinding
import com.example.eatinggo.model.DetailsCafe
import com.example.eatinggo.util.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailCafe : AppCompatActivity() {
    private lateinit var binding: CafeSeatBinding
    private var placeId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CafeSeatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val data = intent
        val name = data.getStringExtra("name")
        val used = data.getIntExtra("used",0)
        val avaible = data.getIntExtra("avaible", 0)
        val total = data.getIntExtra("total", 0)
        placeId = data.getStringExtra("place")

        binding.cafeName.text = name
        binding.used.text = used.toString()
        binding.seat.text = avaible.toString()
        binding.totalSeat.text = total.toString()
    }

    override fun onStart() {
        super.onStart()
        getAllData(placeId!!)
    }

    private fun getAllData(placeId: String){
        Api.placeService.placeDetails("current_opening_hours,formatted_phone_number,user_ratings_total,wheelchair_accessible_entrance", placeId, BuildConfig.PLACES_API_KEY).enqueue(object: Callback<DetailsCafe> {
            override fun onResponse(call: Call<DetailsCafe>, response: Response<DetailsCafe>) {
                if(response.isSuccessful){
                    val data = response.body()!!.data
                    binding.phone.text = data.phone
                    binding.rating.text = data.ratings.toString()
                    binding.openNow.text = if(data.time.openNow) "Yes" else "No"
                    binding.times.text = data.time.weekday.toString()
                }
            }
            override fun onFailure(call: Call<DetailsCafe>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}