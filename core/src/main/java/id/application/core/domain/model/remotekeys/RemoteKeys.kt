package id.application.core.domain.model.remotekeys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo("prevKey")
    val prevKey: Int?,
    @ColumnInfo("nextKey")
    val nextKey: Int?
)