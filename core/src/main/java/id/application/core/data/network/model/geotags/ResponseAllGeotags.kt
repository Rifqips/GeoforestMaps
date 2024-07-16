package id.application.core.data.network.model.geotags

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ResponseAllGeotags(
    @SerializedName("data")
    val `data`: List<DataAllGeotags>,
    @SerializedName("links")
    val links: LinksAllGeotags,
    @SerializedName("meta")
    val meta: MetaAllGeotags
)

@Keep
data class DataAllGeotags(
    @SerializedName("altitude")
    val altitude: Int,
    @SerializedName("block_id")
    val blockId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("plant_id")
    val plantId: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: Int
)

@Keep
data class LinksAllGeotags(
    @SerializedName("first")
    val first: String,
    @SerializedName("last")
    val last: String,
    @SerializedName("next")
    val next: Any,
    @SerializedName("prev")
    val prev: Any
)

@Keep
data class MetaAllGeotags(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("from")
    val from: Int,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("links")
    val links: List<LinkDataAllGeotags>,
    @SerializedName("path")
    val path: String,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("to")
    val to: Int,
    @SerializedName("total")
    val total: Int
)

@Keep
data class LinkDataAllGeotags(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("label")
    val label: String,
    @SerializedName("url")
    val url: String
)