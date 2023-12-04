package com.example.eatinggo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.eatinggo.databinding.LoginPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginPageActivity : AppCompatActivity() {
    private lateinit var binding: LoginPageBinding
    private lateinit var switchpage: TextView
    private lateinit var rolesLogin: TextView
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

        var roles = "user"
        auth = Firebase.auth

        tvUsername = findViewById(R.id.login_username)
        tvPassword = findViewById(R.id.login_password)
        switchpage = findViewById(R.id.switch_regis)
        rolesLogin = findViewById(R.id.employee_login)

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        email = sharedpreferences.getString(EMAIL_KEY, null)
        password = sharedpreferences.getString(PASSWORD_KEY, null)

        rolesLogin.setOnClickListener {
            when (roles) {
                "user" -> {
                    rolesLogin.text = getString(R.string.login_as_user)
                    findViewById<TextView>(R.id.roles).text = getString(R.string.i_am_a_employee)
                    roles = "employee"
                }
                "employee" -> {
                    rolesLogin.text = getString(R.string.login_as_employee)
                    findViewById<TextView>(R.id.roles).text = getString(R.string.i_am_a_user)
                    roles = "user"
                }
                else -> {
                    Toast.makeText(this@LoginPageActivity, "Invalid Type Login", Toast.LENGTH_SHORT).show()
                }
            }
        }

        switchpage.setOnClickListener {
            this.startActivity(Intent(this@LoginPageActivity, RegisterActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener {
            val editor = sharedpreferences.edit()

            // below two lines will put values for
            // email and password in shared preferences.
            editor.putString(EMAIL_KEY, tvUsername.text.toString())
            editor.putString(PASSWORD_KEY, tvPassword.text.toString())
            // to save our data with key and value.
            editor.apply()
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
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(baseContext, "Login failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            return
        }
        this.startActivity(Intent(baseContext, MainActivity2::class.java))
        finish()
    }
}