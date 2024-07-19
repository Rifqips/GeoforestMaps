package id.application.core.domain.model.geotags


import androidx.annotation.Keep
import id.application.core.data.network.model.geotags.AllGeotaging
import id.application.core.data.network.model.geotags.DataAllGeotaging
import id.application.core.data.network.model.geotags.ResponseAllGeotagingItem

@Keep
data class ItemAllGeotagingResponse(
    val code: Int,
    val `data`: ItemDataAllGeotaging,
    val message: String
)

@Keep
data class ItemDataAllGeotaging(
    val currentItemCount: Int,
    val items: List<ItemAllGeotaging>,
    val itemsPerPage: Int,
    val pageIndex: Int,
    val totalPages: Int
)

@Keep
data class ItemAllGeotaging(
    val altitude: Int,
    val blockId: Int,
    val createdAt: String,
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val photo: String,
    val plantId: Int,
    val updatedAt: String,
    val userId: Int
)



fun ResponseAllGeotagingItem.toAllGeotagingResponse() = ItemAllGeotagingResponse(
    code = this.code,
    data = this.data.toDataAllGeotagingItem(),
    message = this.message
)

fun DataAllGeotaging.toDataAllGeotagingItem() = ItemDataAllGeotaging(
    currentItemCount = this.currentItemCount,
    items = this.items.toAllGeotagingList(),
    itemsPerPage = this.itemsPerPage,
    pageIndex = this.pageIndex,
    totalPages = this.totalPages
)

fun AllGeotaging.toAllGeotaging() = ItemAllGeotaging(
    altitude = this.altitude,
    blockId = this.blockId,
    createdAt = this.createdAt,
    id = this.id,
    latitude = this.latitude,
    longitude = this.longitude,
    photo = this.photo,
    plantId = this.plantId,
    updatedAt = this.updatedAt,
    userId = this.userId
)

fun List<AllGeotaging>.toAllGeotagingList(): List<ItemAllGeotaging> {
    return this.map { it.toAllGeotaging() }
}


