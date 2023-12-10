package com.example.eatinggo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eatinggo.ReservationDetailPageActivity
import com.example.eatinggo.databinding.HomepageEmployeeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class HomepageEmployeeFragment : Fragment() {
    private lateinit var binding: HomepageEmployeeBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        binding = HomepageEmployeeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.greetUsername.text = auth.currentUser!!.displayName.toString()

        binding.information.setOnClickListener {
            Toast.makeText(requireContext(), "Unfortunately, for now, cafe information is managed by developers", Toast.LENGTH_SHORT).show()
        }

        binding.seat.setOnClickListener {
            startActivity(Intent(requireContext(), ReservationDetailPageActivity::class.java))
        }
    }
}