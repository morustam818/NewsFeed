package com.webmd.newsfeed.domain.usecase

import com.webmd.newsfeed.domain.repository.NewsRepository
import javax.inject.Inject

/**
 * Use case for refreshing top headlines from network
 */
class RefreshTopHeadlinesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshTopHeadlines()
    }
}
