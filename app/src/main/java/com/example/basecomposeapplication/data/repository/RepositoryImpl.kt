package com.example.basecomposeapplication.data.repository

import com.example.basecomposeapplication.data.local.datastore.Pref
import com.example.basecomposeapplication.data.local.room.note.Note
import com.example.basecomposeapplication.data.local.room.note.NoteDao
import com.example.basecomposeapplication.data.model.TestModel
import com.example.basecomposeapplication.data.remote.NetworkDataSource
import com.example.basecomposeapplication.data.repository.error.ApiErrorMapper
import java.util.Date
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val dbDataSource: NoteDao,
    private val pref: Pref
) : Repository {
    override suspend fun getMessageList(name: String): TestModel {
        try {
            val response = networkDataSource.searchUserByName(name)
            pref.setAccessToken(response.age.toString())
            return response
        } catch (throwable: Throwable) {
            throw ApiErrorMapper.map(throwable)
        }
    }

    override suspend fun setNote(content: String, time: String) {
        try {
            dbDataSource.insertAll(Note(id = Date().time.toInt(), content, time))
        } catch (throwable: Throwable) {
            throw ApiErrorMapper.map(throwable)
        }
    }

//    override fun getListNote(): Flow<List<Note>> {
//        try{
//            return db.noteDao().getAll()
//        }catch (throwable: Throwable){
//            throw ApiErrorMapper.map(throwable)
//        }
//    }

    override suspend fun getListNote(): List<Note> {
        try {
            return dbDataSource.getAll()
        } catch (throwable: Throwable) {
            throw ApiErrorMapper.map(throwable)
        }
    }
}