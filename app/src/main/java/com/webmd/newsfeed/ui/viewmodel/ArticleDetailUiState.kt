package com.webmd.newsfeed.ui.viewmodel

import com.webmd.newsfeed.domain.model.Article

/**
 * Represents the UI state for Article Detail screen
 */
sealed class ArticleDetailUiState {
    data object Initial : ArticleDetailUiState()
    
    data object Loading : ArticleDetailUiState()
    
    data class Success(
        val article: Article
    ) : ArticleDetailUiState()
    
    data class Error(
        val message: String
    ) : ArticleDetailUiState()
}
