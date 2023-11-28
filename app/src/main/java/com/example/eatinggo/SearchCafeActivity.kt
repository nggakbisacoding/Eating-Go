package com.example.eatinggo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eatinggo.databinding.SearchCafeBinding

class SearchCafeActivity : AppCompatActivity() {
    private lateinit var binding: SearchCafeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchCafeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}