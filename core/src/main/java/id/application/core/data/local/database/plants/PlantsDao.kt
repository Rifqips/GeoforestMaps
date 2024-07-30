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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKeyPlants(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysIdPlants(id:String): RemoteKeys?

    @Query("SELECT * FROM all_plants")
    fun retrieveAllplants(): PagingSource<Int, ItemAllPlants>

    @Query("DELETE FROM all_plants")
    suspend fun deleteAllPlants()

}