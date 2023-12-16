package com.example.eatinggo.data

import androidx.room.TypeConverter
import java.sql.Date
import java.sql.Time

class DateConverter {
    @TypeConverter
    fun fromDateToLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun fromLongToDate(date: Long): Date {
        return Date(date)
    }

    @TypeConverter
    fun fromTimetoLong(time: Time): Long {
        return time.time
    }

    @TypeConverter
    fun fromLongtoTime(long:  Long): Time {
        return Time(long)
    }
}