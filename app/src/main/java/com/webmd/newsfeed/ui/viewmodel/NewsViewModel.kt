package com.webmd.newsfeed.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webmd.newsfeed.domain.usecase.GetTopHeadlinesUseCase
import com.webmd.newsfeed.domain.usecase.RefreshTopHeadlinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.isNotEmpty
import kotlin.onFailure
import kotlin.onSuccess

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val refreshTopHeadlinesUseCase: RefreshTopHeadlinesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Initial)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    // Channel for intents
    private val _intent = Channel<NewsIntent>(Channel.UNLIMITED)

    init {
        // Process intents
        processIntents()
        // Initial load
        sendIntent(NewsIntent.LoadNews)
        sendIntent(NewsIntent.RefreshNews)
    }

    /**
     * Send intent to ViewModel
     * This is the only public method exposed to View for any UI Action
     */
    fun sendIntent(intent: NewsIntent) {
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

    private fun handleIntent(intent: NewsIntent) {
        when (intent) {
            is NewsIntent.LoadNews -> loadNews()
            is NewsIntent.RefreshNews -> refreshNews()
            is NewsIntent.ToggleViewMode -> toggleViewMode()
            is NewsIntent.ClearError -> clearError()
        }
    }

    private fun loadNews() {
        viewModelScope.launch {
            // Observe Flow from Room database (offline-first) via use case
            getTopHeadlinesUseCase()
                .catch { exception ->
                    reduceState { currentState ->
                        when (currentState) {
                            is NewsUiState.Success -> NewsUiState.Error(
                                message = exception.message ?: "Unknown error occurred",
                                articles = currentState.articles,
                                isGridView = currentState.isGridView
                            )
                            is NewsUiState.Error -> currentState.copy(
                                message = exception.message ?: "Unknown error occurred"
                            )
                            else -> NewsUiState.Error(
                                message = exception.message ?: "Unknown error occurred"
                            )
                        }
                    }
                }
                .collect { articles ->
                    reduceState { currentState ->
                        NewsUiState.Success(
                            articles = articles,
                            isGridView = when (currentState) {
                                is NewsUiState.Success -> currentState.isGridView
                                is NewsUiState.Error -> currentState.isGridView
                                else -> false
                            }
                        )
                    }
                }
        }
    }

    private fun refreshNews() {
        viewModelScope.launch {
            reduceState { currentState ->
                when (currentState) {
                    is NewsUiState.Success -> NewsUiState.Loading(
                        articles = currentState.articles
                    )
                    is NewsUiState.Error -> NewsUiState.Loading(
                        articles = currentState.articles
                    )
                    else -> NewsUiState.Loading()
                }
            }
            
            refreshTopHeadlinesUseCase()
                .onSuccess {
                    // Data will be automatically updated via Flow from Room
                    // State will be updated by loadNews() Flow collection
                }
                .onFailure { exception ->
                    reduceState { currentState ->
                        val articles = when (currentState) {
                            is NewsUiState.Loading -> currentState.articles
                            is NewsUiState.Success -> currentState.articles
                            is NewsUiState.Error -> currentState.articles
                            else -> emptyList()
                        }
                        val isGridView = when (currentState) {
                            is NewsUiState.Success -> currentState.isGridView
                            is NewsUiState.Error -> currentState.isGridView
                            else -> false
                        }
                        NewsUiState.Error(
                            message = exception.message ?: "Failed to refresh news",
                            articles = articles,
                            isGridView = isGridView
                        )
                    }
                }
        }
    }

    private fun toggleViewMode() {
        reduceState { currentState ->
            when (currentState) {
                is NewsUiState.Success -> currentState.copy(
                    isGridView = !currentState.isGridView
                )
                is NewsUiState.Error -> currentState.copy(
                    isGridView = !currentState.isGridView
                )
                else -> currentState
            }
        }
    }

    private fun clearError() {
        reduceState { currentState ->
            when (currentState) {
                is NewsUiState.Error -> {
                    if (currentState.articles.isNotEmpty()) {
                        NewsUiState.Success(
                            articles = currentState.articles,
                            isGridView = currentState.isGridView
                        )
                    } else {
                        NewsUiState.Initial
                    }
                }
                else -> currentState
            }
        }
    }

    /**
     * Reducer function - that takes current state and returns new state
     */
    private fun reduceState(reducer: (NewsUiState) -> NewsUiState) {
        _uiState.value = reducer(_uiState.value)
    }
}
