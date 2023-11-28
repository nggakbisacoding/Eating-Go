package com.example.eatinggo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.eatinggo.model.Cafe
import com.example.eatinggo.model.CafeDao
import com.example.eatinggo.model.Chair
import com.example.eatinggo.model.ChairDao
import com.example.eatinggo.model.History
import com.example.eatinggo.model.HistoryDao

// Annotates class to be a Room Database with a table (entity) of the Word class
@TypeConverters(DateConverter::class)
@Database(entities = [Chair::class, History::class, Cafe::class], version = 1, exportSchema = true)
abstract class ControllerDatabase : RoomDatabase() {

    abstract fun CafeDao(): CafeDao?
    abstract fun HistoryDao(): HistoryDao?
    abstract fun ChairDao(): ChairDao?

    companion object {
        @Volatile
        private var INSTANCE: ControllerDatabase? = null

        fun getDatabase(context: Context): ControllerDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(ControllerDatabase::class.java) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ControllerDatabase::class.java,
                    "database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}