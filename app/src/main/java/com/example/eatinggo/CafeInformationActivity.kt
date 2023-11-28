package com.example.eatinggo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eatinggo.data.ControllerDatabase
import com.example.eatinggo.databinding.CafeInformationBinding
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class CafeInformationActivity : AppCompatActivity() {
    private lateinit var database: ControllerDatabase
    private lateinit var binding: CafeInformationBinding
    private lateinit var firedb: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CafeInformationBinding.inflate(layoutInflater)
        firedb = Firebase.database.reference
        setContentView(binding.root)
        database = ControllerDatabase.getDatabase(baseContext)
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        firedb.child("Cafe").addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                for(e in snapshot.children) {
                    if(snapshot.key == "cafe1") {
                        if(e.key == "name") {
                            binding.cafeName.text = e.value.toString()
                        }
                        if(e.key == "location") {
                            binding.cafeAddress.text = e.value.toString()
                        }
                        if(e.key == "used") {
                            binding.seat.text = e.value.toString()
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}