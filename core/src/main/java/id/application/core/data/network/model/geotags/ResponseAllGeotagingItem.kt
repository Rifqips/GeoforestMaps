package id.application.core.data.network.model.geotags


import androidx.annotation.Keep
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
    @SerializedName("items") val items: List<AllGeotaging> = listOf(),
    @SerializedName("itemsPerPage") val itemsPerPage: Int,
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("totalPages") val totalPages: Int
)

@Keep
data class AllGeotaging(
    @SerializedName("id")
    val id: Int,
    @SerializedName("altitude")
    val altitude: Double,
    @SerializedName("block")
    val block: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("plant")
    val plant: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: Int
)