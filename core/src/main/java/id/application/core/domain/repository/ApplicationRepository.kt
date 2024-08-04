package id.application.core.domain.repository

import androidx.paging.PagingData
import id.application.core.R
import id.application.core.data.datasource.AppPreferenceDataSource
import id.application.core.data.datasource.ApplicationDataSource
import id.application.core.data.local.database.geotags.GeotagsOfflineDao
import id.application.core.data.local.database.plants.PlantsDao
import id.application.core.data.network.model.login.RequestLoginItem
import id.application.core.data.network.model.profile.toProfileResponse
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.domain.model.blocks.ItemAllBlocksResponse
import id.application.core.domain.model.blocks.toAllBlockResponse
import id.application.core.domain.model.geotags.ItemAllGeotagingOffline
import id.application.core.domain.model.geotags.ItemAllGeotagingResponse
import id.application.core.domain.model.geotags.toAllGeotagingResponse
import id.application.core.domain.model.login.UserLoginRequest
import id.application.core.domain.model.login.UserLoginResponse
import id.application.core.domain.model.login.toLoginResponse
import id.application.core.domain.model.plants.ItemAllPlants
import id.application.core.domain.model.plants.ItemAllPlantsResponse
import id.application.core.domain.model.plants.toAllPlantsResponse
import id.application.core.domain.model.profile.UserProfileResponse
import id.application.core.domain.paging.BlockPagingMediator
import id.application.core.utils.AssetWrapperApp
import id.application.core.utils.ResultWrapper
import id.application.core.utils.proceed
import id.application.core.utils.proceedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

interface  ApplicationRepository{

    suspend fun userLogin(request: UserLoginRequest): Flow<ResultWrapper<UserLoginResponse>>

    suspend fun userLogout(): Flow<ResultWrapper<Boolean>>

    suspend fun userProfile(): Flow<ResultWrapper<UserProfileResponse>>

    suspend fun createGeotaging(
        plantId: RequestBody?,
        blockId: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?,
        altitude: RequestBody?,
        userImage: MultipartBody.Part? = null,
        photoBase64: RequestBody? = null
    ): Result<Unit>

    suspend fun getAllGeotaging(
        block:String? = null,
        createdBy:String? = null,
        limitItem:Int? = null,
        pageItem:Int? = null,
    ): ItemAllGeotagingResponse

    suspend fun fetchAllBlockLocal(): Flow<PagingData<ItemAllBlocks>>

    suspend fun getAllPlants(
        limitItem:Int? = null,
        pageItem:Int? = null,
    ): Flow<ResultWrapper<ItemAllPlantsResponse>>

    suspend fun retrieveAllPlants(): List<ItemAllPlants>


    suspend fun getAllBlocks(
        limitItem:Int? = null,
        pageItem:Int? = null,
    ): ItemAllBlocksResponse

    suspend fun exportFile(type: String?, block: String?, geoatagId : Int?): Response<ResponseBody>

    fun getAllGeotagingOffline(): Flow<ResultWrapper<List<ItemAllGeotagingOffline>>>

    suspend fun insertAllGeotagsOffline(geotagsOffline: ItemAllGeotagingOffline)

    suspend fun deleteGeotaggingById(geotaggingId: Int)

}

