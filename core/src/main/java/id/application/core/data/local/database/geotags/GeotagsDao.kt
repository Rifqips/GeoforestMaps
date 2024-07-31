package id.application.core.data.local.database.geotags

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.core.domain.model.remotekeys.RemoteKeys

@Dao
interface GeotagsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGeotags(geotags: List<ItemAllGeotaging>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKeyGeotags(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysIdGeotags(id:String): RemoteKeys?

    @Query("SELECT * FROM all_geotaging ORDER BY created_at DESC")
    fun retrieveAllGeotags(): PagingSource<Int, ItemAllGeotaging>

    @Query("DELETE FROM all_geotaging")
    suspend fun deleteAllGeotags()

}