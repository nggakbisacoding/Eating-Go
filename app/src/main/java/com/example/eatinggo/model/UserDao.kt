package com.example.eatinggo.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Query("SELECT * FROM user WHERE email LIKE :email")
    fun findbyEmail(email: String): User

    @Insert
    fun insertAll(vararg user: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM user WHERE id LIKE :userId")
    fun deleteById(userId: Int)

    @Query("SELECT * FROM user ORDER BY first_name ASC")
    fun getAlphabetized(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert (user: User)

    @Update
    fun update (user: User)

    @get:Query("SELECT * FROM user ORDER BY id ASC")
    val allNotes: LiveData<List<User>>
}