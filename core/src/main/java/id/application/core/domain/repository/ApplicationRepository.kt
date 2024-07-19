package id.application.core.domain.repository

import id.application.core.R
import id.application.core.data.datasource.AppPreferenceDataSource
import id.application.core.data.datasource.ApplicationDataSource
import id.application.core.data.network.model.login.RequestLoginItem
import id.application.core.data.network.model.profile.toProfileResponse
import id.application.core.domain.model.blocks.ItemAllBlocksResponse
import id.application.core.domain.model.blocks.toAllBlockResponse
import id.application.core.domain.model.geotags.ItemAllGeotagingResponse
import id.application.core.domain.model.geotags.toAllGeotagingResponse
import id.application.core.domain.model.login.UserLoginRequest
import id.application.core.domain.model.login.UserLoginResponse
import id.application.core.domain.model.login.toLoginResponse
import id.application.core.domain.model.plants.ItemAllPlantsResponse
import id.application.core.domain.model.plants.toAllPlantsResponse
import id.application.core.domain.model.profile.UserProfileResponse
import id.application.core.utils.AssetWrapperApp
import id.application.core.utils.ResultWrapper
import id.application.core.utils.proceedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface  ApplicationRepository{

    suspend fun userLogin(request: UserLoginRequest): Flow<ResultWrapper<UserLoginResponse>>

    suspend fun userLogout(): Flow<ResultWrapper<Boolean>>

    suspend fun userProfile(): Flow<ResultWrapper<UserProfileResponse>>

    suspend fun getAllGeotaging(
        limitItem:Int? = null,
        pageItem:Int? = null,
    ): ItemAllGeotagingResponse

    suspend fun getAllPlants(
        limitItem:Int? = null,
        pageItem:Int? = null,
    ): Flow<ResultWrapper<ItemAllPlantsResponse>>

    suspend fun getAllBlocks(
        limitItem:Int? = null,
        pageItem:Int? = null,
    ):  ItemAllBlocksResponse
}

class ApplicationRepositoryImpl(
    private val appDataSource: ApplicationDataSource,
    private val appPreferenceDataSource: AppPreferenceDataSource,
    private val assetWrapper : AssetWrapperApp
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

    override suspend fun getAllGeotaging(
        limitItem: Int?,
        pageItem: Int?
    ): ItemAllGeotagingResponse {
        return appDataSource.getAllGeotaging(limitItem, pageItem).toAllGeotagingResponse()
    }

    override suspend fun getAllPlants(
        limitItem: Int?,
        pageItem: Int?
    ): Flow<ResultWrapper<ItemAllPlantsResponse>> {
        return proceedFlow {
            appDataSource.getAllPlants(limitItem, pageItem).toAllPlantsResponse()
        }.catch { e ->
            emit(ResultWrapper.Error(e as? Exception ?: Exception(assetWrapper.getString(R.string.text_unknown_error))))
        }
    }

    override suspend fun getAllBlocks(
        limitItem: Int?,
        pageItem: Int?
    ): ItemAllBlocksResponse {
        return appDataSource.getAllBlocks(limitItem, pageItem).toAllBlockResponse()

    }
}