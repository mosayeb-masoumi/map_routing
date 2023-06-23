package com.mosayebmap.mapapplication.di

import android.app.Application
import androidx.room.Room
import com.mosayebmap.mapapplication.features.locations.data.data_source.LocationDatabase
import com.mosayebmap.mapapplication.features.locations.data.locations_repository_impl.LocationRepositoryImpl
import com.mosayebmap.mapapplication.features.locations.domain.repository.LocationRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocationDatabase(app: Application): LocationDatabase {
        return Room.databaseBuilder(
            app,
            LocationDatabase::class.java,
            LocationDatabase.DATABASE_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: LocationDatabase): LocationRepository {
        return LocationRepositoryImpl(db.locationDao)
    }

}