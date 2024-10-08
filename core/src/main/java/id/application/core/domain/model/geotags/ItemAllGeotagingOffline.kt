package id.application.core.domain.model.geotags


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "all_geotaging_offline")
data class ItemAllGeotagingOffline(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "plant_name")
    val plant: String,

    @ColumnInfo(name = "user")
    val user: String,

    @ColumnInfo(name = "plant_id")
    val plantId: String,

    @ColumnInfo(name = "block_name")
    val block: String,

    @ColumnInfo(name = "block_id")
    val blockId: String,

    @ColumnInfo(name = "latitude")
    val latitude: String,

    @ColumnInfo(name = "longitude")
    val longitude: String,

    @ColumnInfo(name = "altitude")
    val altitude: String,

    @ColumnInfo(name = "base64")
    val base64: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
