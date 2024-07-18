package id.application.core.data.network.model.plants


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ResponseAllPlantsItem(
    @SerializedName("code") val code: Int?, // 200
    @SerializedName("data") val `data`: DataAllPlants?,
    @SerializedName("message") val message: String? // Success
)


@Keep
data class DataAllPlants(
    @SerializedName("currentItemCount") val currentItemCount: Int?, // 60
    @SerializedName("items") val items: List<AllPlants?>?,
    @SerializedName("itemsPerPage") val itemsPerPage: Int?, // 15
    @SerializedName("pageIndex") val pageIndex: Int?, // 1
    @SerializedName("totalPages") val totalPages: Int? // 4
)

@Keep
data class AllPlants(
    @SerializedName("created_at") val createdAt: String?, // 2024-07-15T15:00:36.000000Z
    @SerializedName("id") val id: Int?, // 1
    @SerializedName("name") val name: String?, // ex
    @SerializedName("updated_at") val updatedAt: String? // 2024-07-15T15:00:36.000000Z
)