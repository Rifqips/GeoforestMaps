package id.application.geoforestmaps.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import id.application.core.data.datasource.AppPreferenceDataSource
import id.application.core.data.datasource.AppPreferenceDataSourceImpl
import id.application.core.data.datasource.ApplicationDataSource
import id.application.core.data.datasource.ApplicationDataSourceImpl
import id.application.core.data.datasource.FirebaseDataSource
import id.application.core.data.datasource.FirebaseDataSourceImpl
import id.application.core.data.local.database.ApplicationDatabase
import id.application.core.data.local.datastore.PreferenceDataStoreHelper
import id.application.core.data.local.datastore.PreferenceDataStoreHelperImpl
import id.application.core.data.local.datastore.appDataSource
import id.application.core.data.network.interceptor.AuthInterceptor
import id.application.core.data.network.service.ApplicationService
import id.application.core.domain.paging.BlockPagingMediator
import id.application.core.domain.paging.GeotagingPagingMediator
import id.application.core.domain.repository.ApplicationRepository
import id.application.core.domain.repository.ApplicationRepositoryImpl
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

    private val firebaseModule = module {
        single { Firebase.analytics }
        single { Firebase.crashlytics }
        single<FirebaseDataSource> { FirebaseDataSourceImpl(get()) }
    }

    private val localModule = module {
        single { androidContext().appDataSource }
        single { ApplicationDatabase.getInstance(get()) }
        single<PreferenceDataStoreHelper> { PreferenceDataStoreHelperImpl(get()) }
        single { get<ApplicationDatabase>().blocksDao() }
        single { get<ApplicationDatabase>().geotagsDao() }
        single { get<ApplicationDatabase>().plantsDao() }
        single { get<ApplicationDatabase>().geotagsOfflineDao() }

    }

    private val networkModule = module {
        single { ChuckerInterceptor(androidContext()) }
        single { AuthInterceptor(get(), get()) }
        single { ApplicationService.invoke(get(), get()) }
    }

    private val dataSourceModule = module {
        single<AppPreferenceDataSource> { AppPreferenceDataSourceImpl(get()) }
        single<ApplicationDataSource> { ApplicationDataSourceImpl(get()) }
    }

    private val repositoryModule = module {
        single<ApplicationRepository> {
            ApplicationRepositoryImpl(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }

    private val pagingSource = module {
        single { GeotagingPagingMediator(get(), get()) }
        single { BlockPagingMediator(get(), get()) }
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