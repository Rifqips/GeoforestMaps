package id.application.geoforestmaps.presentation.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import id.application.core.data.datasource.AppPreferenceDataSource
import id.application.core.data.network.service.ApplicationService
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.core.domain.model.login.UserLoginRequest
import id.application.core.domain.model.login.UserLoginResponse
import id.application.core.domain.model.plants.ItemAllPlantsResponse
import id.application.core.domain.model.profile.UserProfileResponse
import id.application.core.domain.paging.BlocksPagingSource
import id.application.core.domain.paging.GeotagingPagingSource
import id.application.core.domain.repository.ApplicationRepository
import id.application.core.utils.ResultWrapper
import id.application.geoforestmaps.presentation.adapter.blocks.DatabaseAdapterItem
import id.application.geoforestmaps.presentation.adapter.geotags.GeotaggingAdapterItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException

class VmApplication(
    private val repo: ApplicationRepository,
    private val appPreferenceDataSource: AppPreferenceDataSource
) : ViewModel() {

    private val _loginResult = MutableLiveData<ResultWrapper<UserLoginResponse>>()
    val loginResult: LiveData<ResultWrapper<UserLoginResponse>> = _loginResult

    private val _isUserLogin = MutableLiveData<Boolean>()
    val isUserLogin: LiveData<Boolean> = _isUserLogin

    private val _logoutResults = MutableLiveData<ResultWrapper<Boolean>>()
    val logoutResults: LiveData<ResultWrapper<Boolean>> = _logoutResults

    private val _userProfileResult = MutableLiveData<ResultWrapper<UserProfileResponse>>()
    val userProfileResult: LiveData<ResultWrapper<UserProfileResponse>> = _userProfileResult

    private val _plantsResult = MutableLiveData<ResultWrapper<ItemAllPlantsResponse>>()
    val plantsResult: LiveData<ResultWrapper<ItemAllPlantsResponse>> = _plantsResult

    private val _geotagingCreateResult = MutableLiveData<ResultWrapper<List<ItemAllGeotaging>>>()
    val geotagingCreateResult: LiveData<ResultWrapper<List<ItemAllGeotaging>>>
        get() = _geotagingCreateResult

    private val _isLoadingGeotaging = MutableLiveData<Boolean>()
    val isLoadingGeotaging: LiveData<Boolean>
        get() = _isLoadingGeotaging


    private val _exportResult = MutableLiveData<Boolean>()
    val exportResult: LiveData<Boolean> get() = _exportResult


    fun userLogin(request: UserLoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.userLogin(request).collect {
                _loginResult.postValue(it)
            }
        }
    }

    fun checkLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            val userStatus = appPreferenceDataSource.getUserToken().firstOrNull()
            _isUserLogin.postValue(userStatus != null)
        }
    }

    fun userLogout() {
        viewModelScope.launch {
            _logoutResults.value = repo.userLogout().first()

        }
    }

    fun userProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.userProfile().collect {
                _userProfileResult.postValue(it)
            }
        }
    }

    fun loadPagingBlocks(
        adapter: DatabaseAdapterItem,
        limitItem: Int?  = null,
        pageItem: Int?  = null
    ) {
        viewModelScope.launch {
            val response = repo.getAllBlocks(
                limitItem = limitItem,
                pageItem = pageItem
            )
            if (response.code == 200) {
                val postsResponse = response.data
                postsResponse.let {
                    val store = it.items
                    adapter.submitData(PagingData.from(store))
                }
            }
        }
    }

    val blockList = Pager(PagingConfig(pageSize = 4)) {
        BlocksPagingSource(repo)
    }.liveData.cachedIn(viewModelScope)


    fun loadPagingGeotagging(
        adapter: GeotaggingAdapterItem,
        blockId: Int? = null,
        limitItem: Int?  = null,
        pageItem: Int?  = null
    ) {
        viewModelScope.launch {
            val response =  repo.getAllGeotaging(
                blockId = blockId,
                limitItem = limitItem,
                pageItem = pageItem
            )
            if (response.code == 200) {
                val postsResponse = response.data
                postsResponse.let {
                    val store = it.items
                    adapter.submitData(PagingData.from(store))
                }
            }
        }
    }

    val geotaggingList = Pager(PagingConfig(pageSize = 4)) {
        GeotagingPagingSource(repo)
    }.liveData.cachedIn(viewModelScope)

    fun getPlant() {
        viewModelScope.launch {
            repo.getAllPlants().collect {
                _plantsResult.postValue(it)
            }
        }
    }

    fun createGeotaging(
        plantId: RequestBody?,
        blockId: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?,
        altitude: RequestBody?,
        userImage: MultipartBody.Part?
    ) {
        _isLoadingGeotaging.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            repo.createGeotaging(
                plantId,
                blockId,
                latitude,
                longitude,
                altitude,
                userImage
            ).collect {
                _geotagingCreateResult.postValue(it)
                _isLoadingGeotaging.postValue(false)
            }
        }
    }

    fun exportFile(type: String, blockId: Int?, file: File) {
        viewModelScope.launch {
            _exportResult.postValue(true)
            try {
                val response = repo.exportFile(type, blockId)
                if (response.isSuccessful) {
                    // Save the response body to the file
                    response.body()?.let { responseBody ->
                        saveResponseToFile(responseBody, file)
                        Log.d("File-Path", responseBody.toString())
                        _exportResult.postValue(false)
                    } ?: run {
                        _exportResult.postValue(false)
                    }
                } else {
                    _exportResult.postValue(false)

                }
            } catch (e: Exception) {
                _exportResult.postValue(false)
            }
        }
    }


    private fun saveResponseToFile(responseBody: ResponseBody, file: File) {
        try {
            file.outputStream().use { outputStream ->
                responseBody.byteStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}