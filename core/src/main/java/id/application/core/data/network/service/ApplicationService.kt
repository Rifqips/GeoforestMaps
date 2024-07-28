package id.application.core.data.network.service

import com.chuckerteam.chucker.api.ChuckerInterceptor
import id.application.core.BuildConfig
import id.application.core.data.network.interceptor.AuthInterceptor
import id.application.core.data.network.model.blocks.ResponseAllBlocksItem
import id.application.core.data.network.model.geotags.ResponseAllGeotagingItem
import id.application.core.data.network.model.login.RequestLoginItem
import id.application.core.data.network.model.login.ResponseLoginItem
import id.application.core.data.network.model.logout.ResponseLogoutItem
import id.application.core.data.network.model.plants.ResponseAllPlantsItem
import id.application.core.data.network.model.profile.ResponseProfileItem
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApplicationService {

    @POST("v1/auth/login")
    suspend fun userLogin(@Body userLoginRequest: RequestLoginItem): ResponseLoginItem

    @POST("v1/auth/logout")
    suspend fun userLogout(): Response<ResponseLogoutItem>

    @GET("v1/profile")
    suspend fun userProfile(): ResponseProfileItem

    @GET("v1/geotags")
    suspend fun getAllGeotaging(
        @Query("sort") sort:String? = null,
        @Query("block") blockId:Int? = null,
        @Query("limit") limitItem:Int? = null,
        @Query("page") pageItem:Int? = null,
    ): ResponseAllGeotagingItem

    @Multipart
    @POST("v1/geotags")
    suspend fun createGeotaging(
        @Part("plant_id") plantId: RequestBody?,
        @Part("block_id") blockId: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("altitude") altitude: RequestBody?,
        @Part userImage: MultipartBody.Part?
    ): ResponseAllGeotagingItem

    @GET("v1/plants")
    suspend fun getAllPlants(
        @Query("limit") limitItem:Int? = null,
        @Query("page") pageItem:Int? = null,
    ): ResponseAllPlantsItem

    @GET("v1/blocks")
    suspend fun getAllBlocks(
        @Query("limit") limitItem:Int? = null,
        @Query("page") pageItem:Int? = null,
    ): ResponseAllBlocksItem

    @GET("v1/exports")
    suspend fun eksports(
        @Query("type") type:String? = null,
        @Query("block_id") blockId:Int? = null,
        ): Response<ResponseBody>


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
                 .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return retrofit.create(ApplicationService::class.java)
        }
    }
}
