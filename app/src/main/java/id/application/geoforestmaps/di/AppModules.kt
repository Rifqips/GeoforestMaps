package id.application.geoforestmaps.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import id.application.core.data.datasource.AppPreferenceDataSource
import id.application.core.data.datasource.AppPreferenceDataSourceImpl
import id.application.core.data.datasource.FirebaseDataSource
import id.application.core.data.datasource.FirebaseDataSourceImpl
import id.application.core.data.local.datastore.PreferenceDataStoreHelper
import id.application.core.data.local.datastore.PreferenceDataStoreHelperImpl
import id.application.core.data.local.datastore.appDataSource
import id.application.core.data.network.interceptor.AuthInterceptor
import id.application.core.utils.AssetWrapperApp
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModules {

    private val viewModelModule = module {
        viewModelOf(::VmApplication)
    }

    private val utilsModule = module {
        single { AssetWrapperApp(androidContext()) }
    }

    private val firebaseModule = module{
        single { Firebase.analytics }
        single { Firebase.crashlytics }
        single<FirebaseDataSource> { FirebaseDataSourceImpl(get()) }
    }

    private val localModule = module {
        single { androidContext().appDataSource }
        single<PreferenceDataStoreHelper> { PreferenceDataStoreHelperImpl(get()) }
    }

    private val networkModule = module{
    single { ChuckerInterceptor(androidContext()) }
    single { AuthInterceptor(get()) }
    }

    private val dataSourceModule = module {
        single<AppPreferenceDataSource> { AppPreferenceDataSourceImpl() }

    }

    private val repositoryModule = module {
    }

    private val pagingSource = module {
    }

    val modules: List<Module> = listOf(
        viewModelModule,
        utilsModule,
        localModule,
        networkModule,
        dataSourceModule,
        repositoryModule,
        utilsModule,
        pagingSource,
        firebaseModule
    )

}