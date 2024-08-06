package id.application.core.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import id.application.core.data.local.database.blocks.BlocksDao
import id.application.core.data.local.database.geotags.GeotagsOfflineDao
import id.application.core.data.local.database.plants.PlantsDao
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.domain.model.geotags.ItemAllGeotagingOffline
import id.application.core.domain.model.plants.ItemAllPlants
import id.application.core.domain.model.remotekeys.RemoteKeys

@Database(
    entities = [ItemAllBlocks::class, ItemAllPlants::class, RemoteKeys::class, ItemAllGeotagingOffline::class],
    version = 2,
    exportSchema = false
)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun blocksDao(): BlocksDao
    abstract fun plantsDao(): PlantsDao
    abstract fun geotagsOfflineDao(): GeotagsOfflineDao

    companion object {

        private var INSTANCE: ApplicationDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Mengubah nama tabel lama
                db.execSQL("ALTER TABLE all_geotaging RENAME TO all_geotaging_two")
                db.execSQL("ALTER TABLE all_blocks RENAME TO all_blocks_two")
                db.execSQL("ALTER TABLE all_plants RENAME TO all_plants_two")
                db.execSQL("ALTER TABLE all_geotaging_offline RENAME TO all_geotaging_offline_two")

                // Membuat tabel baru untuk all_geotaging
                db.execSQL(
                    """
            CREATE TABLE all_geotaging (
                id INTEGER PRIMARY KEY NOT NULL,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL,
                altitude REAL NOT NULL,
                block TEXT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                photo TEXT NOT NULL,
                plant TEXT NOT NULL,
                user_id INTEGER NOT NULL
            )
            """
                )

                // Membuat tabel baru untuk all_blocks
                db.execSQL(
                    """
            CREATE TABLE all_blocks (
                id INTEGER PRIMARY KEY NOT NULL,
                created_at TEXT NOT NULL,
                name TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )
            """
                )

                // Membuat tabel baru untuk all_plants
                db.execSQL(
                    """
            CREATE TABLE all_plants (
                id INTEGER PRIMARY KEY NOT NULL,
                created_at TEXT NOT NULL,
                name TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )
            """
                )

                // Membuat tabel baru untuk all_geotaging_offline
                db.execSQL(
                    """
            CREATE TABLE all_geotaging_offline (
                id INTEGER PRIMARY KEY NOT NULL,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL,
                altitude REAL NOT NULL,
                block TEXT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                photo TEXT NOT NULL,
                plant TEXT NOT NULL,
                user_id INTEGER NOT NULL
            )
            """
                )

                // Menyalin data dari tabel lama ke tabel baru
                db.execSQL(
                    """
            INSERT INTO all_geotaging (id, created_at, updated_at, altitude, block, latitude, longitude, photo, plant, user_id)
            SELECT id, created_at, updated_at, altitude, block, latitude, longitude, photo, plant, user_id
            FROM all_geotaging_two
            """
                )

                db.execSQL(
                    """
            INSERT INTO all_blocks (id, created_at, name, updated_at)
            SELECT id, created_at, name, updated_at
            FROM all_blocks_two
            """
                )

                db.execSQL(
                    """
            INSERT INTO all_plants (id, created_at, name, updated_at)
            SELECT id, created_at, name, updated_at
            FROM all_plants_two
            """
                )

                // GeotagsOffline
                db.execSQL(
                    """
            INSERT INTO all_geotaging_offline (id, created_at, updated_at, altitude, block, latitude, longitude, photo, plant, user_id)
            SELECT id, created_at, updated_at, altitude, block, latitude, longitude, photo, plant, user_id
            FROM all_geotaging_offline_two
            """
                )

                // Menghapus tabel lama
                db.execSQL("DROP TABLE all_geotaging_two")
                db.execSQL("DROP TABLE all_blocks_two")
                db.execSQL("DROP TABLE all_plants_two")
                db.execSQL("DROP TABLE all_geotaging_offline_two")
            }
        }

        fun getInstance(context: Context): ApplicationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApplicationDatabase::class.java,
                    "db_egeoforest"
                )
                    .addMigrations(MIGRATION_1_2)
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