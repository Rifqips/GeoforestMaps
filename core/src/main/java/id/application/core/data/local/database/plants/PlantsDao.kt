package id.application.core.data.local.database.plants

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.application.core.domain.model.plants.ItemAllPlants
import id.application.core.domain.model.remotekeys.RemoteKeys

@Dao
interface PlantsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllplants(plants: List<ItemAllPlants>)

    @Query("SELECT * FROM all_plants")
    suspend fun retrieveAllPlants(): List<ItemAllPlants>

    @Query("DELETE FROM all_plants")
    suspend fun deleteAllPlants()

}