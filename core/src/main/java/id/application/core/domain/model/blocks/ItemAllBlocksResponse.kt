package id.application.core.domain.model.blocks

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

@Entity(tableName = "all_blocks")
data class ItemAllBlocks(
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