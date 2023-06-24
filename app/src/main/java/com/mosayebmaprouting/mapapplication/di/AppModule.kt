package com.mosayebmaprouting.mapapplication.di

import android.app.Application
import androidx.room.Room
import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.LocationDatabase
import com.mosayebmaprouting.mapapplication.features.locations.data.locations_repository_impl.LocationRepositoryImpl
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.LocationRepository
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.AddLocationUseCase
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.ClearDbUseCase
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.DeleteLocationUseCase
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.GetLocationUseCase
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.GetLocationsUseCase
import com.mosayebmaprouting.mapapplication.features.locations.domain.use_case.LocationUseCases

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


    @Provides
    @Singleton
    fun provideUseCases(repository: LocationRepository): LocationUseCases{
        return  LocationUseCases(
            addLocationUseCase = AddLocationUseCase(repository),
            clearDbUseCase= ClearDbUseCase(),
            deleteLocationUseCase = DeleteLocationUseCase(repository),
            getLocationUseCase = GetLocationUseCase(repository),
            getLocationsUseCase = GetLocationsUseCase(repository),
        )
    }
}