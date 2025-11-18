package com.webmd.newsfeed.ui.viewmodel

import com.webmd.newsfeed.domain.model.Article

/**
 * Represents the UI state for News screen
 * Using sealed class for explicit state representation
 */
sealed class NewsUiState {
    open val articles: List<Article> = emptyList()

    data object Initial : NewsUiState()
    
    data class Loading(
        override val articles: List<Article> = emptyList()
    ) : NewsUiState()
    
    data class Success(
        override val articles: List<Article>
    ) : NewsUiState()
    
    data class Error(
        override val articles: List<Article> = emptyList(),
        val message: String
    ) : NewsUiState()
}
