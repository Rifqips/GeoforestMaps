package id.application.core.domain.model.blocks

import androidx.annotation.Keep
import id.application.core.data.network.model.blocks.AllBlocks
import id.application.core.data.network.model.blocks.DataAllBlocks
import id.application.core.data.network.model.blocks.ResponseAllBlocksItem

@Keep
data class ItemAllBlocksResponse(
    val code: Int,
    val `data`: ItemDataAllBlocks,
    val message: String
)

@Keep
data class ItemDataAllBlocks(
    val currentItemCount: Int,
    val items: List<ItemAllBlocks>,
    val itemsPerPage: Int,
    val pageIndex: Int,
    val totalPages: Int
)

@Keep
data class ItemAllBlocks(
    val createdAt: String?,
    val id: Int,
    val name: String?,
    val updatedAt: String?
)


fun ResponseAllBlocksItem.toAllBlockResponse() = ItemAllBlocksResponse(
    code = this.code,
    data = this.data.toDataAllBlocksItem(),
    message = this.message
)

fun DataAllBlocks.toDataAllBlocksItem() = ItemDataAllBlocks(
    currentItemCount = this.currentItemCount,
    items = this.items.toAllBlocksList(),
    itemsPerPage = this.itemsPerPage,
    pageIndex = this.pageIndex,
    totalPages = this.totalPages
)

fun AllBlocks.toAllBlocks() = ItemAllBlocks(
    createdAt = this.createdAt,
    id = this.id,
    name = this.name,
    updatedAt = this.updatedAt
)

fun List<AllBlocks>.toAllBlocksList(): List<ItemAllBlocks> {
    return this.map { it.toAllBlocks() }
}