class ApplicationRepositoryImpl(
    private val appDataSource: ApplicationDataSource,
    private val appPreferenceDataSource: AppPreferenceDataSource,
    private val assetWrapper : AssetWrapperApp,
    private val pagingBlock : BlockPagingMediator,
    private val plantsDao: PlantsDao,
    private val geotagsOfflineDao : GeotagsOfflineDao
) : ApplicationRepository{

    override suspend fun userLogin(request: UserLoginRequest): Flow<ResultWrapper<UserLoginResponse>> {
        return proceedFlow {
            val dataReq = RequestLoginItem(request.email, request.password)
            val loginResult = appDataSource.userLogin(dataReq)
            appPreferenceDataSource.saveUserToken(loginResult.accessToken)
            appPreferenceDataSource.saveUserName(loginResult.user.name)
            appPreferenceDataSource.saveUserEmail(loginResult.user.email)
            loginResult.toLoginResponse()
        }
    }

    override suspend fun userLogout(): Flow<ResultWrapper<Boolean>> = flow {
        val response = appDataSource.userLogout()
        if (response.isSuccessful){
            appPreferenceDataSource.deleteAllData()
            emit(ResultWrapper.Success(true))
        } else {
            emit(ResultWrapper.Error(Exception(assetWrapper.getString(R.string.text_logout_failed_with_response_code) + response.code().toString())))
        }
    }.catch { e ->
        emit(ResultWrapper.Error(e as? Exception ?: Exception(assetWrapper.getString(R.string.text_unknown_error))))
    }

    override suspend fun userProfile(): Flow<ResultWrapper<UserProfileResponse>> {
        return proceedFlow {
            appDataSource.userProfile().toProfileResponse()
        }.catch { e ->
            emit(ResultWrapper.Error(e as? Exception ?: Exception(assetWrapper.getString(R.string.text_unknown_error))))
        }
    }

    override suspend fun createGeotaging(
        plantId: RequestBody?,
        blockId: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?,
        altitude: RequestBody?,
        userImage: MultipartBody.Part?,
        photoBase64: RequestBody?
    ): Result<Unit> {
        return appDataSource.createGeotaging(
            plantId, blockId, latitude, longitude, altitude, userImage, photoBase64
        )
    }

    override suspend fun getAllGeotaging(
        block:String?,
        createdBy:String?,
        limitItem: Int?,
        pageItem: Int?
    ): ItemAllGeotagingResponse {
        return appDataSource.getAllGeotaging(block, createdBy,limitItem, pageItem).toAllGeotagingResponse()
    }


    override suspend fun fetchAllBlockLocal(): Flow<PagingData<ItemAllBlocks>> {
        return pagingBlock.fetchBlocks()

    }

    override suspend fun getAllPlants(
        limitItem: Int?,
        pageItem: Int?
    ): Flow<ResultWrapper<ItemAllPlantsResponse>> {
        return proceedFlow {
            val list = appDataSource.getAllPlants(limitItem, pageItem).toAllPlantsResponse()
            plantsDao.insertAllplants(list.data.items)
            plantsDao.retrieveAllPlants()
            list
        }.catch { e ->
            emit(ResultWrapper.Error(e as? Exception ?: Exception(assetWrapper.getString(R.string.text_unknown_error))))
        }
    }

    override suspend fun retrieveAllPlants(): List<ItemAllPlants> {
        return plantsDao.retrieveAllPlants()
    }

    override suspend fun getAllBlocks(
        limitItem: Int?,
        pageItem: Int?
    ): ItemAllBlocksResponse {
        return appDataSource.getAllBlocks(limitItem, pageItem).toAllBlockResponse()

    }


    override suspend fun exportFile(type: String?, block: String?, geoatagId: Int?):  Response<ResponseBody> {
        return appDataSource.exportFile(type, block, geoatagId)
    }

    override fun getAllGeotagingOffline(): Flow<ResultWrapper<List<ItemAllGeotagingOffline>>> {
        return geotagsOfflineDao.getAllGeotagsOffline().map {
            proceed {
                it.toList()
            }
        }.map{
            if (it.payload?.isEmpty() == true) {
                ResultWrapper.Empty(it.payload)
            } else {
                it
            }
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(3000)
        }
    }

    override suspend fun insertAllGeotagsOffline(geotagsOffline: ItemAllGeotagingOffline) {
        return geotagsOfflineDao.insertAllGeotagsOffline(geotagsOffline)
    }

    override suspend fun deleteGeotaggingById(geotaggingId: Int) {
        return geotagsOfflineDao.deleteGeotaggingById(geotaggingId)
    }

}
