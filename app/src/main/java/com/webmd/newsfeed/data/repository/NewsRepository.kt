package com.webmd.newsfeed.data.repository

import androidx.room.withTransaction
import com.webmd.newsfeed.data.local.database.NewsDatabase
import com.webmd.newsfeed.data.mapper.toArticle
import com.webmd.newsfeed.data.mapper.toArticleEntity
import com.webmd.newsfeed.data.mapper.toArticleList
import com.webmd.newsfeed.data.remote.NewsApiService
import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.domain.repository.NewsRepository
import com.webmd.newsfeed.utils.AppConstant
import com.webmd.newsfeed.utils.networkBoundResource
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

/**
 * Data layer implementation of the domain repository
 * Handles data source coordination (Room + Network)
 */
class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val db : NewsDatabase,
    @param:Named(AppConstant.API_KEY) private val apiKey: String
) : NewsRepository {
    override fun getTopHeadlines() = networkBoundResource(
        query = {
            db.articleDao.getAllArticles().map {
                it.toArticleList()
            }
        },
        fetch = {
            newsApiService.getTopHeadlines(apiKey = apiKey)
        },
        saveFetchResult = {
            db.withTransaction {
                with(db.articleDao){
                    it.body()?.articles?.let { result ->
                        val entities = result.toArticleEntity()
                        db.articleDao.insertArticles(entities)
                    }
                }
            }
        }
    )

    override suspend fun getArticleByUrl(url: String): Article? {
        val entity = db.articleDao.getArticleByUrl(url)
        return entity?.let { entity.toArticle() }
    }
}
