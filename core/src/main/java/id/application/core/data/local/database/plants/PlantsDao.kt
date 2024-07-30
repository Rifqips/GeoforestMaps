package id.application.core.data.local.database.plants

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import id.application.core.data.network.model.blocks.AllBlocks
import id.application.core.data.network.model.plants.AllPlants

@Dao
interface PlantsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllplants(blocks: List<AllPlants>)

}