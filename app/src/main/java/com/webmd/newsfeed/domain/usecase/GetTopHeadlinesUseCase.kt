package com.webmd.newsfeed.domain.usecase

import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.domain.repository.NewsRepository
import com.webmd.newsfeed.utils.AppNetworkResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting top headlines
 * Encapsulates business logic and follows single responsibility principle
 */
class GetTopHeadlinesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(): Flow<AppNetworkResult<List<Article>>> {
        return repository.getTopHeadlines()
    }
}

