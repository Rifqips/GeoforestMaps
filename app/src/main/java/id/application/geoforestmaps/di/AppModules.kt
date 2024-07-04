package id.application.geoforestmaps.di

import id.application.core.utils.AssetWrapperApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModules {


    private val viewModelModule = module {
    }

    private val utilsModule = module {
        single { AssetWrapperApp(androidContext()) }
    }

    private val firebaseModule = module{
    }

    private val localModule = module {
    }

    private val networkModule = module {
    }

    private val dataSourceModule = module {
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