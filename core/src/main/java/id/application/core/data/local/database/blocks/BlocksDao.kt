package id.application.core.data.local.database.blocks

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.domain.model.remotekeys.RemoteKeys

@Dao
interface BlocksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBlock(blocks: List<ItemAllBlocks>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKeyBlock(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysIdBlock(id:String): RemoteKeys?

    @Query("SELECT * FROM all_blocks")
    fun retrieveAllBlock(): PagingSource<Int, ItemAllBlocks>

    @Query("DELETE FROM all_blocks")
    suspend fun deleteAllBlock()

}