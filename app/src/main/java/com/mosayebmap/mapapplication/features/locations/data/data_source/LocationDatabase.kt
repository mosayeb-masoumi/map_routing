package com.mosayebmap.mapapplication.features.locations.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mosayebmap.mapapplication.features.locations.domain.model.LocationModel

@Database(
    entities = [LocationModel::class],
    version = 1
//    , autoMigrations = [AutoMigration(from = 1 , to = 2)]   // to migrate automatically
)
abstract class LocationDatabase : RoomDatabase(){

    abstract val locationDao: LocationDao

    companion object {
        const val DATABASE_NAME = "location_db"


        // to migrate manually
//        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // The following query will add a new column called lastUpdate to the notes database
//                database.execSQL("ALTER TABLE notes ADD COLUMN lastUpdate INTEGER NOT NULL DEFAULT 0")
//            }
//        }
    }
}