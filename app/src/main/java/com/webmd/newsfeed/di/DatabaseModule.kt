package com.webmd.newsfeed.di

import android.content.Context
import androidx.room.Room
import com.webmd.newsfeed.BuildConfig
import com.webmd.newsfeed.data.local.database.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNewsDatabase(
        @ApplicationContext context: Context
    ): NewsDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = NewsDatabase::class.java,
            name = BuildConfig.DB_NAME
        ).build()
    }
}
