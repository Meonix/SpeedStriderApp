package com.example.basecomposeapplication.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface BaseDAO<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPayload(model: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCollection(coll: Collection<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArray(array: Array<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePayload(t: T)

    @Delete
    fun deletePayload(t: T)

}