package com.example.eatinggo

// import com.example.eatinggo.fragments.ActiveBookFragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.eatinggo.databinding.ActivityMainBinding
import com.example.eatinggo.fragments.HomeFragment
import com.example.eatinggo.fragments.HomepageEmployeeFragment
import com.example.eatinggo.fragments.ProfileFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    companion object {
        const val SHARED_PREFS = "shared_prefs"
        const val EMAIL_KEY = "email_key"
        const val PASSWORD_KEY = "password_key"
        const val ROLES = "roles"
    }
    private lateinit var sharedpreferences: SharedPreferences
    private var email: String? = null
    private var password: String? = null
    private var typeUser: String? = null

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
        database = Firebase.database.reference
        auth = Firebase.auth
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        email = sharedpreferences.getString(EMAIL_KEY, null)
        password = sharedpreferences.getString(PASSWORD_KEY, null)
        typeUser = sharedpreferences.getString(ROLES, null)
        if(typeUser == null) {
            database.child("users").child(auth.currentUser?.uid.toString()).child("userCategory").get().addOnCompleteListener {
                if(it.isSuccessful) {
                    val roles = it.result.value.toString()
                    val editor = sharedpreferences.edit()
                    editor.putString(ROLES, roles)
                    editor.apply()
                    typeUser = roles
                }
            }
        }
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        // atur tampilan awal dari fragement
        binding.bottomNavigationView.setOnItemSelectedListener{ item ->
            when(item.itemId){
                R.id.home -> if(typeUser == "user") switchFragment(HomeFragment()) else switchFragment(HomepageEmployeeFragment())
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
        if(email != auth.currentUser?.email.toString()) {
            val editor = sharedpreferences.edit()
            editor.clear()
            editor.apply()
        }
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            startActivity(Intent(this, SplashPageActivity::class.java))
            finish()
        } else {
            if(typeUser == "user") switchFragment(HomeFragment()) else switchFragment(HomepageEmployeeFragment())
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                finish()
                return true
            }
            R.id.active_book -> {
                finish()
                return true
            }
            R.id.profile -> {
                finish()
                return true
            }
            R.id.back_btn -> {
                finish()
                return true
            }
            R.drawable.ic_back_arrow -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
}