package id.application.core.data.network.model.blocks


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResponseAllBlocksItem(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val `data`: DataAllBlocks,
    @SerializedName("message") val message: String
)

@Keep
data class DataAllBlocks(
    @SerializedName("currentItemCount") val currentItemCount: Int,
    @SerializedName("items") val items: List<AllBlocks>,
    @SerializedName("itemsPerPage") val itemsPerPage: Int,
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("totalPages") val totalPages: Int
)

@Keep
data class AllBlocks(
    @SerializedName("id")
    val id: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("updated_at")
    val updatedAt: String
)