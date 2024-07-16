package id.application.core.data.datasource

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import id.application.core.data.local.datastore.PreferenceDataStoreHelper

interface AppPreferenceDataSource {

    suspend fun getUserToken(): String
    suspend fun saveUserToken(token: String)
    suspend fun removeToken()


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

    companion object {
        val USER_ID_KEY = stringPreferencesKey("USER_ID_KEY")
        val USERNAME_KEY = stringPreferencesKey("USER_NAME_KEY")
        val USER_TOKEN_KEY = stringPreferencesKey("USER_TOKEN_KEY")
        val USER_SEARCH_KEY = stringPreferencesKey("USER_SEARCH_KEY")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("REFRESH_TOKEN_KEY")
        val USER_FIREBASE_TOKEN_KEY = stringPreferencesKey("USER_FIREBASE_TOKEN_KEY")
        val STATE_ONBOARDING = booleanPreferencesKey("STATE_ONBOARDING")
        val STATE_DARK_MODE = booleanPreferencesKey("STATE_DARK_MODE")
        val STATE_LOCALE = booleanPreferencesKey("STATE_LOCALE")
        val PREF_GRID_LAYOUT_WISHLIST = booleanPreferencesKey("PREF_GRID_LAYOUT_WISHLIST")
        val PREF_GRID_LAYOUT_STORE = booleanPreferencesKey("PREF_GRID_LAYOUT_STORE")
        val PREF_CHEKCED_CART = booleanPreferencesKey("PREF_CHEKCED_CART")
        val PREF_PRICE_CART = stringPreferencesKey("PREF_PRICE_CART")
        val PREF_COUNT_NUMBER_CART = stringPreferencesKey("PREF_COUNT_NUMBER_CART")
    }
}

