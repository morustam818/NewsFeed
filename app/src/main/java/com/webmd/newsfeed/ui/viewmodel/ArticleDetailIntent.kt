package com.webmd.newsfeed.ui.viewmodel

/**
 * Represents user intents/actions for Article Detail screen
 */
sealed class ArticleDetailIntent {
    data class LoadArticle(val url: String?) : ArticleDetailIntent()
    object ClearError : ArticleDetailIntent()
}
