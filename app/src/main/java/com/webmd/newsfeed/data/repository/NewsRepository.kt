package com.webmd.newsfeed.data.repository

import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Data layer implementation of the domain repository
 * Handles data source coordination (Room + Network)
 */
class NewsRepositoryImpl @Inject constructor(

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
