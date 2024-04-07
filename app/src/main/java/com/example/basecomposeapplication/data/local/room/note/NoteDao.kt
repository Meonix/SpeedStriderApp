package com.example.basecomposeapplication.data.local.room.note

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Note>

    @Insert
    fun insertAll(vararg note: Note)

    @Delete
    fun delete(user: Note)
}