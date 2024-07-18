package id.application.core.data.network.model.geotags


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep


@Keep
    data class ResponseAllGeotagingItem(
    @SerializedName("code") val code: Int?,
    @SerializedName("data") val `data`: DataAllGeotaging?,
    @SerializedName("message") val message: String?
)

@Keep
data class DataAllGeotaging(
    @SerializedName("currentItemCount") val currentItemCount: Int?,
    @SerializedName("items") val items: List<AllGeotaging?>?,
    @SerializedName("itemsPerPage") val itemsPerPage: Int?,
    @SerializedName("pageIndex") val pageIndex: Int?,
    @SerializedName("totalPages") val totalPages: Int?
)

@Keep
data class AllGeotaging(
    @SerializedName("altitude") val altitude: Int?,
    @SerializedName("block_id") val blockId: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("photo") val photo: String?,
    @SerializedName("plant_id") val plantId: Int?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("user_id") val userId: Int?
)