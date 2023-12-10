package com.example.eatinggo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eatinggo.databinding.LoginPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth


class LoginPageActivity : AppCompatActivity() {
    private lateinit var binding: LoginPageBinding
    private lateinit var switchpage: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvPassword: TextView
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
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        tvUsername = findViewById(R.id.login_username)
        tvPassword = findViewById(R.id.login_password)
        switchpage = findViewById(R.id.switch_regis)

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        email = sharedpreferences.getString(EMAIL_KEY, null)
        password = sharedpreferences.getString(PASSWORD_KEY, null)

        switchpage.setOnClickListener {
            this.startActivity(Intent(this@LoginPageActivity, RegisterActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        if (email != null && password != null) {
            signIn(email!!, password!!)
            val i = Intent(baseContext, MainActivity2::class.java)
            startActivity(i)
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    private fun signIn() {
        val username = tvUsername.text.toString()
        val password = tvPassword.text.toString()
        if(username.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Username or Password can't be blank!", Toast.LENGTH_LONG).show()
            return
        }
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "signInWithEmail:failed", task.exception)
                } else {
                    checkIfEmailVerified()
                }
            }
    }

    private fun checkIfEmailVerified() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user!!.isEmailVerified) {
            updateUI(user)
            Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()
        } else {
            FirebaseAuth.getInstance().signOut()
        }
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            return
        }
        val editor = sharedpreferences.edit()
        editor.putString(EMAIL_KEY, tvUsername.text.toString())
        editor.putString(PASSWORD_KEY, tvPassword.text.toString())
        editor.apply()
        this.startActivity(Intent(baseContext, MainActivity2::class.java))
        finish()
    }
}