package com.example.eatinggo.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eatinggo.AboutPageActivity
import com.example.eatinggo.AccountDetailPageActivity
import com.example.eatinggo.FaqPageActivity
import com.example.eatinggo.LoginPageActivity
import com.example.eatinggo.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.tvusername.text = auth.currentUser!!.displayName.toString()

        binding.accountDetail.setOnClickListener {
            startActivity(Intent(requireContext(), AccountDetailPageActivity::class.java))
        }

        binding.about.setOnClickListener {
            startActivity(Intent(requireContext(), AboutPageActivity::class.java))
        }

        binding.faqBtn.setOnClickListener {
            startActivity(Intent(requireContext(), FaqPageActivity::class.java))
        }

        binding.logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(), LoginPageActivity::class.java))
        }
    }
}