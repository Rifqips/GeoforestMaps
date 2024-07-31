package id.application.core.data.local.database.geotags

import androidx.paging.PagingSource
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.core.domain.model.geotags.ItemAllGeotagingOffline
import id.application.core.domain.model.remotekeys.RemoteKeys
import kotlinx.coroutines.flow.Flow

interface GeotagsOfflineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGeotagsOffline(geotagsOffline: ItemAllGeotagingOffline) : Long

    @Query("SELECT * FROM all_geotaging_offline")
    fun getAllGeotagsOffline(): Flow<ItemAllGeotagingOffline>

    @Query("DELETE FROM all_geotaging_offline")
    suspend fun deleteAllGeotagsOffline()

}