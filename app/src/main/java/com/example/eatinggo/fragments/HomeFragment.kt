package com.example.eatinggo.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eatinggo.CafeInformationActivity
import com.example.eatinggo.HistoryPageActivity
import com.example.eatinggo.SearchCafeActivity
import com.example.eatinggo.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.greetUsername.text = auth.currentUser!!.displayName.toString()

        binding.searchCafe.setOnClickListener {
            startActivity(Intent(requireContext(), SearchCafeActivity::class.java))
        }

        binding.nearbyCafe.setOnClickListener {
            startActivity(Intent(requireContext(), CafeInformationActivity::class.java))
        }

        binding.history.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryPageActivity::class.java))
        }
    }
}