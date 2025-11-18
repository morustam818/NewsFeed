package com.webmd.newsfeed.domain.usecase

import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.domain.repository.NewsRepository
import javax.inject.Inject

/**
 * Use case for getting a single article by URL
 */
class GetArticleByUrlUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(url: String): Article? {
        return repository.getArticleByUrl(url)
    }
}

