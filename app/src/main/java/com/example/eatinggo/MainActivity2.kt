package com.example.eatinggo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.eatinggo.Fragments.ActiveBookFragment
import com.example.eatinggo.Fragments.HomeFragment
import com.example.eatinggo.Fragments.ProfileFragment
import com.example.eatinggo.databinding.ActivityMainBinding

class MainActivity2 : AppCompatActivity() {
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
        // atur tampilan awal dari fragement
        switchFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home -> switchFragment(HomeFragment())
                R.id.active_book -> switchFragment(ActiveBookFragment())
                R.id.profile -> switchFragment(ProfileFragment())
                else ->{

                }
            }
            true
        }
    }
}