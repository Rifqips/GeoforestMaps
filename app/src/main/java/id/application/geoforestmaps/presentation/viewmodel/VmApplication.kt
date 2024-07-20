package id.application.geoforestmaps.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import id.application.core.data.datasource.AppPreferenceDataSource
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
            }
        }
    }

}