package com.example.eatinggo.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChairDao {
    @Query("SELECT * FROM chair")
    fun getAll(): List<Chair>

    @Query("SELECT * FROM chair WHERE id IN (:chairIds)")
    fun loadAllByIds(chairIds: IntArray): List<Chair>

    @Query("SELECT * FROM chair WHERE cafe_id LIKE :cafeId")
    fun findbycafeId(cafeId: Int): Chair

    @Query("SELECT * FROM chair WHERE user_id Like :userId")
    fun findbyUserId(userId: Int): Chair

    @Query("SELECT * FROM chair WHERE is_used Like :isUsed")
    fun findbyIsUsed(isUsed: Boolean): Chair

    @Insert
    fun insertAll(vararg user: Chair)

    @Delete
    fun delete(user: Chair)

    @Query("SELECT * FROM chair ORDER BY is_used ASC")
    fun getAlphabetized(): Flow<List<Chair>>
}