package com.webmd.newsfeed.di

import com.webmd.newsfeed.data.local.database.NewsDatabase
import com.webmd.newsfeed.data.remote.NewsApiService
import com.webmd.newsfeed.data.repository.NewsRepositoryImpl
import com.webmd.newsfeed.domain.repository.NewsRepository
import com.webmd.newsfeed.utils.AppConstant
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun providesNewsRepository(
        db: NewsDatabase,
        apiService: NewsApiService,
        @Named(AppConstant.API_KEY) apikey: String,
    ): NewsRepository {
        return NewsRepositoryImpl(apiService, db, apikey)
    }
}
