package com.glints.lingoparents.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

class TokenPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
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

    private val TOKEN_KEY = stringPreferencesKey("access_token")
}