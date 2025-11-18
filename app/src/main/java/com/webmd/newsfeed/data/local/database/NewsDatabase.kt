package com.webmd.newsfeed.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.webmd.newsfeed.data.local.dao.ArticleDao
import com.webmd.newsfeed.data.local.entity.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}
