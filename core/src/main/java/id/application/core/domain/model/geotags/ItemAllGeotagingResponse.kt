package id.application.core.domain.model.geotags


import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
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

@Entity(tableName = "all_geotaging")
data class ItemAllGeotaging(
    @field:PrimaryKey
    @field:ColumnInfo(name = "id")
    val id: Int,
    @field:ColumnInfo(name = "altitude")
    val altitude: Double,
    @field:ColumnInfo(name = "block")
    val block: String,
    @field:ColumnInfo(name = "created_at")
    val createdAt: String,
    @field:ColumnInfo(name = "latitude")
    val latitude: Double,
    @field:ColumnInfo(name = "longitude")
    val longitude: Double,
    @field:ColumnInfo(name = "photo")
    val photo: String,
    @field:ColumnInfo(name = "plant")
    val plant: String,
    @field:ColumnInfo(name = "updated_at")
    val updatedAt: String,
    @field:ColumnInfo(name = "user_id")
    @SerializedName("user_id")
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
    block = this.block,
    createdAt = this.createdAt,
    id = this.id,
    latitude = this.latitude,
    longitude = this.longitude,
    photo = this.photo,
    plant = this.plant,
    updatedAt = this.updatedAt,
    userId = this.userId
)

fun List<AllGeotaging>.toAllGeotagingList(): List<ItemAllGeotaging> {
    return this.map { it.toAllGeotaging() }
}
