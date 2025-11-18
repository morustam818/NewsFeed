package com.webmd.newsfeed.data.repository

import com.webmd.newsfeed.data.local.dao.ArticleDao
import com.webmd.newsfeed.data.mapper.ArticleMapper
import com.webmd.newsfeed.data.remote.NewsApiService
import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        return articleDao.getAllArticles().map { entities ->
            ArticleMapper.toDomainListFromEntity(entities)
        }
    }

    override suspend fun getArticleByUrl(url: String): Article? {
        val entity = articleDao.getArticleByUrl(url)
        return entity?.let { ArticleMapper.toDomainFromEntity(it) }
    }

    override suspend fun refreshTopHeadlines(): Result<Unit> {
        return try {
            // Fetch from network (returns DTOs)
            val response = newsApiService.getTopHeadlines(apiKey = apiKey)
            if (response.status == "ok" && response.articles != null) {
                // Convert DTOs to Domain models, then to Entities
                val domainArticles = ArticleMapper.toDomainListFromDto(response.articles)
                val entities = ArticleMapper.toEntityList(domainArticles)
                articleDao.insertArticles(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch news: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
