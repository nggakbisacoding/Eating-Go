package com.example.eatinggo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "history", foreignKeys = [ForeignKey(Cafe::class,
    ["id"], ["cafe_id"], ForeignKey.CASCADE)]
)
data class History(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "history_date") val historyDate: Date,
    @ColumnInfo(name = "cafe_id") val cafeId: Int,
    @ColumnInfo(name = "user_id") val userId: Int
)