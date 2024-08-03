package id.application.geoforestmaps.presentation.viewmodel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import id.application.core.data.datasource.AppPreferenceDataSource
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.core.domain.model.geotags.ItemAllGeotagingOffline
import id.application.core.domain.model.login.UserLoginRequest
import id.application.core.domain.model.login.UserLoginResponse
import id.application.core.domain.model.plants.ItemAllPlants
import id.application.core.domain.model.plants.ItemAllPlantsResponse
import id.application.core.domain.paging.GeotagingAllPagingSource
import id.application.core.domain.repository.ApplicationRepository
import id.application.core.utils.ResultWrapper
import id.application.core.utils.Utils.saveFile
import id.application.geoforestmaps.presentation.adapter.databasegallery.DatabaseGalleryAdapterItem
import id.application.geoforestmaps.presentation.adapter.databaselist.DatabaseListAdapterItem
import id.application.geoforestmaps.utils.Constant.generateUniqueFileName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class VmApplication(
    private val repo: ApplicationRepository,
    private val appPreferenceDataSource: AppPreferenceDataSource
) : ViewModel() {

    private val _loginResult = MutableLiveData<ResultWrapper<UserLoginResponse>>()
    val loginResult: LiveData<ResultWrapper<UserLoginResponse>> = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _state = MutableLiveData<Result<Unit>>()
    val state: LiveData<Result<Unit>> get() = _state

    private val _geotagingLocalResult = MutableLiveData<PagingData<ItemAllGeotaging>>()
    val geotagingLocalResult: LiveData<PagingData<ItemAllGeotaging>> = _geotagingLocalResult

    private val _blockLocalResult = MutableLiveData<PagingData<ItemAllBlocks>>()
    val blockLocalResult: LiveData<PagingData<ItemAllBlocks>> = _blockLocalResult

    private val _plantsLiveData = MutableLiveData<List<ItemAllPlants>>()
    val plantsLiveData: LiveData<List<ItemAllPlants>> = _plantsLiveData

    private val _isUserLogin = MutableLiveData<Boolean>()
    val isUserLogin: LiveData<Boolean> = _isUserLogin

    private val _isUserName = MutableLiveData<String>()
    val isUserName: LiveData<String> = _isUserName

    private val _isBlockName = MutableLiveData<String>()
    val isBlockName: LiveData<String> = _isBlockName

    private val _isUserEmail = MutableLiveData<String>()
    val isUserEmail: LiveData<String> = _isUserEmail

    private val _logoutResults = MutableLiveData<ResultWrapper<Boolean>>()
    val logoutResults: LiveData<ResultWrapper<Boolean>> = _logoutResults


    private val _loadingPagingResults = MutableLiveData<Boolean>()
    val loadingPagingResults: LiveData<Boolean> = _loadingPagingResults

    private val _plantsResult = MutableLiveData<ResultWrapper<ItemAllPlantsResponse>>()
    val plantsResult: LiveData<ResultWrapper<ItemAllPlantsResponse>> = _plantsResult

    private val _geotagingCreateResult = MutableLiveData<ResultWrapper<List<ItemAllGeotaging>>>()
    val geotagingCreateResult: LiveData<ResultWrapper<List<ItemAllGeotaging>>> =
        _geotagingCreateResult

    private val _downloadStatus = MutableLiveData<String>()
    val downloadStatus: LiveData<String> get() = _downloadStatus

    val geotagingListOffline = repo.getAllGeotagingOffline().asLiveData(Dispatchers.IO)

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

    fun deleteBlockName(onComplete: () -> Unit) {
        viewModelScope.launch {
            appPreferenceDataSource.deleteBlockName()
            onComplete()
        }
    }
    fun getUserName() {
        viewModelScope.launch {
            val userName = appPreferenceDataSource.getUserName()
            _isUserName.postValue(userName)
        }
    }

    fun saveBlockName(onComplete: () -> Unit, blockName: String) {
        viewModelScope.launch {
            appPreferenceDataSource.saveBlockName(blockName)
            onComplete()
        }
    }
    fun getBlockName(){
        viewModelScope.launch {
            val blockName = appPreferenceDataSource.getBlockName()
            _isBlockName.postValue(blockName)
        }
    }

    fun getUserEmail() {
        viewModelScope.launch {
            val userEmail = appPreferenceDataSource.getUserEmail()
            _isUserEmail.postValue(userEmail)
        }
    }


    fun userLogout() {
        viewModelScope.launch {
            _logoutResults.value = repo.userLogout().first()
        }
    }

    fun fetchPlants() {
        viewModelScope.launch {
            try {
                val plants = repo.retrieveAllPlants()
                _plantsLiveData.postValue(plants)
            } catch (e: Exception) {
                _plantsLiveData.postValue(emptyList())
            }
        }
    }

    fun loadPagingGeotagging(
        adapter: DatabaseListAdapterItem,
        block: String? = null,
        limitItem: Int? = null,
        pageItem: Int? = null
    ) {
        _loadingPagingResults.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repo.getAllGeotaging(
                block = block,
                limitItem = limitItem,
                pageItem = pageItem
            )
            if (response.code == 200) {
                val postsResponse = response.data
                postsResponse.let {
                    val store = it.items
                    adapter.submitData(PagingData.from(store))
                }
                _loadingPagingResults.postValue(false)
            }
        }
    }

    fun loadPagingGeotaggingGallery(
        adapter: DatabaseGalleryAdapterItem,
        block: String? = null,
        createdBy: String? = null,
        limitItem: Int? = null,
        pageItem: Int? = null
    ) {
        _loadingPagingResults.postValue(true)
        viewModelScope.launch {
            val response = repo.getAllGeotaging(
                block = block,
                createdBy = createdBy,
                limitItem = limitItem,
                pageItem = pageItem
            )
            if (response.code == 200) {
                val postsResponse = response.data
                postsResponse.let {
                    val store = it.items
                    adapter.submitData(PagingData.from(store))
                }
                _loadingPagingResults.postValue(false)
            }
        }
    }

    val geotaggingListAll = Pager(PagingConfig(pageSize = 4)) {
        GeotagingAllPagingSource(repo)
    }.liveData.cachedIn(viewModelScope)

    fun fetchAllGeotagingLocal() {
        viewModelScope.launch {
            repo.fetchAllGeotagingLocal().collect {
                _geotagingLocalResult.postValue(it)
            }
        }
    }

    fun fetchAllBlockLocal() {
        viewModelScope.launch {
            repo.fetchAllBlockLocal().collect {
                _blockLocalResult.postValue(it)
            }
        }
    }


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
        userImage: MultipartBody.Part? = null,
        photoBase64: RequestBody? = null
    ) {
        viewModelScope.launch {
            val result = repo.createGeotaging(
                plantId, blockId, latitude, longitude, altitude, userImage, photoBase64
            )
            _state.value = result
        }
    }

    fun createGeotaggingOflline(item : ItemAllGeotagingOffline){
        viewModelScope.launch {
            repo.insertAllGeotagsOffline(item)
        }
    }
    fun deleteGeotaggingById(geotaggingId: Int){
        viewModelScope.launch {
            repo.deleteGeotaggingById(geotaggingId)
        }
    }



    fun eksports(
        type: String?,
        block: String?,
        geoatagId: Int? = null,
        fileName: String,
        context: Context,
        isZip: Boolean = false
    ) {
        viewModelScope.launch {
            _downloadStatus.value = "Mendownload File..."
            try {
                val response = repo.exportFile(type, block, geoatagId)
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val downloadDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        if (!downloadDir.exists()) {
                            downloadDir.mkdirs()
                        }
                        val uniqueFileName = generateUniqueFileName(fileName)
                        val filePath = File(downloadDir, uniqueFileName).absolutePath

                        withContext(Dispatchers.IO) {
                            saveFile(responseBody, filePath)
                        }
                        _downloadStatus.value = "Download berhasil!"
                    } ?: run {
                        _downloadStatus.value = "Download gagal!"
                    }
                } else {
                    _downloadStatus.value = "Download gagal!"
                }
            } catch (e: IOException) {
                _downloadStatus.value = "Download gagal!"
            } catch (e: Exception) {
                _downloadStatus.value = "Download gagal!"
            }
        }
    }
}
