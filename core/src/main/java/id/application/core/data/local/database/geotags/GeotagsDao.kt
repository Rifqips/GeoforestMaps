package id.application.core.data.local.database.geotags

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.application.core.data.network.model.blocks.AllBlocks
import id.application.core.data.network.model.geotags.AllGeotaging
import id.application.core.domain.model.remotekeys.RemoteKeys

@Dao
interface GeotagsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGeotags(blocks: List<AllGeotaging>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKey(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id:String): RemoteKeys?

    @Query("SELECT * FROM all_geotaging")
    fun retrieveAllGeotags(): PagingSource<Int, AllGeotaging>

    @Query("DELETE FROM all_geotaging")
    suspend fun deleteAll()

}