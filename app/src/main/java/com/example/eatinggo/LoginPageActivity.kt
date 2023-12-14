package com.example.eatinggo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eatinggo.databinding.LoginPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.util.regex.Pattern


class LoginPageActivity : AppCompatActivity() {
    private lateinit var binding: LoginPageBinding
    private lateinit var switchpage: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvPassword: TextView
    private lateinit var tvEmail: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    companion object {
        const val SHARED_PREFS = "shared_prefs"
        const val EMAIL_KEY = "email_key"
        const val PASSWORD_KEY = "password_key"
        const val ROLES = "roles"
    }
    private lateinit var sharedpreferences: SharedPreferences
    private var email: String? = null
    private var password: String? = null
    private var roles: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        tvUsername = findViewById(R.id.login_username)
        tvPassword = findViewById(R.id.login_password)
        switchpage = findViewById(R.id.switch_regis)
        tvEmail = findViewById(R.id.email_reset)
        db = Firebase.database.reference

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        email = sharedpreferences.getString(EMAIL_KEY, null)
        password = sharedpreferences.getString(PASSWORD_KEY, null)
        roles = sharedpreferences.getString(ROLES, null)

        switchpage.setOnClickListener {
            this.startActivity(Intent(this@LoginPageActivity, RegisterActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener {
            signIn()
        }

        binding.resetPass.setOnClickListener {
            binding.loginContent.visibility = View.GONE
            binding.loginBtn.visibility = View.GONE
            binding.reset.visibility = View.VISIBLE
            binding.resetBtn.visibility = View.VISIBLE
            binding.cancel.visibility = View.VISIBLE
        }

        binding.cancel.setOnClickListener {
            hidden()
        }

        binding.resetBtn.setOnClickListener {
            val email = tvEmail.text.toString()
            if(email.isBlank() || !isValidString(email)) {
                Toast.makeText(baseContext, "Email not valid!", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(baseContext, "Check your email to change password", Toast.LENGTH_LONG).show()
                        hidden()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (email != null && password != null) {
            signIn(email!!, password!!)
            checkShared(email!!, password!!)
            val i = Intent(baseContext, MainActivity2::class.java)
            startActivity(i)
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
        checkIfEmailVerified()
        checkShared(email, password)
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
                    checkShared(tvUsername.text.toString(), tvPassword.text.toString())
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
        this.startActivity(Intent(baseContext, MainActivity2::class.java))
        finish()
    }

    private val emailpattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    private fun isValidString(str: String): Boolean{
        return emailpattern.matcher(str).matches()
    }

    private fun hidden() {
        binding.loginContent.visibility = View.VISIBLE
        binding.loginBtn.visibility = View.VISIBLE
        binding.reset.visibility = View.GONE
        binding.cancel.visibility = View.GONE
        binding.resetBtn.visibility = View.GONE
    }

    private fun checkShared(email: String, password: String) {
        if(roles == null) {
            db.child("users").child(auth.currentUser?.uid.toString()).child("userCategory").get().addOnCompleteListener {
                if(it.isSuccessful) {
                    val roleses = it.result.value.toString()
                    val editor = sharedpreferences.edit()
                    editor.putString(EMAIL_KEY, email)
                    editor.putString(PASSWORD_KEY, password)
                    editor.putString(ROLES, roleses)
                    editor.apply()
                    roles = roleses
                }
            }
        } else {
            val editor = sharedpreferences.edit()
            editor.putString(EMAIL_KEY, email)
            editor.putString(PASSWORD_KEY, password)
            editor.apply()
        }
    }
}