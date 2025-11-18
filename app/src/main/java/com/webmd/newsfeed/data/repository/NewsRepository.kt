package com.webmd.newsfeed.data.repository

import com.webmd.newsfeed.data.local.dao.ArticleDao
import com.webmd.newsfeed.data.remote.NewsApiService
import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Data layer implementation of the domain repository
 * Handles data source coordination (Room + Network)
 */
class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val articleDao: ArticleDao,
    private val apiKey: String
) : NewsRepository {
    override fun getTopHeadlines(): Flow<List<Article>> {
        TODO("Not yet implemented")
    }

    override suspend fun getArticleByUrl(url: String): Article? {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTopHeadlines(): Result<Unit> {
        TODO("Not yet implemented")
    }

}
