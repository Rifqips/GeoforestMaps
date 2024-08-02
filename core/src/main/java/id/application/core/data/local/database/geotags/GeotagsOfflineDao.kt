package id.application.core.data.local.database.geotags

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.application.core.domain.model.geotags.ItemAllGeotagingOffline
import kotlinx.coroutines.flow.Flow

@Dao
interface GeotagsOfflineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGeotagsOffline(geotagsOffline: ItemAllGeotagingOffline) : Long

    @Query("SELECT * FROM all_geotaging_offline")
    fun getAllGeotagsOffline(): Flow<ItemAllGeotagingOffline>

    @Query("DELETE FROM all_geotaging_offline")
    suspend fun deleteAllGeotagsOffline()

}