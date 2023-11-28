package com.example.eatinggo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "chair", foreignKeys = [ForeignKey(Cafe::class,
    ["id"], ["cafe_id"], CASCADE)]
)
data class Chair(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "is_used") val chairUsed: Boolean? = false,
    @ColumnInfo(name = "cafe_id", index = true) val cafeId: Int,
    @ColumnInfo(name = "user_id") val userId: Int? = null
)