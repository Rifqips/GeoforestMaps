package id.application.core.domain.repository

import id.application.core.R
import id.application.core.data.datasource.AppPreferenceDataSource
import id.application.core.data.datasource.ApplicationDataSource
import id.application.core.data.network.model.login.RequestLoginItem
import id.application.core.data.network.model.profile.toProfileResponse
import id.application.core.domain.model.login.UserLoginRequest
import id.application.core.domain.model.login.UserLoginResponse
import id.application.core.domain.model.login.toLoginResponse
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
}