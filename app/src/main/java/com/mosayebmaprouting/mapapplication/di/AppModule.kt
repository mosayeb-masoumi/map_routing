package com.mosayebmaprouting.mapapplication.di

import android.app.Application
import androidx.room.Room
import com.mosayebmaprouting.mapapplication.core.AppConstant
import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.LocationDatabase
import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.NeshanService
import com.mosayebmaprouting.mapapplication.features.locations.data.locations_repository_impl.LocationRepositoryImpl
import com.mosayebmaprouting.mapapplication.features.locations.data.neshan_repository_impl.NeshanDirectionRepositoryImpl
import com.mosayebmaprouting.mapapplication.features.locations.data.neshan_repository_impl.NeshanRepositoryImpl
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.LocationRepository
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.NeshanDirectionRepository
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.NeshanRepository
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
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*********************** database ***************************/
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
    fun provideUseCases(repository: LocationRepository): LocationUseCases {
        return LocationUseCases(
            addLocationUseCase = AddLocationUseCase(repository),
            clearDbUseCase = ClearDbUseCase(),
            deleteLocationUseCase = DeleteLocationUseCase(repository),
            getLocationUseCase = GetLocationUseCase(repository),
            getLocationsUseCase = GetLocationsUseCase(repository),
        )
    }


    /*********************** neshan api ***************************/
    @Provides
    @Singleton
    fun provideNeshanService(): NeshanService {
        val client = OkHttpClient.Builder()
            .build()
        return Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(NeshanService::class.java)
    }


    @Provides
    @Singleton
    fun provideNeshanRepository(service: NeshanService): NeshanRepository {
        return NeshanRepositoryImpl(service)
    }


    /*********************** Neshan routing ***********************/

//    @Provides
//    @Singleton
//    fun provideNeshanDirectionService(firstMarker: Marker, secondMarker: Marker): NeshanDirection {
//        return NeshanDirection.Builder(
//            AppConstant.NESHAN_API_KEY,
//            firstMarker.latLng,
//            secondMarker.latLng
//        ).build()
//    }

    @Provides
    @Singleton
    fun provideNeshanDirectionRepository(): NeshanDirectionRepository {
        return NeshanDirectionRepositoryImpl()
    }


}