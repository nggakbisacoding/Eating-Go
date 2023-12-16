package com.example.eatinggo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eatinggo.databinding.FaqPageBinding

class FaqPageActivity : AppCompatActivity() {
    private lateinit var binding: FaqPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FaqPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}