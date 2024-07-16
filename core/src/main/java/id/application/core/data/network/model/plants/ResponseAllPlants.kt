package id.application.core.data.network.model.plants

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ResponseAllPlants(
    @SerializedName("data")
    val `data`: List<DataAllPlants>,
    @SerializedName("links")
    val links: LinksAllPlants,
    @SerializedName("meta")
    val meta: MetaAllPlants
)

@Keep
data class DataAllPlants(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("updated_at")
    val updatedAt: String
)


@Keep
data class LinksAllPlants(
    @SerializedName("first")
    val first: String,
    @SerializedName("last")
    val last: String,
    @SerializedName("next")
    val next: String,
    @SerializedName("prev")
    val prev: Any
)

@Keep
data class MetaAllPlants(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("from")
    val from: Int,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("links")
    val links: List<LinkAllPlants>,
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
data class LinkAllPlants(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("label")
    val label: String,
    @SerializedName("url")
    val url: String
)