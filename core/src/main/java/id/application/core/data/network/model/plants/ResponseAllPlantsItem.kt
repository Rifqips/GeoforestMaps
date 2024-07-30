package id.application.core.data.network.model.plants

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResponseAllPlantsItem(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val `data`: DataAllPlants,
    @SerializedName("message") val message: String
)


@Keep
data class DataAllPlants(
    @SerializedName("currentItemCount") val currentItemCount: Int,
    @SerializedName("items") val items: List<AllPlants>,
    @SerializedName("itemsPerPage") val itemsPerPage: Int,
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("totalPages") val totalPages: Int
)

@Keep
data class AllPlants(
    @SerializedName("id")
    val id: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("updated_at")
    val updatedAt: String
)