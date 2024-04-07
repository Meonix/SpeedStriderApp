package com.example.basecomposeapplication.data.local.room.note

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "time") val time: String?,
)
