package com.example.eatinggo.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eatinggo.AboutPageActivity
import com.example.eatinggo.AccountDetailPageActivity
import com.example.eatinggo.FaqPageActivity
import com.example.eatinggo.LoginPageActivity
import com.example.eatinggo.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.util.concurrent.Executors

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firedb: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        firedb = Firebase.database.reference
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        binding.tvusername.text = auth.currentUser!!.displayName.toString()

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap?
        var url: String
        firedb.child("users").child(auth.currentUser!!.uid).child("photoUri").get().addOnSuccessListener {
                url = it.value.toString()
            println(it.key + " " + it.value)
            executor.execute {
                try {
                    val `in` = java.net.URL(url).openStream()
                    image = BitmapFactory.decodeStream(`in`)
                    handler.post {
                        binding.profileImage.setImageBitmap(image)
                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

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