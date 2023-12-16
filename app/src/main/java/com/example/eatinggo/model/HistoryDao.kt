package com.example.eatinggo.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.sql.Date

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Query("SELECT * FROM history WHERE id IN (:historyIds)")
    fun loadAllByIds(historyIds: IntArray): List<History>

    @Query("SELECT * FROM history WHERE history_date LIKE :historyDate")
    fun findbycafeId(historyDate: Date): History

    @Query("SELECT * FROM history WHERE user_id Like :userId")
    fun findbyUserId(userId: Int): History

    @Query("SELECT * FROM history WHERE cafe_id Like :cafeId")
    fun findbyIsUsed(cafeId: Int): History

    @Insert
    fun insertAll(vararg user: History)

    @Delete
    fun delete(user: History)

    @Query("SELECT * FROM history ORDER BY history_date ASC")
    fun getAlphabetized(): Flow<List<History>>
}