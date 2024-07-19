package id.application.core.domain.model.plants

import androidx.annotation.Keep
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

@Keep
data class ItemAllPlants(
    val createdAt: String,
    val id: Int,
    val name: String,
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