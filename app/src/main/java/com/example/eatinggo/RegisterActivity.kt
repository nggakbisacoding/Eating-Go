package com.example.eatinggo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eatinggo.databinding.RegisterPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: RegisterPageBinding
    private lateinit var etEmailorPhone: EditText
    private lateinit var etPass: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvRedirectRegis: TextView
    private lateinit var tvRegis: TextView
    lateinit var etFirstName: EditText
    lateinit var etLastName: EditText
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        etFirstName = findViewById(R.id.reg_firstname)
        etLastName = findViewById(R.id.reg_lastname)
        etEmailorPhone = findViewById(R.id.reg_email)
        etPass = findViewById(R.id.reg_password)
        btnSignUp = findViewById(R.id.register_btn)
        tvRedirectRegis = findViewById(R.id.employee_regis)
        tvRegis = findViewById(R.id.tvregis_employee)
        var regisType = "user"

        /*
        var phoneNumber
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        Toast.makeText(this@RegisterActivity, e.toString(), Toast.LENGTH_LONG).show()
                    }

                    is FirebaseTooManyRequestsException -> {
                        Toast.makeText(this@RegisterActivity, e.toString(), Toast.LENGTH_SHORT).show()
                    }

                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        Toast.makeText(this@RegisterActivity, e.toString(), Toast.LENGTH_LONG).show()
                    }
                }
                return
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                verifyPhoneNumberWithCode(storedVerificationId, resendToken.toString())
            }
        }
         */

        btnSignUp.setOnClickListener {
            signUpUser(etFirstName.text.toString().replaceFirstChar { firstChar ->
                firstChar.uppercase()
            }, etLastName.text.toString().replaceFirstChar { firstChar ->
                firstChar.uppercase()
            })
        }

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

        findViewById<TextView>(R.id.login_now).setOnClickListener {
            this.startActivity(Intent(this@RegisterActivity, LoginPageActivity::class.java))
            finish()
        }
    }

    private fun signUpUser(firstName: String, lastName: String) {
        val email = etEmailorPhone.text.toString()
        val pass = etPass.text.toString()

        // check pass
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass.length < 8) {
            Toast.makeText(this, "Password need 8 character or more", Toast.LENGTH_SHORT)
                .show()
            return
        }
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                val profileUpdates = userProfileChangeRequest{
                    displayName = "$firstName $lastName"
                }
                val user = auth.currentUser
                user!!.updateProfile(profileUpdates).addOnCompleteListener {
                    if(it.isSuccessful) {
                        updateUI(user)
                    }
                }
            } else {
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* SMS Verification For Phone Number Register Access (Disable For Now)
    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // (optional) Activity for callback binding
            // If no activity is passed, reCAPTCHA verification can not be used.
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user
                    updateUI(user)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this@RegisterActivity, "Verification code Invalid", Toast.LENGTH_LONG).show()
                    }
                    val user = null
                    updateUI(user)
                }
            }
    }
     */

    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            return
        }
        this.startActivity(Intent(baseContext, MainActivity2::class.java))
        finish()
    }
}