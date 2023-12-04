package com.example.eatinggo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "cafe")
data class Cafe(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id", index = true) var id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "owner") val ownerName: String?,
    @ColumnInfo(name = "location") val cafeLocation: String?,
    @ColumnInfo(name = "total_seat") val totalSeat: Int,
    @ColumnInfo(name = "open_hour") val openHour: Time,
    @ColumnInfo(name = "close_hour") val  closeHour: Time,
    @ColumnInfo(name = "cafe_image") val cafeImage: Int,
    @ColumnInfo(name = "reservation_available") val reservationAvailable: Boolean? = false,
    @ColumnInfo(name = "is_open") val cafeOpen: Boolean
)