package id.application.core.domain.model.plants

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.application.core.data.network.model.blocks.AllBlocks
import id.application.core.data.network.model.blocks.DataAllBlocks
import id.application.core.data.network.model.plants.AllPlants
import id.application.core.data.network.model.plants.DataAllPlants
import id.application.core.data.network.model.plants.ResponseAllPlantsItem
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.domain.model.blocks.ItemDataAllBlocks

@Keep
data class ItemAllPlantsItem(
    val code: Int?,
    val `data`: ItemDataAllPlants?,
    val message: String?
)

@Keep
data class ItemDataAllPlants(
    val currentItemCount: Int?,
    val items: List<ItemAllPlants?>?,
    val itemsPerPage: Int?,
    val pageIndex: Int?,
    val totalPages: Int?
)

@Keep
data class ItemAllPlants(
    val createdAt: String?,
    val id: Int?,
    val name: String?,
    val updatedAt: String?
)

fun ResponseAllPlantsItem.toAllPlantsResponse() = ItemAllPlantsItem(
    code = this.code,
    data = this.data?.toDataAllPlantsItem(),
    message = this.message
)


fun DataAllPlants.toDataAllPlantsItem() = ItemDataAllPlants(
    currentItemCount = this.currentItemCount,
    items = this.items?.toAllPlantsList(),
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

fun List<AllPlants?>?.toAllPlantsList(): List<ItemAllPlants?>? {
    return this?.map { it?.toAllPlants() }
}