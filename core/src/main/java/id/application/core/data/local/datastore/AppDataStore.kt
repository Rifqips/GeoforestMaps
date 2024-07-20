package id.application.core.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.appDataSource by preferencesDataStore(
    name = "appPreference"
)