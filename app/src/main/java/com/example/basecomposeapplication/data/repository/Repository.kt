package com.example.basecomposeapplication.data.repository

import com.example.basecomposeapplication.data.local.room.note.Note
import com.example.basecomposeapplication.data.model.TestModel


interface Repository {
    suspend fun getMessageList(name: String): TestModel
    suspend fun setNote(content: String, time: String)
    suspend fun getListNote(): List<Note>
}