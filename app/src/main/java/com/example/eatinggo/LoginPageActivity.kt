package com.example.eatinggo

import android.content.Intent
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
            signIn()
        }
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