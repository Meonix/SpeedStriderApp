package com.example.basecomposeapplication.data.local.datastore

import androidx.datastore.preferences.core.stringPreferencesKey


class Pref(private val preferences: PreferencesDataStore) {
    private val accessTokenKey = stringPreferencesKey("ACCESS_TOKEN")

    suspend fun getAccessToken(): String {
        return preferences.read(accessTokenKey, "")
    }

    suspend fun setAccessToken(value: String) {
        preferences.write(accessTokenKey, value)
    }
}