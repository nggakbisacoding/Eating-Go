package com.example.eatinggo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eatinggo.data.ControllerDatabase
import com.example.eatinggo.databinding.AboutPageBinding

class AboutPageActivity : AppCompatActivity() {
    private lateinit var database: ControllerDatabase
    private lateinit var binding: AboutPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AboutPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = ControllerDatabase.getDatabase(baseContext)
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}