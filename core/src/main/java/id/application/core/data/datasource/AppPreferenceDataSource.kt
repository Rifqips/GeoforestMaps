package id.application.core.data.datasource

import androidx.datastore.preferences.core.stringPreferencesKey
import id.application.core.data.local.datastore.PreferenceDataStoreHelper

interface AppPreferenceDataSource {

    suspend fun getUserToken(): String
    suspend fun saveUserToken(token: String)
    suspend fun removeToken()
    suspend fun saveUserName(name: String)
    suspend fun getUserName(): String
    suspend fun saveBlockName(blockName: String)
    suspend fun getBlockName(): String
    suspend fun deleteBlockName()
    suspend fun saveUserEmail(email: String)
    suspend fun getUserEmail(): String
    suspend fun deleteAllData()
}

class AppPreferenceDataSourceImpl(
    private val preferenceHelper: PreferenceDataStoreHelper
) : AppPreferenceDataSource {
    override suspend fun getUserToken(): String {
        return preferenceHelper.getFirstPreference(USER_TOKEN_KEY, "")
    }
    override suspend fun saveUserToken(token: String) {
        return preferenceHelper.putPreference(USER_TOKEN_KEY, token)
    }

    override suspend fun removeToken() {
        preferenceHelper.removePreference(USER_TOKEN_KEY)
    }

    override suspend fun saveUserName(name: String) {
        return preferenceHelper.putPreference(USER_NAME_KEY, name)
    }

    override suspend fun getUserName(): String {
        return preferenceHelper.getFirstPreference(USER_NAME_KEY, "")
    }

    override suspend fun saveBlockName(blockName: String) {
        return preferenceHelper.putPreference(BLOCK_NAME, blockName)
    }

    override suspend fun getBlockName(): String {
        return preferenceHelper.getFirstPreference(BLOCK_NAME, "")
    }

    override suspend fun deleteBlockName() {
        preferenceHelper.removePreference(BLOCK_NAME)
    }

    override suspend fun saveUserEmail(email: String) {
        return preferenceHelper.putPreference(USER_EMAIL_KEY, email)
    }

    override suspend fun getUserEmail(): String {
        return preferenceHelper.getFirstPreference(USER_EMAIL_KEY, "")
    }

    override suspend fun deleteAllData() {
        preferenceHelper.clearAllPreference()
    }

    companion object {
        val USER_TOKEN_KEY = stringPreferencesKey("USER_TOKEN_KEY")
        val USER_NAME_KEY = stringPreferencesKey("USER_NAME_KEY")
        val USER_EMAIL_KEY = stringPreferencesKey("USER_EMAIL_KEY")
        val BLOCK_NAME = stringPreferencesKey("BLOCK_NAME")
    }
}

