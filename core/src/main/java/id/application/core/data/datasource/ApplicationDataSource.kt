package id.application.core.data.datasource

import id.application.core.data.network.model.login.RequestLoginItem
import id.application.core.data.network.model.login.ResponseLoginItem
import id.application.core.data.network.model.logout.ResponseLogoutItem
import id.application.core.data.network.service.ApplicationService
import retrofit2.Response

interface ApplicationDataSource {
    suspend fun userLogin(userLoginRequest: RequestLoginItem): ResponseLoginItem
    suspend fun userLogout(): Response<ResponseLogoutItem>
}

class ApplicationDataSourceImpl(private val service: ApplicationService) : ApplicationDataSource{
    override suspend fun userLogin(userLoginRequest: RequestLoginItem): ResponseLoginItem {
        return service.userLogin(userLoginRequest)
    }

    override suspend fun userLogout(): Response<ResponseLogoutItem> {
        return service.userLogout()
    }

}
