package id.application.core.data.datasource

import id.application.core.data.network.model.blocks.ResponseAllBlocksItem
import id.application.core.data.network.model.geotags.ResponseAllGeotagingItem
import id.application.core.data.network.model.login.RequestLoginItem
import id.application.core.data.network.model.login.ResponseLoginItem
import id.application.core.data.network.model.logout.ResponseLogoutItem
import id.application.core.data.network.model.plants.ResponseAllPlantsItem
import id.application.core.data.network.model.profile.ResponseProfileItem
import id.application.core.data.network.service.ApplicationService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part

interface ApplicationDataSource {
    suspend fun userLogin(userLoginRequest: RequestLoginItem): ResponseLoginItem
    suspend fun userLogout(): Response<ResponseLogoutItem>
    suspend fun userProfile(): ResponseProfileItem
    suspend fun getAllGeotaging(
        blockId:Int? = null,
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

    suspend fun createGeotaging(
        plantId: RequestBody?,
        blockId: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?,
        altitude: RequestBody?,
        userImage: MultipartBody.Part?
    ) : ResponseAllGeotagingItem

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
        blockId: Int?,
        limitItem: Int?,
        pageItem: Int?
    ): ResponseAllGeotagingItem {
        return service.getAllGeotaging(blockId,limitItem, pageItem)
    }

    override suspend fun getAllPlants(limitItem: Int?, pageItem: Int?): ResponseAllPlantsItem {
        return service.getAllPlants(limitItem, pageItem)
    }

    override suspend fun getAllBlocks(limitItem: Int?, pageItem: Int?): ResponseAllBlocksItem {
        return service.getAllBlocks(limitItem, pageItem)
    }

    override suspend fun createGeotaging(
        plantId: RequestBody?,
        blockId: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?,
        altitude: RequestBody?,
        userImage: MultipartBody.Part?
    ): ResponseAllGeotagingItem {
        return service.createGeotaging( plantId, blockId, latitude, longitude, altitude, userImage)
    }

}
