package id.application.geoforestmaps.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.application.core.data.datasource.AppPreferenceDataSource
import id.application.core.domain.model.login.UserLoginRequest
import id.application.core.domain.model.login.UserLoginResponse
import id.application.core.domain.repository.ApplicationRepository
import id.application.core.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.log

class VmApplication(

    private val repo : ApplicationRepository,
    private val appPreferenceDataSource: AppPreferenceDataSource
) : ViewModel() {

    private val _loginResult = MutableLiveData<ResultWrapper<UserLoginResponse>>()
    val loginResult: LiveData<ResultWrapper<UserLoginResponse>>
        get() = _loginResult

    private val _isUserLogin = MutableLiveData<Boolean>()
    val isUserLogin: LiveData<Boolean>
        get() = _isUserLogin

    private val _logoutResults = MutableLiveData<ResultWrapper<Boolean>>()
    val logoutResults: LiveData<ResultWrapper<Boolean>>
        get() = _logoutResults

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
}