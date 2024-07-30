package id.application.core.data.network.model.blocks


import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
@Entity(tableName = "all_blocks")
data class AllBlocks(
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