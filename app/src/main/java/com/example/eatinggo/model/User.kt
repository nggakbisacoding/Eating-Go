package com.example.eatinggo.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val firstName: String?= null, val lastName: String?=null, val email: String?= null, val pass: String?=null, val photoUri: String? = null)
