package com.example.eatinggo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
// import com.example.eatinggo.fragments.ActiveBookFragment
import com.example.eatinggo.fragments.HomeFragment
import com.example.eatinggo.fragments.ProfileFragment
import com.example.eatinggo.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity2 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityMainBinding

    //fungsi untuk switch fragment
    private fun switchFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    // inisiasi switch fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        // atur tampilan awal dari fragement
        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home -> switchFragment(HomeFragment())
                R.id.active_book -> Toast.makeText(baseContext, "This Feature Disable by Now", Toast.LENGTH_LONG).show()
                R.id.profile -> switchFragment(ProfileFragment())
                else ->{
                }
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        } else {
            switchFragment(HomeFragment())
        }
    }
}