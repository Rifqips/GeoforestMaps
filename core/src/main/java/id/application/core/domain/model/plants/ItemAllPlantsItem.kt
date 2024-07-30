package id.application.core.domain.model.plants

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import id.application.core.data.network.model.plants.AllPlants
import id.application.core.data.network.model.plants.DataAllPlants
import id.application.core.data.network.model.plants.ResponseAllPlantsItem

@Keep
data class ItemAllPlantsResponse(
    val code: Int,
    val `data`: ItemDataAllPlants,
    val message: String
)

@Keep
data class ItemDataAllPlants(
    val currentItemCount: Int,
    val items: List<ItemAllPlants>,
    val itemsPerPage: Int,
    val pageIndex: Int,
    val totalPages: Int
)

@Entity(tableName = "all_plants")
data class ItemAllPlants(
    @field:PrimaryKey
    @field:ColumnInfo(name = "id")
    val id: Int,
    @field:ColumnInfo(name = "created_at")
    val createdAt: String,
    @field:ColumnInfo(name = "name")
    val name: String,
    @field:ColumnInfo(name = "updated_at")
    val updatedAt: String
)

fun ResponseAllPlantsItem.toAllPlantsResponse() = ItemAllPlantsResponse(
    code = this.code,
    data = this.data.toDataAllPlantsItem(),
    message = this.message
)


fun DataAllPlants.toDataAllPlantsItem() = ItemDataAllPlants(
    currentItemCount = this.currentItemCount,
    items = this.items.toAllPlantsList(),
    itemsPerPage = this.itemsPerPage,
    pageIndex = this.pageIndex,
    totalPages = this.totalPages
)

fun AllPlants.toAllPlants() = ItemAllPlants(
    createdAt = this.createdAt,
    id = this.id,
    name = this.name,
    updatedAt = this.updatedAt
)

fun List<AllPlants>.toAllPlantsList(): List<ItemAllPlants> {
    return this.map { it.toAllPlants() }
}