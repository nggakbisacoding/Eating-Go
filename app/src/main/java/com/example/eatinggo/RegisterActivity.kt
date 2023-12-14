package com.example.eatinggo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eatinggo.databinding.RegisterPageBinding
import com.example.eatinggo.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: RegisterPageBinding
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var etconfPass: EditText
    private lateinit var database: DatabaseReference
    private lateinit var btnSignUp: Button
    private lateinit var tvRedirectRegis: TextView
    private lateinit var tvRegis: TextView
    private lateinit var etFirstName: EditText
    private lateinit var storageRef: StorageReference
    private lateinit var etLastName: EditText
    private lateinit var auth: FirebaseAuth
    private var regisType = "user"

    companion object {
        const val SHARED_PREFS = "shared_prefs"
        const val EMAIL_KEY = "email_key"
        const val PASSWORD_KEY = "password_key"
        const val ROLES = "roles"
    }
    private lateinit var sharedpreferences: SharedPreferences
    private var email: String? = null
    private var password: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        database = Firebase.database.reference
        storageRef = Firebase.storage.reference

        etFirstName = findViewById(R.id.reg_firstname)
        etLastName = findViewById(R.id.reg_lastname)
        etEmail = findViewById(R.id.reg_email)
        etPass = findViewById(R.id.reg_password)
        etconfPass = findViewById(R.id.conf_password)
        btnSignUp = findViewById(R.id.register_btn)
        tvRedirectRegis = findViewById(R.id.employee_regis)
        tvRegis = findViewById(R.id.tvregis_employee)
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        email = sharedpreferences.getString(EMAIL_KEY, null)
        password = sharedpreferences.getString(PASSWORD_KEY, null)

        tvRedirectRegis.setOnClickListener {
            when (regisType) {
                "employee" -> {
                    tvRegis.text = getString(R.string.i_am_a_user)
                    tvRedirectRegis.text = getString(R.string.register_as_employee)
                    regisType = "user"
                }
                "user" -> {
                    tvRegis.text = getString(R.string.i_am_a_employee)
                    tvRedirectRegis.text = getString(R.string.register_as_user)
                    regisType = "employee"
                }
                else -> {
                    Toast.makeText(this@RegisterActivity, "Invalid Regis Type", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnSignUp.setOnClickListener {
            if(regisType == "user") {
                signUpUser(etFirstName.text.toString().replaceFirstChar { firstChar ->
                    firstChar.uppercase()
                }, etLastName.text.toString().replaceFirstChar { firstChar ->
                    firstChar.uppercase()
                })
            } else {
                binding.content.visibility = View.GONE
                binding.registerBtn.visibility = View.GONE
                binding.confirmDelete.visibility = View.VISIBLE
                binding.sure.setOnClickListener {
                    signUpUser(etFirstName.text.toString().replaceFirstChar { firstChar ->
                        firstChar.uppercase()
                    }, etLastName.text.toString().replaceFirstChar { firstChar ->
                        firstChar.uppercase()
                    })
                }
                binding.cancel.setOnClickListener {
                    binding.content.visibility = View.VISIBLE
                    binding.registerBtn.visibility = View.VISIBLE
                    binding.confirmDelete.visibility = View.GONE
                }
            }
        }

        findViewById<TextView>(R.id.login_now).setOnClickListener {
            this.startActivity(Intent(this@RegisterActivity, LoginPageActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if(email != auth.currentUser?.email) {
            val editor = sharedpreferences.edit()
            editor.clear()
            editor.apply()
        }
    }

    private fun signUpUser(firstName: String, lastName: String) {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        val confpass = etconfPass.text.toString()

        // check pass
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if(!isValidString(email)) {
            Toast.makeText(baseContext, "Invalid email", Toast.LENGTH_SHORT).show()
        }

        if(pass != confpass) {
            Toast.makeText(this, "Password and Confirmation doesn't match", Toast.LENGTH_SHORT).show()
            return
        } else {
            if (pass.length < 8) {
                Toast.makeText(this, "Password need 8 character or more", Toast.LENGTH_SHORT).show()
                return
            }
        }
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up Please Verify your email", Toast.LENGTH_SHORT).show()
                val user = auth.currentUser
                storageRef.child("file/PuraUlunDanuBratan.jpg").downloadUrl.addOnSuccessListener {
                    val profileUpdates = userProfileChangeRequest{
                        displayName = "$firstName $lastName"
                        photoUri = it
                    }
                    database.child("users").child(user?.uid.toString()).setValue(User(id = user?.uid!!, firstName = firstName, lastName = lastName, phoneNumber = null, email = email, password = pass, profileImage = profileUpdates.photoUri.toString(), location = null, onSeat = false, userCategory = regisType))
                    user.updateProfile(profileUpdates).addOnCompleteListener { voidTask ->
                        if(voidTask.isSuccessful) {
                            val editor = sharedpreferences.edit()

                            // below two lines will put values for
                            // email and password in shared preferences.
                            editor.putString(LoginPageActivity.PASSWORD_KEY, pass)
                            editor.putString(ROLES, regisType)
                            // to save our data with key and value.
                            editor.apply()
                            updateUI(user)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmailVerify(user: FirebaseUser?) {
        user?.sendEmailVerification()?.addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(this, "Please check your email", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(!user?.isEmailVerified!!) {
            sendEmailVerify(user)
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(baseContext, LoginPageActivity::class.java))
            finish()
        }
        return
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
}