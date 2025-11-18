package com.webmd.newsfeed.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webmd.newsfeed.domain.usecase.GetTopHeadlinesUseCase
import com.webmd.newsfeed.utils.AppNetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.isNotEmpty

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Initial)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    // Channel for intents
    private val _intent = Channel<NewsIntent>(Channel.UNLIMITED)

    init {
        // Process intents
        processIntents()
        sendIntent(NewsIntent.LoadNews)
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
            is NewsIntent.LoadNews, NewsIntent.RefreshNews -> loadNews()
            is NewsIntent.ToggleViewMode -> toggleViewMode()
            is NewsIntent.ClearError -> clearError()
        }
    }

    private fun loadNews() {
        viewModelScope.launch {
            // Observe Flow from Room database (offline-first) via use case
            getTopHeadlinesUseCase().collect { result ->
                when(result){
                    is AppNetworkResult.Loading -> {
                        reduceState { NewsUiState.Loading() }
                    }
                    is AppNetworkResult.Failed -> {
                        reduceState {
                            NewsUiState.Success(result.data?:emptyList())
                        }
                    }
                    is AppNetworkResult.Success ->{
                        reduceState {
                            NewsUiState.Success(result.data?:emptyList())
                        }
                    }
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
