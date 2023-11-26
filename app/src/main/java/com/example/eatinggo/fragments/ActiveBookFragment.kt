package com.example.eatinggo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eatinggo.databinding.FragmentActiveBookBinding

class ActiveBookFragment : Fragment() {
    private lateinit var binding : FragmentActiveBookBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentActiveBookBinding.inflate(inflater, container, false)
        return binding.root
    }
}