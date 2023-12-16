package com.example.eatinggo

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eatinggo.databinding.ReservationDetailPageBinding
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class ReservationDetailPageActivity : AppCompatActivity() {
    private lateinit var binding: ReservationDetailPageBinding
    private lateinit var dbref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReservationDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbref = Firebase.database.reference
    }

    override fun onStart() {
        super.onStart()
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                if(dataSnapshot.child("onSeat").value == true) {
                    println(dataSnapshot.value)
                }
                println(dataSnapshot.value)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                println(dataSnapshot.value)
                println(dataSnapshot.key)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                println(dataSnapshot.key)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                println(dataSnapshot.value)
                println(dataSnapshot.key)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(
                    baseContext,
                    "Failed to load comments.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
        dbref.child("users").addChildEventListener(childEventListener)
    }
}