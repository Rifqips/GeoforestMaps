package id.application.core.data.local.database.blocks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import id.application.core.data.network.model.blocks.AllBlocks

@Dao
interface BlocksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllblocks(blocks: List<AllBlocks>)

}