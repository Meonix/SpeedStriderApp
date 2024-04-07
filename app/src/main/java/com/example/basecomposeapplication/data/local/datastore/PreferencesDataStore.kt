package com.example.basecomposeapplication.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlin.reflect.KClass


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "application_preferences")

class PreferencesDataStore(private val context: Context, private val gson: Gson) {
    /**
     * in case T is a basic data type like Int, String, Boolean, Float, Long
     */
    suspend fun <T> read(key: Preferences.Key<T>, default: T): T {
        val preferences = context.dataStore.data.first()
        return preferences[key] ?: default
    }

    suspend fun <T> write(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    /**
     * -----------------------------------------------------------------------
     */

    suspend fun <T : Any> obj(key: Preferences.Key<String>, cls: KClass<T>): T? {
        val json = read(key, "")
        return gson.fromJson(json, cls.java)
    }

    suspend fun <T : Any> list(key: Preferences.Key<String>, cls: KClass<Array<T>>): List<T>? {
        val json = read(key, "")
        return gson.fromJson(json, cls.java)?.toList()
    }

    suspend fun <T : Any> putObj(key: Preferences.Key<String>, value: T?) {
        val json = gson.toJson(value)
        write(key, json)
    }

    suspend fun <T : Any> putList(key: Preferences.Key<String>, list: List<T>?) {
        val json = gson.toJson(list)
        write(key, json)
    }

}