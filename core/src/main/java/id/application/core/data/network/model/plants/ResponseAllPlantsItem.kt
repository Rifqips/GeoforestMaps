package id.application.core.data.network.model.plants


import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
@Entity(tableName = "all_plants")
data class AllPlants(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: Int,

    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    val createdAt: String,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "updated_at")
    @SerializedName("updated_at")
    val updatedAt: String
)