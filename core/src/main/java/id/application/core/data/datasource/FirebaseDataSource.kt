package id.application.core.data.datasource

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

interface FirebaseDataSource {


    fun debugSreenView(debug : String)

    fun logEvent(eventName : String, bundle : Bundle)

    fun logExeception(exception: Exception)

}

class FirebaseDataSourceImpl(
    private val firebaseAnalytics: FirebaseAnalytics
) : FirebaseDataSource {
    override fun debugSreenView(debug: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply { putString("screen_view",debug) })
    }

    override fun logEvent(eventName: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    override fun logExeception(exception: Exception) {
        FirebaseCrashlytics.getInstance().recordException(exception)
    }

}
