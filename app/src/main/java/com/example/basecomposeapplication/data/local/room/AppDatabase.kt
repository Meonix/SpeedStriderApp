package com.example.basecomposeapplication.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.basecomposeapplication.data.local.room.note.Note
import com.example.basecomposeapplication.data.local.room.note.NoteDao

@Database(entities = [Note::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

}