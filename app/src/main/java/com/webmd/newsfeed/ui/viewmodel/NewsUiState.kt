package com.webmd.newsfeed.ui.viewmodel

import com.webmd.newsfeed.domain.model.Article

/**
 * Represents the UI state for News screen
 * Using sealed class for explicit state representation
 */
sealed class NewsUiState {
    data object Initial : NewsUiState()
    
    data class Loading(
        val articles: List<Article> = emptyList()
    ) : NewsUiState()
    
    data class Success(
        val articles: List<Article>,
        val isGridView: Boolean = false
    ) : NewsUiState()
    
    data class Error(
        val message: String,
        val articles: List<Article> = emptyList(),
        val isGridView: Boolean = false
    ) : NewsUiState()
}
