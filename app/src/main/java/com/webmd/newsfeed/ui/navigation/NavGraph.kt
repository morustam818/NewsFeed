package com.webmd.newsfeed.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object NewsFeed : Screen("news_feed")
    object ArticleDetail : Screen("article_detail") {
        const val ARTICLE_ARG = "article"
        val routeWithArgs = "$route/{$ARTICLE_ARG}"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.NewsFeed.route
    ) {
        composable(Screen.NewsFeed.route) {

        }

        composable(Screen.ArticleDetail.routeWithArgs) { backStackEntry ->
            val articleUrl = backStackEntry.arguments?.getString(Screen.ArticleDetail.ARTICLE_ARG)

        }
    }
}