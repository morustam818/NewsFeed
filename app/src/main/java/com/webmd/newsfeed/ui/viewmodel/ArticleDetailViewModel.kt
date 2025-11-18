package com.webmd.newsfeed.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webmd.newsfeed.domain.usecase.GetArticleByUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val getArticleByUrlUseCase: GetArticleByUrlUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticleDetailUiState>(ArticleDetailUiState.Initial)
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    // Channel for intents
    private val _intent = Channel<ArticleDetailIntent>(Channel.UNLIMITED)

    init {
        // Process intents
        processIntents()
    }

    /**
     * Send intent to ViewModel
     * This is the only public method exposed to View for any UI Action
     */
    fun sendIntent(intent: ArticleDetailIntent) {
        viewModelScope.launch {
            _intent.send(intent)
        }
    }

    private fun processIntents() {
        viewModelScope.launch {
            _intent.receiveAsFlow().collect { intent ->
                handleIntent(intent)
            }
        }
    }

    private fun handleIntent(intent: ArticleDetailIntent) {
        when (intent) {
            is ArticleDetailIntent.LoadArticle -> loadArticle(intent.url)
            is ArticleDetailIntent.ClearError -> clearError()
        }
    }

    private fun loadArticle(url: String?) {
        if (url == null) {
            reduceState {
                ArticleDetailUiState.Error("Article URL is missing")
            }
            return
        }

        viewModelScope.launch {
            reduceState { ArticleDetailUiState.Loading }

            try {
                val article = getArticleByUrlUseCase(url)
                if (article != null) {
                    reduceState { ArticleDetailUiState.Success(article) }
                } else {
                    reduceState { ArticleDetailUiState.Error("Article not found") }
                }
            } catch (e: Exception) {
                reduceState {
                    ArticleDetailUiState.Error(e.message ?: "Failed to load article")
                }
            }
        }
    }

    private fun clearError() {
        reduceState { ArticleDetailUiState.Initial }
    }

    /**
     * Reducer function - that takes current state and returns new state
     */
    private fun reduceState(reducer: (ArticleDetailUiState) -> ArticleDetailUiState) {
        _uiState.value = reducer(_uiState.value)
    }
}
