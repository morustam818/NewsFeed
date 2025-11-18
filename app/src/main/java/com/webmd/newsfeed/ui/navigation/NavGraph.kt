package com.webmd.newsfeed.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.webmd.newsfeed.ui.screen.ArticleDetailScreen
import com.webmd.newsfeed.ui.screen.NewsFeedScreen

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
            NewsFeedScreen(
                onArticleClick = { article ->
                    val encodedUrl = Uri.encode(article.url)
                    navController.navigate("${Screen.ArticleDetail.route}/$encodedUrl")
                }
            )
        }

        composable(Screen.ArticleDetail.routeWithArgs) { backStackEntry ->
            val articleUrl = backStackEntry.arguments?.getString(Screen.ArticleDetail.ARTICLE_ARG)
            ArticleDetailScreen(
                articleUrl = articleUrl,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}