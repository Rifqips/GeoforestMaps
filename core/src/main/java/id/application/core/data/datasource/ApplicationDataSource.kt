package id.application.core.data.datasource

import id.application.core.data.network.model.blocks.ResponseAllBlocksItem
import id.application.core.data.network.model.geotags.ResponseAllGeotagingItem
import id.application.core.data.network.model.login.RequestLoginItem
import id.application.core.data.network.model.login.ResponseLoginItem
import id.application.core.data.network.model.logout.ResponseLogoutItem
import id.application.core.data.network.model.plants.ResponseAllPlantsItem
import id.application.core.data.network.model.profile.ResponseProfileItem
import id.application.core.data.network.service.ApplicationService
import retrofit2.Response

interface ApplicationDataSource {
    suspend fun userLogin(userLoginRequest: RequestLoginItem): ResponseLoginItem
    suspend fun userLogout(): Response<ResponseLogoutItem>
    suspend fun userProfile(): ResponseProfileItem
    suspend fun getAllGeotaging(
        limitItem:Int? = null,
        pageItem:Int? = null,
    ): ResponseAllGeotagingItem
    suspend fun getAllPlants(
        limitItem:Int? = null,
        pageItem:Int? = null,
    ): ResponseAllPlantsItem
    suspend fun getAllBlocks(
        limitItem:Int? = null,
        pageItem:Int? = null,
    ): ResponseAllBlocksItem

}

class ApplicationDataSourceImpl(private val service: ApplicationService) : ApplicationDataSource{
    override suspend fun userLogin(userLoginRequest: RequestLoginItem): ResponseLoginItem {
        return service.userLogin(userLoginRequest)
    }

    override suspend fun userLogout(): Response<ResponseLogoutItem> {
        return service.userLogout()
    }

    override suspend fun userProfile(): ResponseProfileItem {
        return service.userProfile()
    }

    override suspend fun getAllGeotaging(
        limitItem: Int?,
        pageItem: Int?
    ): ResponseAllGeotagingItem {
        return service.getAllGeotaging(limitItem, pageItem)
    }

    override suspend fun getAllPlants(limitItem: Int?, pageItem: Int?): ResponseAllPlantsItem {
        return service.getAllPlants(limitItem, pageItem)
    }

    override suspend fun getAllBlocks(limitItem: Int?, pageItem: Int?): ResponseAllBlocksItem {
        return service.getAllBlocks(limitItem, pageItem)
    }

}
