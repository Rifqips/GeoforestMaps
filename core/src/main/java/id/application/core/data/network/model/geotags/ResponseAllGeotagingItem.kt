package id.application.core.data.network.model.geotags


import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Keep
    data class ResponseAllGeotagingItem(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val `data`: DataAllGeotaging,
    @SerializedName("message") val message: String
)

@Keep
data class DataAllGeotaging(
    @SerializedName("currentItemCount") val currentItemCount: Int,
    @SerializedName("items") val items: List<AllGeotaging>,
    @SerializedName("itemsPerPage") val itemsPerPage: Int,
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("totalPages") val totalPages: Int
)

@Keep
@Entity(tableName = "all_geotaging")
data class AllGeotaging(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: Int,

    @ColumnInfo(name = "altitude")
    @SerializedName("altitude")
    val altitude: Double,

    @ColumnInfo(name = "block")
    @SerializedName("block")
    val block: String,

    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    val createdAt: String,

    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    val longitude: Double,

    @ColumnInfo(name = "photo")
    @SerializedName("photo")
    val photo: String,

    @ColumnInfo(name = "plant")
    @SerializedName("plant")
    val plant: String,

    @ColumnInfo(name = "updated_at")
    @SerializedName("updated_at")
    val updatedAt: String,

    @ColumnInfo(name = "user_id")
    @SerializedName("user_id")
    val userId: Int
)