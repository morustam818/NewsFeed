package com.webmd.newsfeed.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.webmd.newsfeed.domain.model.Article
import com.webmd.newsfeed.ui.component.ArticleItem
import com.webmd.newsfeed.ui.viewmodel.NewsIntent
import com.webmd.newsfeed.ui.viewmodel.NewsUiState
import com.webmd.newsfeed.ui.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Feed") },
                actions = {
                    val isGridView = when (val state = uiState) {
                        is NewsUiState.Success -> state.isGridView
                        is NewsUiState.Error -> state.isGridView
                        else -> false
                    }
                    IconButton(onClick = { viewModel.sendIntent(NewsIntent.ToggleViewMode) }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.List else Icons.Default.List,
                            contentDescription = "Toggle view"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is NewsUiState.Initial -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }

                is NewsUiState.Loading -> {
                    if (state.articles.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize()
                        )
                    } else {
                        ArticleListContent(
                            articles = state.articles,
                            isGridView = false,
                            onArticleClick = onArticleClick
                        )
                    }
                }

                is NewsUiState.Success -> {
                    if (state.articles.isEmpty()) {
                        EmptyState(
                            message = "No articles available",
                            onRetry = { viewModel.sendIntent(NewsIntent.RefreshNews) }
                        )
                    } else {
                        ArticleListContent(
                            articles = state.articles,
                            isGridView = state.isGridView,
                            onArticleClick = onArticleClick
                        )
                    }
                }

                is NewsUiState.Error -> {
                    if (state.articles.isEmpty()) {
                        ErrorMessage(
                            message = state.message,
                            onRetry = { viewModel.sendIntent(NewsIntent.RefreshNews) }
                        )
                    } else {
                        // Show articles with error banner
                        Column {
                            ErrorBanner(
                                message = state.message,
                                onDismiss = { viewModel.sendIntent(NewsIntent.ClearError) }
                            )
                            ArticleListContent(
                                articles = state.articles,
                                isGridView = state.isGridView,
                                onArticleClick = onArticleClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleListContent(
    articles: List<Article>,
    isGridView: Boolean,
    onArticleClick: (Article) -> Unit
) {
    if (isGridView) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(articles) { article ->
                ArticleItem(
                    article = article,
                    isGridView = true,
                    onClick = { onArticleClick(article) }
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(articles) { article ->
                ArticleItem(
                    article = article,
                    isGridView = false,
                    onClick = { onArticleClick(article) }
                )
            }
        }
    }
}

@Composable
fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

