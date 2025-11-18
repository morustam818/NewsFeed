package com.webmd.newsfeed.domain.repository

import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.utils.AppNetworkResult
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository interface - defines what the domain layer needs
 * Implementation is in the data layer
 */
interface NewsRepository {
    fun getTopHeadlines(): Flow<AppNetworkResult<List<Article>>>
    suspend fun getArticleByUrl(url: String): Article?
}
