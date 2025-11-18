package com.webmd.newsfeed.ui.viewmodel

/**
 * Represents user intents/actions for News screen
 */
sealed class NewsIntent {
    object LoadNews : NewsIntent()
    object RefreshNews : NewsIntent()
    object ClearError : NewsIntent()
}
