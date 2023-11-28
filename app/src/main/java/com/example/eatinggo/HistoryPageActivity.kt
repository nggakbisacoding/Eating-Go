package com.example.eatinggo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eatinggo.data.ControllerDatabase
import com.example.eatinggo.databinding.HistoryPageBinding

class HistoryPageActivity : AppCompatActivity() {
    private lateinit var database: ControllerDatabase
    private lateinit var binding: HistoryPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HistoryPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = ControllerDatabase.getDatabase(baseContext)
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}