package com.example.eatinggo.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CafeDao {
    @Query("SELECT * FROM cafe")
    fun getAll(): List<Cafe>

    @Query("SELECT * FROM cafe WHERE id IN (:cafeIds)")
    fun loadAllByIds(cafeIds: IntArray): List<Cafe>

    @Query("SELECT * FROM cafe WHERE name LIKE :name")
    fun findbyName(name: String): Cafe

    @Insert
    fun insertAll(vararg user: Cafe)

    @Delete
    fun delete(user: Cafe)

    @Query("SELECT * FROM cafe ORDER BY name ASC")
    fun getAlphabetized(): Flow<List<Cafe>>
}