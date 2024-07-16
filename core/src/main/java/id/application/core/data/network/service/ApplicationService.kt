package id.application.core.data.network.service

import com.chuckerteam.chucker.api.ChuckerInterceptor
import id.application.core.data.network.interceptor.AuthInterceptor
import id.application.core.data.network.model.geotags.ResponseAllGeotags
import id.application.core.data.network.model.login.RequestLoginItem
import id.application.core.data.network.model.login.ResponseLoginItem
import id.application.core.data.network.model.logout.ResponseLogoutItem
import id.application.core.data.network.model.profile.ResponseProfileItem
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApplicationService {

    @POST("v1/auth/login")
    suspend fun userLogin(@Body userLoginRequest: RequestLoginItem): ResponseLoginItem

    @POST("v1/auth/logout")
    suspend fun userLogout(): ResponseLogoutItem

    @GET("v1/profile")
    suspend fun userProfile(): ResponseProfileItem

    @GET("v1/geotags")
    suspend fun getAllGeotags(
        @Query("limit") limitItem:Int? = null,
        @Query("page") pageItem:Int? = null,
    ): ResponseAllGeotags

    @GET("v1/plants")
    suspend fun getAllplants(
        @Query("limit") limitItem:Int? = null,
    ): ResponseAllGeotags


    companion object{
        @JvmStatic
        operator fun invoke(
            chucker: ChuckerInterceptor,
            authInterceptor: AuthInterceptor,
        ): ApplicationService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(chucker)
                .addInterceptor(authInterceptor)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()
            val retrofit = Retrofit.Builder()
                // .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return retrofit.create(ApplicationService::class.java)
        }

    }
}