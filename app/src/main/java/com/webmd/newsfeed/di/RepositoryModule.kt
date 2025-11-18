package com.webmd.newsfeed.di

import com.webmd.newsfeed.data.local.dao.ArticleDao
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
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository

    companion object {
        @Provides
        @Singleton
        fun provideNewsRepositoryImpl(
            newsApiService: NewsApiService,
            articleDao: ArticleDao,
            @Named(AppConstant.API_KEY) apiKey: String
        ): NewsRepositoryImpl {
            return NewsRepositoryImpl(newsApiService, articleDao, apiKey)
        }
    }
}
