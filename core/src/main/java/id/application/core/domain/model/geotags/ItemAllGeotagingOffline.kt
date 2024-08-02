package id.application.core.domain.model.geotags


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import id.application.core.data.network.model.geotags.AllGeotaging

@Entity(tableName = "all_geotaging_offline")
data class ItemAllGeotagingOffline(
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

fun AllGeotaging.toAllGeotagingOffline() = ItemAllGeotagingOffline(
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

fun List<AllGeotaging>.toAllGeotagingOfflineList(): List<ItemAllGeotagingOffline> {
    return this.map { it.toAllGeotagingOffline() }
}

