package com.example.eatinggo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eatinggo.databinding.SplashPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SplashPageActivity : AppCompatActivity() {
    private lateinit var binding: SplashPageBinding
    private lateinit var auth: FirebaseAuth
    companion object {
        const val SHARED_PREFS = "shared_prefs"
        const val EMAIL_KEY = "email_key"
        const val PASSWORD_KEY = "password_key"
    }
    private lateinit var sharedpreferences: SharedPreferences
    private var email: String? = null
    private var password: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        email = sharedpreferences.getString(EMAIL_KEY, null)
        password = sharedpreferences.getString(PASSWORD_KEY, null)
        auth = Firebase.auth
        binding.loginBtn.setOnClickListener {
            if(isOnline(baseContext)) {
                startActivity(Intent(baseContext, LoginPageActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please enable your connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerBtn.setOnClickListener {
            if(isOnline(baseContext)) {
                startActivity(Intent(baseContext, RegisterActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please enable your connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(!isOnline(baseContext)) {
            Toast.makeText(baseContext, "Current APK Version required Internet Connection!", Toast.LENGTH_LONG).show()
            return
        }
        if(auth.currentUser == null) {
            val editor = sharedpreferences.edit()
            editor.clear()
            editor.apply()
            return
        }
        if(auth.currentUser != null && email == auth.currentUser?.email.toString()) {
            startActivity(Intent(this, MainActivity2::class.java))
            finish()
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

}