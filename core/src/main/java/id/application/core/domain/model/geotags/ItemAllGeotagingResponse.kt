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

data class ItemAllGeotaging(
    val id: Int,
    val altitude: Double,
    val block: String,
    val createdAt: String,
    val latitude: Double,
    val longitude: Double,
    val photo: String,
    val plant: String,
    val updatedAt: String,
    val user: String
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
    block = this.block,
    createdAt = this.createdAt,
    id = this.id,
    latitude = this.latitude,
    longitude = this.longitude,
    photo = this.photo,
    plant = this.plant,
    updatedAt = this.updatedAt,
    user = this.user
)

fun List<AllGeotaging>.toAllGeotagingList(): List<ItemAllGeotaging> {
    return this.map { it.toAllGeotaging() }
}
