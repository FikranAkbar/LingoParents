package com.glints.lingoparents.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class TokenPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("access_token")

        //amin
        private val EMAIL_KEY = stringPreferencesKey("email")


        @Volatile
        private var INSTANCE: TokenPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): TokenPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getAccessToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    //amin
    fun getAccessEmail(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[EMAIL_KEY] ?: ""
        }
    }



    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    //amin
    suspend fun saveAccessEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
        }
    }



    suspend fun resetAccessToken() {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = ""
        }
    }

    suspend fun resetAccessEmail() {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = ""
        }
    }


}