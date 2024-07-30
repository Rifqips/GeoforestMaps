package id.application.core.data.local.database

import android.content.Context
import android.content.SyncResult
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.application.core.data.local.database.blocks.BlocksDao
import id.application.core.data.local.database.geotags.GeotagsDao
import id.application.core.data.local.database.plants.PlantsDao
import id.application.core.data.network.model.blocks.AllBlocks
import id.application.core.data.network.model.geotags.AllGeotaging
import id.application.core.data.network.model.plants.AllPlants
import id.application.core.domain.model.remotekeys.RemoteKeys

@Database(
    entities = [AllBlocks::class, AllGeotaging::class,AllPlants::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun blocksDao() : BlocksDao
    abstract fun geotagsDao() : GeotagsDao
    abstract fun plantsDao() : PlantsDao

    companion object {

        private var INSTANCE: ApplicationDatabase? = null

        fun getInstance(context : Context) : ApplicationDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApplicationDatabase::class.java,
                    "db_egeoforest"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}